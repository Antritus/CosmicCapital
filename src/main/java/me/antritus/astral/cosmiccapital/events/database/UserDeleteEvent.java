package me.antritus.astral.cosmiccapital.events.database;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.Account;
import me.antritus.astral.cosmiccapital.events.CosmicCapitalAccountEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class UserDeleteEvent extends CosmicCapitalAccountEvent {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public UserDeleteEvent(@NotNull CosmicCapital cosmicCapital, Account user) {
		super(true, cosmicCapital, user);
	}

	public static HandlerList getHandlerList(){
		return HANDLER_LIST;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return getHandlerList();
	}
}

