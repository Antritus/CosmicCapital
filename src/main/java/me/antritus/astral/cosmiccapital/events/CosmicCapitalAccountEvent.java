package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.Account;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class CosmicCapitalAccountEvent extends CosmicCapitalEvent{
	private final Account user;

	public CosmicCapitalAccountEvent(@NotNull CosmicCapital cosmicCapital, Account user) {
		super(cosmicCapital);
		this.user = user;
	}

	public CosmicCapitalAccountEvent(boolean isAsync, @NotNull CosmicCapital cosmicCapital, Account user) {
		super(isAsync, cosmicCapital);
		this.user = user;
	}

	public Account getUser() {
		return user;
	}
}
