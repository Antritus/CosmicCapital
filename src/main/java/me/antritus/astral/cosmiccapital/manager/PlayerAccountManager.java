package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class PlayerAccountManager implements Listener {
	private final CosmicCapital cosmicCapital;
	private final BukkitTask task;
	public PlayerAccountManager(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
		task = new BukkitRunnable() {
			@Override
			public void run() {
				cosmicCapital.getLogger().info("Saving players..!");
				PlayerAccountDatabase playerAccountDatabase = cosmicCapital.getPlayerDatabase();
				Bukkit.getOnlinePlayers().forEach(player-> playerAccountDatabase.save(player.getUniqueId(), false));
				cosmicCapital.getLogger().info("Saved players..!");
			}
		}.runTaskTimerAsynchronously(cosmicCapital, 600, 18_000);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		PlayerAccountDatabase playerAccountDatabase = cosmicCapital.getPlayerDatabase();
		new BukkitRunnable() {
			@Override
			public void run() {
				playerAccountDatabase.load(event.getPlayer().getUniqueId());
			}
		}.runTaskAsynchronously(cosmicCapital);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		PlayerAccountDatabase playerAccountDatabase = cosmicCapital.getPlayerDatabase();
		playerAccountDatabase.save(event.getPlayer().getUniqueId(), true);
	}

	public void onEnable(){
		PlayerAccountDatabase playerAccountDatabase = cosmicCapital.getPlayerDatabase();
		new BukkitRunnable() {
			@Override
			public void run() {
				cosmicCapital.getServer().getOnlinePlayers().forEach(player-> playerAccountDatabase.load(player.getUniqueId()));
			}
		}.runTaskAsynchronously(cosmicCapital);
	}
	public void onDisable(){
		PlayerAccountDatabase playerAccountDatabase = cosmicCapital.getPlayerDatabase();
		cosmicCapital.getServer().getOnlinePlayers().forEach(player-> playerAccountDatabase.save(player.getUniqueId(), true));
		task.cancel();
	}
}
