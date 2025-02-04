package com.letcute;

import org.bukkit.event.Listener;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class ClassLoaderUtil {

    private DynamicScriptLoader plugin;

    public ClassLoaderUtil(DynamicScriptLoader plugin) {
        this.plugin = plugin;
    }

    public void loadClassesFromCompiledFolder(File compiledFolder) {
        try {
            URL[] urls = new URL[] { compiledFolder.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls, plugin.getClass().getClassLoader());

            List<File> classFiles = FileUtil.getAllClassFiles(compiledFolder);

            for (File classFile : classFiles) {
                String className = FileUtil.getClassNameFromFile(classFile, compiledFolder);
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    if (instance instanceof Script) {
                        ((Script) instance).onEnable();
                    }

                    if (instance instanceof Listener) {
                        plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);
                    }

                } catch (Exception e) {
                    plugin.getLogger().severe("Error loading class " + className + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading classes: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
