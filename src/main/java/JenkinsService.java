import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@ToString
@Getter
public class JenkinsService {

    private String script;

    private final RestAssist restAssist;

    public JenkinsService(RestAssist restAssist) {
        this.restAssist = restAssist;
    }


    public String getURLSubFolder(String org, String project) {
        try (JenkinsServer jenkinsServer = restAssist.postSubUrl(org)) {
            return jenkinsServer.getJob(project).getUrl().toLowerCase();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
        return null;
    }

    public void createSubFolder(String org, String project) {
        String forLog;
        ClassPathResource resource = new ClassPathResource("createSubFolder.txt");
        try (JenkinsServer jenkinsServer = restAssist.postUrl()){
            InputStream inputStream = resource.getInputStream();
            script = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            forLog = jenkinsServer.runScript(script.replace("folderNameTemp", org).replace("jobNameTemp", project));
            log.info("{}", forLog);
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
        }
    }
}