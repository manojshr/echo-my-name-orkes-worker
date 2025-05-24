package com.codlibs.utils;

import java.util.Optional;
import java.util.function.Predicate;

public class EnvUtil {

    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key))
                .filter(Predicate.not(String::isBlank))
                .orElse(defaultValue);
    }

    public static String get(String key) {
        return Optional.ofNullable(System.getenv(key))
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Environment variable %s is not present".formatted(key)));
    }
}
