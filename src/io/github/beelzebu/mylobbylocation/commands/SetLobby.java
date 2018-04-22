/**
 * This file is part of MyLobbyLocation
 *
 * Copyright (C) 2018 Beelzebu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.beelzebu.mylobbylocation.commands;

import io.github.beelzebu.mylobbylocation.MLLMain;
import io.github.beelzebu.mylobbylocation.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class SetLobby implements CommandExecutor {

    private final MLLMain plugin;

    public SetLobby(MLLMain main) {
        plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                if (sender.hasPermission("mylobbylocation.user")) {
                    sender.sendMessage(plugin.rep("&6&m-------------&6[ &c&lMyLobbyLocation&6 ]&m-------------"));
                    if (sender.hasPermission("mylobbylocation.admin")) {
                        sender.sendMessage(plugin.rep("  &c/setlobby main &f-&7 Set the main lobby."));
                        sender.sendMessage(plugin.rep("  &c/setlobby to [player] &f-&7 Set the lobby for a player."));
                    }
                    sender.sendMessage(plugin.rep("  &a/setlobby me &f-&7 Set your lobby location."));
                } else {
                    sender.sendMessage(plugin.rep("&cYou don't have permissions to do this."));
                }
            } else {
                switch (args[0].toLowerCase()) {
                    case "main":
                        if (sender.hasPermission("mylobbylocation.admin")) {
                            plugin.getDatabase().setMainLobby(((Player) sender).getLocation());
                            sender.sendMessage(plugin.rep("&7Main Lobby was set in: &c" + LocationUtils.toString(((Player) sender).getLocation())));
                        }
                        break;
                    case "my":
                        if (sender.hasPermission("mylobbylocation.user")) {
                            plugin.getDatabase().setPlayerLobby((Player) sender, ((Player) sender).getLocation());
                        } else {
                            sender.sendMessage(plugin.rep("&cYou don't have permissions to do this."));
                        }
                        break;
                    case "to":
                        if (sender.hasPermission("mylobbylocation.admin")) {
                            if (args[1] != null) {
                                if (Bukkit.getPlayer(args[1]) != null) {
                                    plugin.getDatabase().setPlayerLobby(Bukkit.getPlayer(args[1]), ((Player) sender).getLocation());
                                    sender.sendMessage(plugin.rep("&7Lobby for " + args[1] + " was set in: &c" + LocationUtils.toString(((Player) sender).getLocation())));
                                } else {
                                    sender.sendMessage(plugin.rep("&c" + args[1] + " must be online."));
                                }
                            }
                        }
                        break;
                }
            }
        } else {
            sender.sendMessage(plugin.rep("&4This command must be used in game."));
        }
        return true;
    }
}
