package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utilities.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FirstJoinPlus extends JavaPlugin {

    private static FirstJoinPlus instance;
    public String smile = "Girls with the prettiest smiles, have the saddest stories.";

    public List<String> noPVP = new ArrayList<String>();
    public List<String> godMode = new ArrayList<String>();

    public static FirstJoinPlus getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FirstJoinListener(), this);
        Utilities.copyDefaultFiles();

        if (getConfig().getString("settings.teleport-delay") != null) {
            File configuration = new File(getDataFolder() + "/config.yml");
            configuration.setWritable(true);
            configuration.renameTo(new File(getDataFolder() + "/old-config.yml"));
            String[] sections = getConfig().getConfigurationSection("").getKeys(false).toArray(new String[0]);
            for (String s : sections) {
                getConfig().set(s, null);
            }
            saveConfig();
            getLogger().log(Level.SEVERE, "Your configuration was outdated, so we attempted to generate a new one for you.");
        }
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        reloadConfig();
        saveConfig();
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length == 0) {
            cs.sendMessage(ChatColor.YELLOW + "[GoodJoin] " + ChatColor.GRAY + "By Envel. Version " + ChatColor.AQUA + getDescription().getVersion() + ".");
            return true;
        }

        if (strings.length != 1) {
            cs.sendMessage(Utilities.formatCommandResponse("Usage: /firstjoinplus <reload|setspawn|debug>"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("help")) {
            cs.sendMessage(Utilities.formatCommandResponse("Available Commands:"));
            cs.sendMessage(Utilities.formatCommandResponse(ChatColor.AQUA + "/fjp" + ChatColor.GRAY + ": General plugin information."));
            cs.sendMessage(Utilities.formatCommandResponse(ChatColor.AQUA + "/fjp reload" + ChatColor.GRAY + ": Reloads the configuration."));
            cs.sendMessage(Utilities.formatCommandResponse(ChatColor.AQUA + "/fjp setspawn" + ChatColor.GRAY + ": Sets the first-join spawnpoint."));
            cs.sendMessage(Utilities.formatCommandResponse(ChatColor.AQUA + "/fjp debug" + ChatColor.GRAY + ": Become a new player!"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            if (cs.hasPermission("firstjoinplus.reload")) {
                reloadConfig();
                saveConfig();
                Utilities.copyDefaultFiles();
                cs.sendMessage(Utilities.formatCommandResponse("Configuration reloaded."));
            } else {
                cs.sendMessage(Utilities.getNoPermissionMessage());
            }
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(Utilities.formatCommandResponse("You must be a player to do that."));
            return true;
        }

        Player player = (Player) cs;

        if (strings[0].equalsIgnoreCase("debug")) {
            if (cs.hasPermission("firstjoinplus.debug")) {
                Utilities.debugPlayer(player, true);
            } else {
                cs.sendMessage(Utilities.getNoPermissionMessage());
            }
            return true;
        }

        cs.sendMessage(Utilities.formatCommandResponse("Unknown command. Type " + ChatColor.AQUA + "/fjp help" + ChatColor.GRAY + " for help."));
        return true;
    }

}
