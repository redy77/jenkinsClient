package model.utils;

import model.Job;

import java.net.URI;

public class UrlUtils {

    private UrlUtils() {
    }

    public static String toBaseUrl(Job folder) {
        return folder == null ? "/" : folder.getUrl();
    }

    public static String toJobBaseUrl(Job folder, String jobName) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(toBaseUrl(folder));
        if (sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }

        sb.append("job/");
        String[] jobNameParts = jobName.split("/");

        for(int i = 0; i < jobNameParts.length; ++i) {
            sb.append(EncodingUtils.encode(jobNameParts[i]));
            if (i != jobNameParts.length - 1) {
                sb.append('/');
            }
        }

        return sb.toString();
    }

    public static String toFullJobPath(String jobName) {
        String[] parts = jobName.split("/");
        if (parts.length == 1) {
            return parts[0];
        } else {
            StringBuilder sb = new StringBuilder(64);

            for(int i = 0; i < parts.length; ++i) {
                sb.append(parts[i]);
                if (i != parts.length - 1) {
                    sb.append("/job/");
                }
            }

            return sb.toString();
        }
    }

    public static String join(String path1, String path2) {
        if (path1.isEmpty() && path2.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(64);
            sb.append(path1);
            if (sb.charAt(sb.length() - 1) == '/') {
                sb.setLength(sb.length() - 1);
            }

            if (path2.charAt(0) != '/') {
                sb.append('/');
            }

            sb.append(path2);
            return sb.toString();
        }
    }

    public static URI toJsonApiUri(URI uri, String context, String path) {
        String p = path;
        if (!path.matches("(?i)https?://.*")) {
            p = join(context, path);
        }

        if (!p.contains("?")) {
            p = join(p, "api/json");
        } else {
            String[] components = p.split("\\?", 2);
            p = join(components[0], "api/json") + "?" + components[1];
        }

        return uri.resolve("/").resolve(p.replace(" ", "%20"));
    }
}
