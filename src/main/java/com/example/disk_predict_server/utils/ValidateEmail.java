package com.example.disk_predict_server.utils;

import java.util.regex.*;
public class ValidateEmail {
    private static final String EMAIL_REGEX =
            "^(?=.*[a-zA-Z0-9._%+-])" +
                    "(?=.*@(gmail\\.com|outlook\\.com|icloud\\.com|yahoo\\.com))" +
                    "[a-zA-Z0-9._%+-]+@(gmail\\.com|outlook\\.com|icloud\\.com|yahoo\\.com)$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}