package com.letcute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class DynamicScriptLoader extends JavaPlugin {

    private ScriptLoader scriptLoader;

    @Getter
    private static DynamicScriptLoader instannce;

    @Override
    public void onEnable() {
        instannce = this;
        scriptLoader = new ScriptLoader(this);
        scriptLoader.setupFolders();
        scriptLoader.loadAndRunScripts();
    }

    @Override
    public void onDisable() {
        getLogger().info("DynamicScriptLoader Disable.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadscripts")) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            scriptLoader.loadAndRunScripts();
            sender.sendMessage("Script reloaded.");
            return true;
        }
        return false;
    }
}
