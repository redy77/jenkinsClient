import lombok.ToString;
import model.utils.JenkinsHttpClient;
import model.utils.JenkinsHttpConnection;
import model.Job;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.utils.UrlUtils;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

@ToString
public class JenkinsServer implements Closeable {

    private final Logger LOGGER;
    private final JenkinsHttpConnection client;

    public JenkinsServer(URI serverUri, String username, String passwordOrToken) {
        this(new JenkinsHttpClient(serverUri, username, passwordOrToken));
    }

    public JenkinsServer(JenkinsHttpConnection client) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.client = client;
    }

    public Job getJob(String jobName) throws IOException {
        return this.getJob(null, UrlUtils.toFullJobPath(jobName));
    }

    public Job getJob(Job folder, String jobName) throws IOException {
        try {
            Job job = this.client.get(UrlUtils.toJobBaseUrl(folder, jobName), Job.class);
            job.setClient(this.client);
            return job;
        } catch (HttpResponseException var4) {
            this.LOGGER.debug("getJob(folder={}, jobName={}) status={}", folder, jobName, var4.getStatusCode());
            if (var4.getStatusCode() == 404) {
                return null;
            } else {
                throw var4;
            }
        }
    }

    public String runScript(String script) throws IOException {
        return this.runScript(script, false);
    }

    public String runScript(String script, boolean crumbFlag) throws IOException {
        return this.client.post_text("/scriptText", "script=" + script, ContentType.APPLICATION_FORM_URLENCODED, crumbFlag);
    }

    public void close() {
        this.client.close();
    }
}
