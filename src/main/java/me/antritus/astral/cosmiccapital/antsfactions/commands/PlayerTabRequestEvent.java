package me.antritus.astral.cosmiccapital.antsfactions.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PlayerTabRequestEvent extends TabCompletionEvent{
	private static final HandlerList handlers = new HandlerList();
	private final List<OfflinePlayer> players = new ArrayList<>();
	public PlayerTabRequestEvent(SubCommand subCommand, TabCompletion tabCompletion, CommandSender sender, String[] args) {
		super(subCommand, tabCompletion, sender, args);
		players.addAll(Bukkit.getOnlinePlayers());
	}

	public List<OfflinePlayer> getPlayers() {
		return players;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}



}
