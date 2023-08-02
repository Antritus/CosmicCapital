package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class CosmicCapitalEvent extends Event {
	@NotNull
	private final CosmicCapital cosmicCapital;

	public CosmicCapitalEvent(@NotNull CosmicCapital cosmicCapital) {
		this.cosmicCapital = cosmicCapital;
	}

	public CosmicCapitalEvent(boolean isAsync, @NotNull CosmicCapital cosmicCapital) {
		super(isAsync);
		this.cosmicCapital = cosmicCapital;
	}

	@NotNull
	public CosmicCapital getCosmicCapital() {
		return cosmicCapital;
	}
}
