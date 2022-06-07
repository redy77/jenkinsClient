import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class RestAssist {

    private String jenkinsURL;
    private String login;
    private String token;

    public JenkinsServer postSubUrl(String org) {
        try {
            String uri = (jenkinsURL + "job/" + org);
            return new JenkinsServer(new URI(uri), login, token);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public JenkinsServer postUrl() {
        try {
            String uri = (jenkinsURL);
            return new JenkinsServer(new URI(uri), login, token);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}

