package me.nicbo.InvadedLandsEvents.commands;

import me.nicbo.InvadedLandsEvents.EventsMain;
import me.nicbo.InvadedLandsEvents.managers.EventManager;
import me.nicbo.InvadedLandsEvents.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class EventConfigCommand implements CommandExecutor {
    private EventsMain plugin;
    private FileConfiguration config;
    private final String usage;

    public EventConfigCommand(EventsMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.usage = ChatColor.GOLD + "Usage: " + ChatColor.YELLOW;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) { //make me pretty pls
        if (cmd.getName().equalsIgnoreCase("eventconfig") || cmd.getName().equalsIgnoreCase("econfig")) {
            if (args.length == 0) {
                sender.sendMessage(usage + "/econfig <event|reload|setting(event-world|spawn-location)> <setting|value> <value>");
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Event config reloaded");
                return true;
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
                return true;
            }
            Player player = (Player) sender;
            switch (args[0]) {
                case "event-world":
                    world(args, player);
                    break;
                case "spawn-location":
                    spawn(args, player);
                    break;
                case "spleef":
                    spleef(args, player);
                    break;
                default:
                    StringBuilder eventList = new StringBuilder(ChatColor.YELLOW.toString());
                    for (String event : EventManager.getEventNames()) {
                        boolean enabled = config.getBoolean("events." + event + ".enabled");
                        eventList.append(ChatColor.YELLOW + "\n   - ").append((enabled ? ChatColor.GREEN : ChatColor.RED) + event);
                    }
                    player.sendMessage(ChatColor.YELLOW + "'" + args[0] + "'" + ChatColor.GOLD + " doesn't exist! Try event-world, spawn-location or an event. \nAll events: " + eventList.toString());
            return true;
            }
        }
        return false;
    }

    private void world(String[] args, Player player) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.GOLD + "event-world: " + ChatColor.YELLOW + config.getString("event-world"));
            player.sendMessage(usage + "/econfig event-world <string>");
        } else {
            config.set("event-world", args[1]);
            player.sendMessage(Bukkit.getWorld(args[1]) == null ? ChatColor.RED + "Warning: Could not find world '" + args[1] + "'!" : ChatColor.GREEN + "event-world set to '" + args[1] + "'!");
        }
    }

    private void spawn(String[] args, Player player) {
        String usageMessage = player.sendMessage(ChatColor.GOLD + "spawn-location: " + ChatColor.YELLOW + config.getString("spawn-location"));
        if (args.length == 1) {
            player.sendMessage(usageMessage);
            player.sendMessage(usage + "/econfig spawn-location set");
        } else if (args[1].equalsIgnoreCase("set") {
            config.set("spawn-location", player.getLocation());
            player.sendMessage("spawn-location set to your location!");
        } else {
            player.sendMessage(usageMessage);
        }
    }

    private void spleef(String[] args, Player player) {
        ConfigurationSection section = config.getConfigurationSection("events.spleef");
        if (args.length == 1) {
            player.sendMessage(ConfigUtils.configSectionToMsgs(section));
            player.sendMessage("\n" + usage + "/econfig spleef <key>");
        } else {
            boolean preview = args.length == 2;
            switch (args[1].toLowerCase()) {
                case "region":
                    if (preview) {
                        player.sendMessage(ChatColor.YELLOW + "region: " + ChatColor.GOLD + config.getString("events.spleef.region"));
                        player.sendMessage(usage + "/econfig spleef region <string>");
                    } else {
                        section.set("region", args[2]);
                        player.sendMessage(ChatColor.GOLD + "region set to " + ChatColor.YELLOW + "'" + args[2] + "'" + ChatColor.GOLD + "!");
                    }
                    break;
                case "enabled":
                    if (preview) {
                        player.sendMessage(ChatColor.YELLOW + "enabled: " + ChatColor.GOLD + config.getBoolean("events.spleef.enabled"));
                        player.sendMessage(usage + "/econfig spleef enabled <boolean>");
                    } else {
                        boolean enable = Boolean.valueOf(args[2]);
                        section.set("enabled", enable);
                        player.sendMessage(ChatColor.GOLD + "enabled set to " + ChatColor.YELLOW + enable + ChatColor.GOLD + "!");
                    }
                    break;
                case "snow-position-1":
                case "snow-position-2":
                    if (preview) {
                        // use configutils function?
                        player.sendMessage(usage + "/econfig spleef " + args[1].toLowerCase() + " set (while standing on block)");
                    } else {
                        Location loc = player.getLocation();
                        loc.setY(loc.getBlockY() - 1);
                        ConfigUtils.blockVectorToConfig(new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), section.getConfigurationSection(args[1]));
                        player.sendMessage(ChatColor.GREEN + args[1] + " set to block under you!");
                    }
                    break;
                case "start-location-1":
                case "start-location-2":
                case "spec-location":
                    if (preview) {
                        // use configutils function?
                        player.sendMessage(usage + "/econfig spleef " + args[1].toLowerCase() + " set");
                    } else if (args[2].equalsIgnoreCase("set")) {
                        ConfigUtils.locToConfig(player.getLocation(), section.getConfigurationSection(args[1]));
                        player.sendMessage(ChatColor.GREEN + args[1] + " set to your location!");
                    }
                    break;
            }
        }
    }
    /*
    TODO:
        - A lot
        - This entire class needs to be re-written it is a shit show
        - Tab complete
     */

}
