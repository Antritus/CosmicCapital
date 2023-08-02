package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public abstract class CosmicCapitalOfflinePlayerEvent extends CosmicCapitalEvent {
	private final OfflinePlayer player;
	public CosmicCapitalOfflinePlayerEvent(@NotNull CosmicCapital cosmicCapital, @NotNull OfflinePlayer player) {
		super(cosmicCapital);
		this.player = player;
	}

	public CosmicCapitalOfflinePlayerEvent(boolean isAsync, @NotNull CosmicCapital cosmicCapital, @NotNull OfflinePlayer player) {
		super(isAsync, cosmicCapital);
		this.player = player;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}
}
