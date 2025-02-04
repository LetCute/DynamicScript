package com.letcute;

import java.lang.reflect.Field;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class ScriptCommand {
    protected SimpleCommandMap commandMap;

    protected void registerCommand(Command command, String description) {
        try {
            if (commandMap == null) {
                Field commandMapField = DynamicScriptLoader.getInstannce().getServer().getClass()
                        .getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(DynamicScriptLoader.getInstannce().getServer());
            }
            command.setDescription(description);
            commandMap.register("", command); // Đăng ký lệnh
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
