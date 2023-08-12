package me.antritus.astral.cosmiccapital.antsfactions.commands;

import com.google.common.collect.ImmutableList;
import me.antritus.astral.cosmiccapital.astrolminiapi.CoreCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Direct import from AntsFactions plugin.
 * This shows how sub command parser is worked in commands.
 * There are other ways to use the sub command parser too!
 */
public class FactionCommand extends CoreCommand {
	private final AdvancedFactionsPlugin main;

	public FactionCommand(AdvancedFactionsPlugin main, String name) {
		super(main, name);
		this.main = main;
		setAliases(ImmutableList.of("faction", "f"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, String[] strings) {
		CommandParser.ParseResult result = main.getCommandParser().parse(commandSender, strings);
		switch (result) {
			case NO_SUB_COMMANDS:
				main.getMessageManager().message(commandSender, "command-parse.unknown-argument", "%command%=" + "/factions help");
				break;
			case UNKNOWN:
				main.getMessageManager().message(commandSender, "command-parse.unknown-command", "%command%=" + strings[0]);
				break;
			case PLAYER_ONLY:
				main.getMessageManager().message(commandSender, "command-parse.player-only", "%command%=" + strings[0]);
				break;
			case CONSOLE_ONLY:
				main.getMessageManager().message(commandSender, "command-parse.console-only", "%command%=" + strings[0]);
				break;
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		CommandParser parser = main.getCommandParser();
		return parser.parseTab(sender, args);
	}
}