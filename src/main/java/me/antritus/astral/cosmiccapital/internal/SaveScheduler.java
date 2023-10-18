package me.antritus.astral.cosmiccapital.internal;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import org.bukkit.Bukkit;

import java.util.List;

public class SaveScheduler {
	private final CosmicCapital cosmicCapital;

	public SaveScheduler(CosmicCapital cosmicCapital) {
		this.cosmicCapital = cosmicCapital;
		Bukkit.getScheduler().runTaskTimerAsynchronously(cosmicCapital, () -> {
			AccountDatabase accountDatabase = cosmicCapital.playerManager();
			List<PlayerAccount> accountList = accountDatabase.accounts();
			for (PlayerAccount account : accountList){
				// 20 ticks * 50 millis (1/20 = 50)
				long lifespan = account.lifespan-25*50;
				account.lifespan = lifespan;
				if (!account.requireSave){
					if (lifespan>-7500 && lifespan <= 0){
						accountDatabase.remove(account);
						continue;
					}
				}
				accountDatabase.save(account.uniqueId(), false);
				if (lifespan>-7500 && lifespan <= 0){
					accountDatabase.remove(account);
				}
			}
				}, 500, 20);
	}
}
