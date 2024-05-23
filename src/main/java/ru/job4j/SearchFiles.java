package ru.job4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SearchFiles extends SimpleFileVisitor<Path> {
    private final Predicate<Path> condition;
    private final List<Path> result = new ArrayList<>();

    public SearchFiles(Predicate<Path> condition) {
        this.condition = condition;
    }

    public List<Path> getResult() {
        return result;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (condition.test(file)) {
            result.add(file);
        }
        return FileVisitResult.CONTINUE;
    }
}
