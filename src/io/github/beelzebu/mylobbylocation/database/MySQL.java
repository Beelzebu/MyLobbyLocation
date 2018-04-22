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
package io.github.beelzebu.mylobbylocation.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.beelzebu.mylobbylocation.MLLMain;
import io.github.beelzebu.mylobbylocation.utils.LocationUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Beelzebu
 */
public class MySQL {

    private final MLLMain plugin;

    public MySQL(MLLMain main) {
        plugin = main;
    }

    private HikariDataSource ds;

    public void setup() {
        HikariConfig hc = new HikariConfig();
        hc.setPoolName("Coins MySQL Connection Pool");
        hc.setDriverClassName("com.mysql.jdbc.Driver");
        hc.setJdbcUrl("jdbc:mysql://" + plugin.getConfig().getString("MySQL.Host") + ":" + plugin.getConfig().getInt("MySQL.Port") + "/" + plugin.getConfig().getString("MySQL.Database") + "?autoReconnect=true&useSSL=false");
        hc.addDataSourceProperty("cachePrepStmts", "true");
        hc.addDataSourceProperty("useServerPrepStmts", "true");
        hc.addDataSourceProperty("prepStmtCacheSize", "250");
        hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hc.addDataSourceProperty("characterEncoding", "utf8");
        hc.addDataSourceProperty("encoding", "UTF-8");
        hc.addDataSourceProperty("useUnicode", "true");
        hc.setUsername(plugin.getConfig().getString("MySQL.User"));
        hc.setPassword(plugin.getConfig().getString("MySQL.Password"));
        hc.setMaxLifetime(60000);
        hc.setMinimumIdle(4);
        hc.setIdleTimeout(30000);
        hc.setConnectionTimeout(10000);
        hc.setMaximumPoolSize(plugin.getConfig().getInt("MySQL.Connection Pool", 8));
        hc.setLeakDetectionThreshold(30000);
        hc.validate();
        ds = new HikariDataSource(hc);
        updateDatabase();
    }

    public void updateDatabase() {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `mll_userdata`"
                    + "(`id` INT NOT NULL AUTO_INCREMENT,"
                    + "`uuid` VARCHAR(36) NOT NULL,"
                    + "`name` VARCHAR(16) NOT NULL,"
                    + "`location` VARCHAR(100) NOT NULL,"
                    + "PRIMARY KEY (`id`));");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setMainLobby(Location loc) {
        try (Connection c = ds.getConnection(); ResultSet res = c.prepareStatement("SELECT * FROM mll_userdata WHERE uuid = 'default' AND name = 'default';").executeQuery()) {
            PreparedStatement ps;
            if (res.next()) {
                ps = c.prepareStatement("UPDATE mll_userdata SET location = ? WHERE uuid = ? AND name = ?;");
                ps.setString(3, "default");
                ps.setString(2, "default");
                ps.setString(1, LocationUtils.toString(loc));
            } else {
                ps = c.prepareStatement("INSERT INTO mll_userdata (id, uuid, name, location) VALUES (null, ?, ?, ?);");
                ps.setString(1, "default");
                ps.setString(2, "default");
                ps.setString(3, LocationUtils.toString(loc));
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An error has ocurred while saving MainLobby into database.");
            handleException(ex);
        }
    }

    public Location getMainLobby() {
        try (Connection c = ds.getConnection(); ResultSet res = c.prepareStatement("SELECT * FROM mll_userdata WHERE uuid = 'default' AND name = 'default';").executeQuery()) {
            if (res.next()) {
                return LocationUtils.fromString(res.getString("location"));
            } else {
                plugin.getLogger().log(Level.SEVERE, "MainLobby is not deffined yet.");
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An error has ocurred while getting MainLobby from database.");
            handleException(ex);
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 120, 0);
    }

    public void setPlayerLobby(Player player, Location loc) {
        try (Connection c = ds.getConnection()) {
            PreparedStatement select = c.prepareStatement("SELECT * FROM mll_userdata WHERE uuid = ? AND name = ?;");
            select.setString(1, player.getUniqueId().toString());
            select.setString(2, player.getName().toLowerCase());
            PreparedStatement ps;
            if (select.executeQuery().next()) {
                ps = c.prepareStatement("UPDATE mll_userdata SET location = ? WHERE uuid = ? AND name = ?;");
                ps.setString(3, player.getName().toLowerCase());
                ps.setString(2, player.getUniqueId().toString());
                ps.setString(1, LocationUtils.toString(loc));
            } else {
                ps = c.prepareStatement("INSERT INTO mll_userdata (id, uuid, name, location) VALUES (null, ?, ?, ?);");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName().toLowerCase());
                ps.setString(3, LocationUtils.toString(loc));
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An error has ocurred while saving lobby for {0} into database.", player.getName());
            handleException(ex);
        }
    }

    public Location getLobbyFor(UUID uuid) {
        try (Connection c = ds.getConnection(); ResultSet res = c.prepareStatement("SELECT * FROM mll_userdata WHERE uuid = '" + uuid.toString() + "';").executeQuery()) {
            if (res.next()) {
                return LocationUtils.fromString(res.getString("location"));
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An error has ocurred while getting lobby for {0} from database.", uuid.toString());
            handleException(ex);
        }
        return getMainLobby();
    }

    public Location getLobbyFor(String name) {
        try (Connection c = ds.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM mll_userdata WHERE name = ?;");
            ps.setString(1, name.toLowerCase());
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return LocationUtils.fromString(res.getString("location"));
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "An error has ocurred while getting lobby for {0} from database.", name);
            handleException(ex);
        }
        return getMainLobby();
    }

    private void handleException(SQLException ex) {
        plugin.getLogger().log(Level.SEVERE, "Error information:\n Message: {0}\n Error code: {1}\n SQLState: {2}", new Object[]{ex.getLocalizedMessage(), ex.getErrorCode(), ex.getSQLState()});
    }
}
