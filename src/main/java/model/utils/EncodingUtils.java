package model.utils;

import com.google.common.net.UrlEscapers;

public class EncodingUtils {

    private EncodingUtils() {
    }

    public static String encode(String pathPart) {
        return UrlEscapers.urlPathSegmentEscaper().escape(pathPart);
    }
}
