package model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Job extends BaseModel {
    private String name;
    private String url;

    public String getName() {
        return this.name;
    }
    public String getUrl() {
        return this.url;
    }
}

