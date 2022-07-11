package me.kelopy.stattracker.listeners;

import me.kelopy.stattracker.StatTracker;
import me.kelopy.stattracker.models.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Date;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws SQLException {
        Player killer = e.getEntity().getKiller();
        Player p = e.getEntity();

        PlayerStats pStats = getPlayerStatsFromDB(p);
        pStats.setDeaths(pStats.getDeaths() + 1);
        pStats.setBalance(pStats.getBalance() - 0.5);

        StatTracker.getInstance().getDatabase().updatePlayerStats(pStats);

        if(killer != null){
            PlayerStats killerStats = getPlayerStatsFromDB(killer);
            killerStats.setKills(killerStats.getKills() + 1);
            killerStats.setBalance(killerStats.getBalance() + 1);

            StatTracker.getInstance().getDatabase().updatePlayerStats(killerStats);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) throws SQLException {
        Player p = e.getPlayer();

        PlayerStats stats = getPlayerStatsFromDB(p);
        stats.setBlocksBroken(stats.getBlocksBroken() + 1);
        stats.setBalance(stats.getBalance() + 0.5);

        StatTracker.getInstance().getDatabase().updatePlayerStats(stats);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        try{
            PlayerStats playerStats = getPlayerStatsFromDB(p);
            playerStats.setLastLogin(new Date());
            StatTracker.getInstance().getDatabase().updatePlayerStats(playerStats);
        }catch (SQLException ex){
            ex.printStackTrace();
            System.out.println("Could not update player stats after join.");
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        try{
            PlayerStats playerStats = getPlayerStatsFromDB(p);
            playerStats.setLastLogout(new Date());
            StatTracker.getInstance().getDatabase().updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Could not update player stats after quit.");
        }

    }

    private PlayerStats getPlayerStatsFromDB(Player p) throws SQLException {
        PlayerStats stats = StatTracker.getInstance().getDatabase().findPlayerStatsByUUID(p.getUniqueId().toString());

        if(stats == null){
            stats = new PlayerStats(p.getUniqueId().toString(), 0, 0, 0, 0, new Date(), new Date());
            StatTracker.getInstance().getDatabase().createPlayerStats(stats);

            return stats;
        }

        return stats;
    }

}
