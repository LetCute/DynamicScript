package com.letcute;

import javax.tools.*;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ScriptCompiler {

    private final DynamicScriptLoader plugin;

    public ScriptCompiler(DynamicScriptLoader plugin) {
        this.plugin = plugin;
    }

    public void compileScripts(File scriptsFolder, File compiledFolder) {
        List<File> javaFiles = FileUtil.getAllJavaFiles(scriptsFolder);
        if (javaFiles.isEmpty()) {
            plugin.getLogger().info("No Java files found in the scripts folder.");
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            plugin.getLogger().severe("Java compiler not found. Please run the plugin using JDK.");
            return;
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(compiledFolder.getAbsolutePath());

            String classpath = getFullClasspath();
            options.add("-classpath");
            options.add(classpath);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null,
                    compilationUnits);
            boolean success = task.call();
            if (success) {
                plugin.getLogger().info("Scripts compiled successfully!");
            } else {
                plugin.getLogger().severe("Script compilation failed.");
            }
        } finally {
            try {
                fileManager.close();
            } catch (IOException e) {
                plugin.getLogger().severe("Error closing file manager: " + e.getMessage());
            }
        }
    }

    private String getFullClasspath() {
        StringBuilder cp = new StringBuilder();
        cp.append(System.getProperty("java.class.path"));

        try {
            File bukkitJar = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            cp.append(File.pathSeparator).append(bukkitJar.getAbsolutePath());

            File pluginJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            cp.append(File.pathSeparator).append(pluginJar.getAbsolutePath());

            File pluginsFolder = new File("plugins");
            addJarsFromFolder(pluginsFolder, cp);

            File dataFolder = plugin.getDataFolder();
            File jarFolder = new File(dataFolder, "jarFolder");
            addJarsFromFolder(jarFolder, cp);
            addJarsToClassLoader(jarFolder);
        } catch (Exception e) {
            plugin.getLogger().severe("Error getting JAR file path: " + e.getMessage());
        }

        return cp.toString();
    }

    private void addJarsFromFolder(File folder, StringBuilder cp) {
        if (folder.exists() && folder.isDirectory()) {
            File[] jarFiles = folder.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    cp.append(File.pathSeparator).append(jar.getAbsolutePath());
                }
            }
        }
    }

    private void addJarsToClassLoader(File jarFolder) throws MalformedURLException {
        if (jarFolder.exists() && jarFolder.isDirectory()) {
            File[] jarFiles = jarFolder.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    URL jarUrl = jar.toURI().toURL();
                    addURLToClasspath(jarUrl);
                }
            }
        }
    }

    private void addURLToClasspath(URL url) {
        try {
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load JAR into classpath: " + e.getMessage());
        }
    }
}
