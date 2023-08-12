package me.antritus.astral.cosmiccapital.antsfactions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public abstract class TabCompletionEvent extends Event {

	private final SubCommand subCommand;
	private final TabCompletion tabCompletion;
	private final CommandSender sender;
	private final String[] args;

	public TabCompletionEvent(SubCommand subCommand, TabCompletion tabCompletion, CommandSender sender, String[] args) {
		this.subCommand = subCommand;
		this.tabCompletion = tabCompletion;
		this.sender = sender;
		this.args = args;
	}

	public SubCommand getSubCommand() {
		return subCommand;
	}

	public TabCompletion getTabCompletion() {
		return tabCompletion;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String[] getArgs() {
		return args;
	}

	public boolean callEvent(){
		Bukkit.getPluginManager().callEvent(this);
		return true;
	}
}
