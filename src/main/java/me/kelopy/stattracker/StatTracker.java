package me.kelopy.stattracker;

import me.kelopy.stattracker.db.Database;
import me.kelopy.stattracker.listeners.Listeners;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class StatTracker extends JavaPlugin {

    private static StatTracker instance;

    private Database database;

    @Override
    public void onEnable() {

            try {
                this.database = new Database();
                database.initializeDatabase();
            } catch (SQLException e) {
                System.out.println("Unable to connect to the database.");
                throw new RuntimeException(e);
            }

            instance = this;
            getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {

        try {
            this.database.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Database getDatabase() {
        return database;
    }

    public static StatTracker getInstance(){
        return instance;
    }

}
