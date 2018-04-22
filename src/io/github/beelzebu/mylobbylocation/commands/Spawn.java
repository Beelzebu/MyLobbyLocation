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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class Spawn implements CommandExecutor {

    private final MLLMain plugin;

    public Spawn(MLLMain main) {
        plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("mylobbylocation.user")) {
                ((Player) sender).teleport(plugin.getDatabase().getLobbyFor(((Player) sender).getUniqueId()));
            } else {
                sender.sendMessage(plugin.rep("&cYou don't have permissions to do this."));
            }
        } else {
            if (args.length == 0) {
                sender.sendMessage(plugin.rep("&7Please use: &f/spawn [player] (main)"));
            } else {
                if (args.length == 1) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Bukkit.getPlayer(args[0]).teleport(plugin.getDatabase().getLobbyFor(args[0]));
                        sender.sendMessage(plugin.rep("&6Teleporting " + args[1] + " to his lobby..."));
                    } else {
                        sender.sendMessage(plugin.rep("&c" + args[0] + " must be online"));
                    }
                } else if (args.length == 2) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("main")) {
                            Bukkit.getPlayer(args[0]).teleport(plugin.getDatabase().getMainLobby());
                            sender.sendMessage(plugin.rep("&6Teleporting " + args[1] + " to main lobby..."));
                        } else {
                            sender.sendMessage(plugin.rep("&cUnknown argument: " + args[1]));
                        }
                    } else {
                        sender.sendMessage(plugin.rep("&c" + args[0] + " must be online"));
                    }
                }
            }
        }
        return true;
    }
}
