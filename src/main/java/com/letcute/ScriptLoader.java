package com.letcute;

import java.io.File;

public class ScriptLoader {

    private final DynamicScriptLoader plugin;
    private File scriptsFolder;
    private File compiledFolder;
    private File jarFolder;

    public ScriptLoader(DynamicScriptLoader plugin) {
        this.plugin = plugin;
    }

    public void setupFolders() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        scriptsFolder = new File(plugin.getDataFolder(), "scripts");
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
            plugin.getLogger().info("Created scripts folder: " + scriptsFolder.getAbsolutePath());
        }

        compiledFolder = new File(plugin.getDataFolder(), "compiled");
        if (!compiledFolder.exists()) {
            compiledFolder.mkdirs();
        }

        jarFolder = new File(plugin.getDataFolder(), "jar");
        if (!jarFolder.exists()) {
            jarFolder.mkdirs();
        }
    }

    public void loadAndRunScripts() {
        ScriptCompiler scriptCompiler = new ScriptCompiler(plugin);
        scriptCompiler.compileScripts(scriptsFolder, compiledFolder);

        ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil(plugin);
        classLoaderUtil.loadClassesFromCompiledFolder(compiledFolder);
    }
}
