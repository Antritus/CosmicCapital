package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class CosmicCapitalAccountEvent extends CosmicCapitalEvent{
	private final IAccount user;

	public CosmicCapitalAccountEvent(@NotNull CosmicCapital cosmicCapital, IAccount user) {
		super(cosmicCapital);
		this.user = user;
	}

	public CosmicCapitalAccountEvent(boolean isAsync, @NotNull CosmicCapital cosmicCapital, IAccount user) {
		super(isAsync, cosmicCapital);
		this.user = user;
	}

	public IAccount getUser() {
		return user;
	}
}
