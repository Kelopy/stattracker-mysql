package me.kelopy.stattracker.db;

import me.kelopy.stattracker.models.PlayerStats;

import java.sql.*;

public class Database {

    private final String HOST;
    private final String USER;
    private final String PASSWORD;
    private final String DATABASE_NAME;

    public Database(String HOST, String USER, String PASSWORD, String DATABASE_NAME) {
        this.HOST = HOST;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.DATABASE_NAME = DATABASE_NAME;
    }

    private Connection connection;

    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        String url = "jdbc:mysql://" + this.HOST + "/" + this.DATABASE_NAME;

        this.connection = DriverManager.getConnection(url, this.USER, this.PASSWORD);
        System.out.println("Stat Tracker is connected to the database.");

        return this.connection;
    }

    public void initializeDatabase() throws SQLException {
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS player_stats(uuid varchar(36) primary key, deaths int, kills int, blocks_broken long, balance double, last_login DATE, last_logout DATE)";
            statement.execute(sql);
            statement.close();
            System.out.println("'player_stats' table was successfully created.");
    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");
        statement.setString(1, uuid);
        ResultSet results = statement.executeQuery();

        if(results.next()){

            int deaths = results.getInt("deaths");
            int kills = results.getInt("kills");
            long blocksBroken = results.getLong("blocks_broken");
            double balance = results.getDouble("balance");
            Date lastLogin = results.getDate("last_login");
            Date lastLogout = results.getDate("last_logout");

            PlayerStats playerStats = new PlayerStats(uuid, deaths, kills, blocksBroken, balance, lastLogin, lastLogout);

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }

    public void createPlayerStats(PlayerStats stats) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_stats(uuid, deaths, kills, blocks_broken, balance, last_login, last_logout) VALUES (?, ?, ?, ?, ?, ?, ?)");

        statement.setString(1, stats.getUuid());
        statement.setInt(2, stats.getDeaths());
        statement.setInt(3, stats.getKills());
        statement.setLong(4, stats.getBlocksBroken());
        statement.setDouble(5, stats.getBalance());
        statement.setDate(6, new Date(stats.getLastLogin().getTime()));
        statement.setDate(7, new Date(stats.getLastLogout().getTime()));

        statement.executeUpdate();
        statement.close();

    }

    public void updatePlayerStats(PlayerStats stats) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE player_stats SET deaths = ?, kills = ?, blocks_broken = ?, balance = ?, last_login = ?, last_logout = ? WHERE uuid = ?");

        statement.setInt(1, stats.getDeaths());
        statement.setInt(2, stats.getKills());
        statement.setLong(3, stats.getBlocksBroken());
        statement.setDouble(4, stats.getBalance());
        statement.setDate(5, new Date(stats.getLastLogin().getTime()));
        statement.setDate(6, new Date(stats.getLastLogout().getTime()));
        statement.setString(7, stats.getUuid());

        statement.executeUpdate();
        statement.close();

    }

    public void closeConnection() throws SQLException {

        if(this.connection != null){
            this.connection.close();
        }

    }

}
