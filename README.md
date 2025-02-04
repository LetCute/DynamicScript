# DynamicScriptLoader

**DynamicScriptLoader** is a plugin for Minecraft servers (Bukkit/Spigot/Paper) that allows you to load Java files directly without the need for rebuilding the plugin. You can easily add Java scripts to the server without having to recompile and reinstall the plugin.

## Installation

1. Download the **DynamicScriptLoader** JAR file and place it into the `plugins` folder in the root directory of your Minecraft server.
2. Restart the server for the plugin to load.
3. Java script files will be loaded and executed when the server starts or when an event is triggered.

## Usage

### Create a New Java Script

1. **Create a new Java script file**: Create a file `JoinScript.java` inside the `scripts` folder of the plugin.

```java
import com.letcute.Script;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinScript implements Script, Listener {
    @Override
    public void onEnable() {
        System.out.println("JoinScript is running! Listening for PlayerJoinEvent.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server, sent from DynamicScriptLoader");
    }
}
