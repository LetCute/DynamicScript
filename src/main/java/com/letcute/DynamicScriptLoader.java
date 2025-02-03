package com.letcute;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.tools.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class DynamicScriptLoader extends JavaPlugin {

    private File scriptsFolder;
    private File compiledFolder;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        scriptsFolder = new File(getDataFolder(), "scripts");
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
            getLogger().info("Created scripts folder: " + scriptsFolder.getAbsolutePath());
        }

        compiledFolder = new File(getDataFolder(), "compiled");
        if (!compiledFolder.exists()) {
            compiledFolder.mkdirs();
        }

        loadAndRunScripts();
    }

    private void loadAndRunScripts() {
        File[] javaFiles = scriptsFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        });

        if (javaFiles == null || javaFiles.length == 0) {
            getLogger().info("Java file not found in scripts folder.");
            return;
        }

        getLogger().info("Find " + javaFiles.length + " java file. Compiling...");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            getLogger().severe("Java compiler not found. Please run the plugin using JDK.");
            return;
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(javaFiles);

            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(compiledFolder.getAbsolutePath());

            String classpath = getFullClasspath();
            options.add("-classpath");
            options.add(classpath);

            getLogger().info("Compilation classpath: " + classpath);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null,
                    compilationUnits);
            boolean success = task.call();
            if (success) {
                getLogger().info("Translation successful!");
                loadClassesFromCompiledFolder();
            } else {
                getLogger().severe("Compilation failed!");
            }
        } finally {
            try {
                fileManager.close();
            } catch (IOException e) {
                getLogger().severe("Error closing fileManager: " + e.getMessage());
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
        } catch (URISyntaxException e) {
            getLogger().severe("Error getting JAR file path: " + e.getMessage());
        }

        return cp.toString();
    }

    private void loadClassesFromCompiledFolder() {
        try {
            URL[] urls = new URL[] { compiledFolder.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls, this.getClassLoader());

            List<File> classFiles = new ArrayList<>();
            collectClassFiles(compiledFolder, classFiles);

            for (File classFile : classFiles) {
                String className = getClassNameFromFile(classFile);
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    if (instance instanceof Script) {
                        getLogger().info("Run script: " + className);
                        ((Script) instance).onEnable();
                    }
                    if (instance instanceof Listener) {
                        getLogger().info("Register event listener: " + className);
                        getServer().getPluginManager().registerEvents((Listener) instance, this);
                    }
                } catch (Exception e) {
                    getLogger().severe("Error loading class " + className + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            getLogger().severe("Error loading classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void collectClassFiles(File folder, List<File> classFiles) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                collectClassFiles(file, classFiles);
            } else if (file.getName().endsWith(".class")) {
                classFiles.add(file);
            }
        }
    }

    private String getClassNameFromFile(File classFile) {
        String path = classFile.getAbsolutePath();
        String basePath = compiledFolder.getAbsolutePath();

        path = path.replace(File.separatorChar, '/');
        basePath = basePath.replace(File.separatorChar, '/');

        if (path.startsWith(basePath)) {
            path = path.substring(basePath.length() + 1);
        }
        if (path.endsWith(".class")) {
            path = path.substring(0, path.length() - 6);
        }
        return path.replace('/', '.');
    }

    @Override
    public void onDisable() {
        getLogger().info("DynamicScriptLoader tắt.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadscripts")) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            loadAndRunScripts();
            sender.sendMessage("Script reloaded.");
            return true;
        }
        return false;
    }
}
