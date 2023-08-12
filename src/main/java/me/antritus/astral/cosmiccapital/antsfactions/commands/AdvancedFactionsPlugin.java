package me.antritus.astral.cosmiccapital.antsfactions.commands;


import me.antritus.astral.cosmiccapital.antsfactions.FactionsPlugin;

public abstract class AdvancedFactionsPlugin extends FactionsPlugin {
	private final CommandParser commandParser;

	public AdvancedFactionsPlugin() {
		this.commandParser = new CommandParser(this);
	}


	public void registerSubcommands(Class<?> subCommandClass){
		commandParser.register(subCommandClass);
	}

	public CommandParser getCommandParser() {
		return commandParser;
	}
}
