package model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.utils.JenkinsHttpConnection;

@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
    protected JenkinsHttpConnection client;

    public void setClient(JenkinsHttpConnection client) {
        this.client = client;
    }
}
