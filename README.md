# DynamicScriptLoader

**DynamicScriptLoader** is a plugin for Minecraft servers (Bukkit/Spigot/Paper) that allows you to load Java files directly without the need for rebuilding the plugin. You can easily add Java scripts to the server without having to recompile and reinstall the plugin.

## Installation

1. Download the **DynamicScriptLoader** JAR file and place it into the `plugins` folder in the root directory of your Minecraft server.
2. Restart the server for the plugin to load.
3. Java script files will be loaded and executed when the server starts or when an event is triggered.

## Usage

### Create a New Java Script

1. **Create a new Java script file**: Create a file `/com/letscript/JoinScript.java` inside the `scripts` folder of the plugin.

```java
package com.letscript;

import com.letcute.Script;
import com.letcute.ScriptCommand;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.command.*;

import net.letcute.guimenu.*;

import org.bukkit.*;
import org.bukkit.entity.*;

public class JoinScript extends ScriptCommand implements Script, Listener {
    @Override
    public void onEnable() {
        GuiManager.register("GuiDemo", new GuiDemo("Gui Demo"));
        registerCommand(new TestCommand(), "A custom hello command");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.getPlayer()
                .sendMessage("Chào mừng " + player.getName() + " đến với server ");

    }
}
```
2. **Create a new Java script file**: Create a file `/com/letscript/GuiDemo.java` inside the `scripts` folder of the plugin.
```java
package com.letscript;

import java.util.Collections;

import net.letcute.guimenu.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiDemo implements Gui {
    private  String title = "";

    private GuiBuilder guiBuilder;

    public GuiDemo() {
    }

    public GuiDemo(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void open(Player player) {
        if (guiBuilder == null) {
            guiBuilder = new GuiBuilder(title, 54);
            guiBuilder.addItem(Material.PLAYER_HEAD, "Profile", Collections.singletonList("info player"), 11);
            guiBuilder.addItem(Material.SPRUCE_SIGN, "Shop", Collections.singletonList("shop Item"), 12);
            guiBuilder.addItem(Material.GRASS_BLOCK, "Island", Collections.singletonList("Controller island"), 13);
        }
        guiBuilder.openMenu(player);
    }

    @Override
    public void action(GuiDataClick guiDataClick) {
        ItemMeta meta = guiDataClick.getItemStack().getItemMeta();
        String displayName = meta.getDisplayName();
        Player player = guiDataClick.getPlayer();
        if (displayName == null)
            return;
        switch (displayName) {
            case "Profile":
                player.sendMessage("profile");
                break;
            case "Shop":
                player.sendMessage("shop");
                break;
            case "Island":
                GuiManager.openGui("GuiDemo2", player);
                break;
        }
    }
}

```
3. **Create a new Java script file**: Create a file `/com/letscript/TestCommand.java` inside the `scripts` folder of the plugin.

```java
package com.letscript;

import org.bukkit.command.*;
import net.letcute.guimenu.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;

public class TestCommand extends Command {
    public TestCommand() {
        super("test");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        GuiManager.openGui("GuiDemo", (Player) sender);
        return true;
    }
}


```