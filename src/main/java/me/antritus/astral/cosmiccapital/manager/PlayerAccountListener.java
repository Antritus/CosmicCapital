package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class PlayerAccountListener implements Listener {
	private final CosmicCapital cosmicCapital;
	private final BukkitTask task;
	public PlayerAccountListener(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
		task = new BukkitRunnable() {
			@Override
			public void run() {
				cosmicCapital.getLogger().info("Saving players..!");
				AccountDatabase playerAccountDatabase = (AccountDatabase) cosmicCapital.playerManager();
				Bukkit.getOnlinePlayers().forEach(player-> playerAccountDatabase.save(player.getUniqueId(), false));
				cosmicCapital.getLogger().info("Saved players..!");
			}
		}.runTaskTimerAsynchronously(cosmicCapital, 600, 18_000);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		AccountDatabase playerAccountDatabase = (AccountDatabase) cosmicCapital.playerManager();
		new BukkitRunnable() {
			@Override
			public void run() {
				playerAccountDatabase.load(event.getPlayer().getUniqueId());
				Objects.requireNonNull(playerAccountDatabase.get(event.getPlayer().getUniqueId())).lifespan = -10000;
			}
		}.runTaskAsynchronously(cosmicCapital);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		AccountDatabase playerAccountDatabase = (AccountDatabase) cosmicCapital.playerManager();
		new BukkitRunnable() {
			/**
			 * When an object implementing interface {@code Runnable} is used
			 * to create a thread, starting the thread causes the object's
			 * {@code run} method to be called in that separately executing
			 * thread.
			 * <p>
			 * The general contract of the method {@code run} is that it may
			 * take any action whatsoever.
			 *
			 * @see Thread#run()
			 */
			@Override
			public void run() {
				playerAccountDatabase.save(event.getPlayer().getUniqueId(), false);
				Objects.requireNonNull(playerAccountDatabase.get(event.getPlayer().getUniqueId())).lifespan =5000;
			}
		}.runTaskAsynchronously(cosmicCapital);
	}

	public void onEnable(){
		AccountDatabase playerAccountDatabase = (AccountDatabase) cosmicCapital.playerManager();
		new BukkitRunnable() {
			@Override
			public void run() {
				cosmicCapital.getServer().getOnlinePlayers().forEach(player-> {
					playerAccountDatabase.load(player.getUniqueId());
					Objects.requireNonNull(playerAccountDatabase.get(player.getUniqueId())).lifespan =5000;
				});
			}
		}.runTaskAsynchronously(cosmicCapital);
	}
	public void onDisable(){
		AccountDatabase playerAccountDatabase = cosmicCapital.playerManager();
		for (PlayerAccount account : playerAccountDatabase.accounts()) {
			if (account.requireSave){
				playerAccountDatabase.save(account.uniqueId(), true);
			}else {
				playerAccountDatabase.remove(account);
			}
		}
		task.cancel();
	}
}
