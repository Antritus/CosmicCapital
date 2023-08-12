package me.antritus.astral.cosmiccapital.antsfactions.commands;

import org.bukkit.command.CommandSender;

public class SubCommandClass {
	protected final AdvancedFactionsPlugin main;
	public SubCommandClass(AdvancedFactionsPlugin main){
		this.main = main;
	}
	public void message(CommandSender sender, String key, String... placeholders){
		main.getMessageManager().message(sender, key, placeholders);
	}
	public void message(boolean reparse, CommandSender sender, String key, String... placeholders){
		main.getMessageManager().message(reparse, sender, key, placeholders);
	}
	public void broadcast(String key, String... placeholders){
		main.getMessageManager().broadcast(key, placeholders);
	}
}
