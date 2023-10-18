package me.antritus.astral.cosmiccapital.types.operators;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;

public class BukkitOfflineOperator extends BukkitDefaultOperator {
	public BukkitOfflineOperator(UUID uniqueId) {
		super(uniqueId, "player");
	}

	@Override
	public OfflinePlayer get() {
		if (uniqueId() != null){
			return Bukkit.getOfflinePlayer(Objects.requireNonNull(uniqueId()));
		}
		return null;
	}
}
