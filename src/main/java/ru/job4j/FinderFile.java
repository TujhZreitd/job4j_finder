package ru.job4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinderFile {

    public static void validateArgs(ArgsName args) {
        Path fileStart = Path.of(args.getValue("d"));
        if (!Files.exists(fileStart)) {
            throw new IllegalArgumentException(String.format("Not exists %s", fileStart.toAbsolutePath()));
        }
        if (!Files.isDirectory(fileStart)) {
            throw new IllegalArgumentException(String.format("Not directory %s", fileStart.toAbsolutePath()));
        }
        String name = args.getValue("n");
        if (!name.contains(".")) {
            throw new IllegalArgumentException(String.format("Incorrect name file %s", name));
        }
        if (name.endsWith(".")) {
            throw new IllegalArgumentException(String.format("Incorrect name file %s - absent file extension", name));
        }
        String typeSearch = args.getValue("t");
        if (!("mask".equals(typeSearch) || "name".equals(typeSearch) || "regex".equals(typeSearch))) {
            throw new IllegalArgumentException(String.format("Incorrect type search %s", typeSearch));
        }
        Path fileResult = Path.of(args.getValue("o"));
        if (!Files.isRegularFile(fileResult)) {
            throw new IllegalArgumentException(String.format("Not file %s", args.getValue("o")));
        }
    }

    public static List<Path> searchFile(Path start, Predicate<Path> condition) throws IOException {
        SearchFiles searchFiles = new SearchFiles(condition);
        Files.walkFileTree(start, searchFiles);
        return searchFiles.getResult();
    }

    public static Predicate<Path> predicate(String typeSearch, String name) {
        Predicate<Path> condition = null;
        if ("name".equals(typeSearch)) {
            condition = path -> path.toFile().getName().equals(name);
        }
        if ("mask".equals(typeSearch)) {
            condition = path -> {
                String regex = name.replace(".", "\\.");
                if (regex.contains("*")) {
                    regex = regex.replace("*", "\\w+");
                }
                if (regex.contains("?")) {
                    regex = regex.replace("?", "\\w{1}");
                }
                Pattern pattern = Pattern.compile(regex);
                String text = String.valueOf(path.getFileName());
                Matcher matcher = pattern.matcher(text);
                return matcher.matches();
            };
        }
        if ("regex".equals(typeSearch)) {
            condition = path -> {
                Pattern pattern = Pattern.compile(name);
                String text = String.valueOf(path.getFileName());
                Matcher matcher = pattern.matcher(text);
                return matcher.matches();
            };
        }
        return condition;
    }

    public static void writeRusult(List<Path> result, String path) {
        try (BufferedWriter writer = new BufferedWriter(new PrintWriter(path))) {
            for (Path pathName : result) {
                writer.write(String.valueOf(pathName));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ArgsName argsName = ArgsName.of(args);
        validateArgs(argsName);
        Predicate<Path> condition = predicate(argsName.getValue("t"), argsName.getValue("n"));
        List<Path> result = searchFile(Path.of(argsName.getValue("d")), condition);
        writeRusult(result, argsName.getValue("o"));
    }
}
/**/

