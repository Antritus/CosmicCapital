package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class CosmicCapitalPlayerEvent extends CosmicCapitalEvent{
	@NotNull
	private final Player player;


	public CosmicCapitalPlayerEvent(@NotNull CosmicCapital cosmicCapital, @NotNull Player player) {
		super(cosmicCapital);
		this.player = player;
	}

	public CosmicCapitalPlayerEvent(boolean isAsync, @NotNull CosmicCapital cosmicCapital, @NotNull Player player) {
		super(isAsync, cosmicCapital);
		this.player = player;
	}

	@NotNull
	public Player getPlayer() {
		return player;
	}
}
