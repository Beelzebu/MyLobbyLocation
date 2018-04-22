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
package io.github.beelzebu.mylobbylocation;

import io.github.beelzebu.mylobbylocation.commands.SetLobby;
import io.github.beelzebu.mylobbylocation.commands.Spawn;
import io.github.beelzebu.mylobbylocation.database.MySQL;
import io.github.beelzebu.mylobbylocation.listener.LoginListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Beelzebu
 */
public class MLLMain extends JavaPlugin {

    private MySQL mysql;

    @Override
    public void onEnable() {
        mysql = new MySQL(this);
        getCommand("setlobby").setExecutor(new SetLobby(this));
        getCommand("spawn").setExecutor(new Spawn(this));
        Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);
    }

    public String rep(String msg) {
        return msg.replace('&', ChatColor.COLOR_CHAR);
    }

    public MySQL getDatabase() {
        return mysql;
    }
}
