package ru.job4j;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String getValue(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Must be four arguments");
        }
        for (String str : args) {
            if (!str.contains("=")) {
                throw new IllegalArgumentException(String.format("Error: this argument '%s' does not contain an equal sign", str));
            }
            String[] stringArg = str.split("=");
            if (stringArg[0].charAt(0) != '-') {
                throw new IllegalArgumentException(String.format("Error: this argument '%s' does not contain symbol '-'", str));
            }
            stringArg[0] = stringArg[0].replace("-", "");
            if (stringArg[0].isEmpty()) {
                throw new IllegalArgumentException(String.format("Error: this argument '%s' does not contain key", str));
            }
            if (stringArg[1].isEmpty()) {
                throw new IllegalArgumentException(String.format("Error: this argument '%s' does not contain value", str));
            }
            values.put(stringArg[0], stringArg[1]);
        }
    }

    public static ArgsName of(String[] args) {
        ArgsName argsName = new ArgsName();
        argsName.parse(args);
        return argsName;
    }
}
