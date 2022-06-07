package model.utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

public class Response {
    private Response() {
    }

    public static String getJenkinsVersion(HttpResponse response) {
        Header[] hdrs = response.getHeaders("X-Jenkins");
        return hdrs.length == 0 ? "" : hdrs[0].getValue();
    }
}
