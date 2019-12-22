package org.ele.springboot.parallel.web;

public class Utils {
    public static String URL_TPL = "http://localhost:8080/legacy/slow/%d";
    public static String combineContent(String content1, String content2) {
        return String.format("%s|%s", content1, content2);
    }
}
