package model.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import lombok.ToString;
import model.BaseModel;
import model.Crumb;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@ToString
public class JenkinsHttpClient implements JenkinsHttpConnection {
    private final Logger LOGGER;
    private final URI uri;
    private final CloseableHttpClient client;
    private HttpContext localContext;
    private final HttpResponseValidator httpResponseValidator;
    private final ObjectMapper mapper;
    private String context;
    private String jenkinsVersion;

    public JenkinsHttpClient(URI uri, CloseableHttpClient client) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.context = uri.getPath();
        if (!this.context.endsWith("/")) {
            this.context = this.context + "/";
        }
        this.uri = uri;
        this.mapper = this.getDefaultMapper();
        this.client = client;
        this.httpResponseValidator = new HttpResponseValidator();
        this.jenkinsVersion = "UNKNOWN";
        this.LOGGER.debug("uri={}", uri);
    }

    public JenkinsHttpClient(URI uri, HttpClientBuilder builder) {
        this(uri, builder.build());
    }

    public JenkinsHttpClient(URI uri, String username, String password) {
        this(uri, HttpClientBuilder.create(), username, password);
    }

    public JenkinsHttpClient(URI uri, HttpClientBuilder builder, String username, String password) {
        this(uri, addAuthentication(builder, uri, username, password));
        if (StringUtils.isNotBlank(username)) {
            this.localContext = new BasicHttpContext();
            this.localContext.setAttribute("preemptive-auth", new BasicScheme());
        }

    }

    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException {
        HttpGet getMethod = new HttpGet(UrlUtils.toJsonApiUri(this.uri, this.context, path));
        HttpResponse response = this.client.execute(getMethod, this.localContext);

        BaseModel var5;
        try {
            this.httpResponseValidator.validateResponse(response);
            var5 = this.objectFromResponse(cls, response);
        } finally {
            EntityUtils.consume(response.getEntity());
            this.releaseConnection(getMethod);
        }
        return (T) var5;
    }

    public String post_text(String path, String textData, ContentType contentType, boolean crumbFlag) throws IOException {
        HttpPost request = new HttpPost(UrlUtils.toJsonApiUri(this.uri, this.context, path));
        if (crumbFlag) {
            Crumb crumb = this.get("/crumbIssuer", Crumb.class);
            if (crumb != null) {
                request.addHeader(new BasicHeader(crumb.getCrumbRequestField(), crumb.getCrumb()));
            }
        }

        if (textData != null) {
            request.setEntity(new StringEntity(textData, contentType));
        }

        HttpResponse response = this.client.execute(request, this.localContext);
        this.jenkinsVersion = Response.getJenkinsVersion(response);

        String var7;
        try {
            this.httpResponseValidator.validateResponse(response);
            var7 = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        } finally {
            EntityUtils.consume(response.getEntity());
            this.releaseConnection(request);
        }

        return var7;
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException var2) {
            this.LOGGER.debug("I/O exception closing client", var2);
        }

    }

    protected static HttpClientBuilder addAuthentication(HttpClientBuilder builder, URI uri, String username, String password) {
        if (StringUtils.isNotBlank(username)) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            AuthScope scope = new AuthScope(uri.getHost(), uri.getPort(), "realm");
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(scope, credentials);
            builder.setDefaultCredentialsProvider(provider);
            builder.addInterceptorFirst(new PreemptiveAuth());
        }

        return builder;
    }

    private <T extends BaseModel> BaseModel objectFromResponse(Class<T> cls, HttpResponse response) throws IOException {
        InputStream content = response.getEntity().getContent();
        byte[] bytes = ByteStreams.toByteArray(content);
        BaseModel result = this.mapper.readValue(bytes, cls);
        result.setClient(this);
        return result;
    }

    private ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    private void releaseConnection(HttpRequestBase httpRequestBase) {
        httpRequestBase.releaseConnection();
    }
}