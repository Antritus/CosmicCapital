package me.antritus.astral.cosmiccapital.events.database;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.events.CosmicCapitalAccountEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class UserSaveEvent extends CosmicCapitalAccountEvent {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public UserSaveEvent(@NotNull CosmicCapital cosmicCapital, IAccount user) {
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
