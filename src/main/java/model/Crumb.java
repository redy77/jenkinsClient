package model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Crumb extends BaseModel {
    private String crumbRequestField;
    private String crumb;

    public String getCrumbRequestField() {
        return this.crumbRequestField;
    }

    public String getCrumb() {
        return this.crumb;
    }
}
