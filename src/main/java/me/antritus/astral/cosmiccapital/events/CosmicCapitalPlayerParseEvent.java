package me.antritus.astral.cosmiccapital.events;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CosmicCapitalPlayerParseEvent extends CosmicCapitalOfflinePlayerEvent implements Cancellable {
	private boolean isCancelled;
	private CosmicCapitalCommand command;
	public CosmicCapitalPlayerParseEvent(CosmicCapital cosmicCapital, CosmicCapitalCommand cmd, @NotNull OfflinePlayer player) {
		super(cosmicCapital, player);
		this.command = cmd;
	}
	private static final HandlerList HANDLER_LIST = new HandlerList();
	public static HandlerList getHandlerList(){
		return HANDLER_LIST;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return getHandlerList();
	}

	public CosmicCapitalCommand getCommand() {
		return command;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;
	}
}
