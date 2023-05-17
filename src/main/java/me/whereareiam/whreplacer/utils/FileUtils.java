package me.whereareiam.whreplacer.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static String readFileContent(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    public static void writeFileContent(Path filePath, String content) throws IOException {
        Files.writeString(filePath, content, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
