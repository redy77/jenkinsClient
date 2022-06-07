package model.utils;

import model.BaseModel;
import org.apache.http.entity.ContentType;

import java.io.Closeable;
import java.io.IOException;

public interface JenkinsHttpConnection extends Closeable {

    void close();

    <T extends BaseModel> T get(String var1, Class<T> var2) throws IOException;


    String post_text(String var1, String var2, ContentType var3, boolean var4) throws IOException;

}
