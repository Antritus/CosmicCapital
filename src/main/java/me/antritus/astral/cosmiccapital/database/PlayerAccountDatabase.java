package me.antritus.astral.cosmiccapital.database;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class PlayerAccountDatabase {
	private final Map<UUID, PlayerAccount> accounts = new HashMap<>();
	private final CosmicCapital cosmicCapital;

	public PlayerAccountDatabase(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
	}

	@NotNull
	public PlayerAccount get(Player player){
		if (accounts.get(player.getUniqueId()) == null){
			load(player.getUniqueId());
		}
		return accounts.get(player.getUniqueId());
	}
	@NotNull
	public PlayerAccount get(UUID uniqueId){
		if (accounts.get(uniqueId) == null){
			load(uniqueId);
		}
		return accounts.get(uniqueId);
	}

	public void load(UUID uuid){
		PlayerAccount account = new PlayerAccount(cosmicCapital, uuid);
		account.setLoading(true);
		//noinspection removal
		account.setBalance(1000.0);
		accounts.put(account.uniqueId(), account);
		account.setLoading(false);
	}

	private void save(UUID uniqueId) {
	}

	private void unload(UUID uniqueId) {
		accounts.remove(uniqueId);
	}

	public void save(UUID uniqueId, boolean unload) {
		new BukkitRunnable() {
			@Override
			public void run() {
				save(uniqueId);
				if (unload){
					unload(uniqueId);
				}
			}
		}.runTaskAsynchronously(cosmicCapital);
	}
}
