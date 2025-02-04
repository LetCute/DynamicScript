package com.letcute;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    public static List<File> getAllJavaFiles(File folder) {
        List<File> javaFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return javaFiles;
    }

    public static List<File> getAllClassFiles(File folder) {
        List<File> classFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            classFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".class"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;
    }

    public static String getClassNameFromFile(File classFile, File rootFolder) {
        String absolutePath = classFile.getAbsolutePath();
        String rootPath = rootFolder.getAbsolutePath();

        if (!absolutePath.startsWith(rootPath)) {
            throw new IllegalArgumentException("Class file is not inside the root folder.");
        }

        String relativePath = absolutePath.substring(rootPath.length() + 1);

        return relativePath.replace(File.separatorChar, '.').replace(".class", "");
    }
}
