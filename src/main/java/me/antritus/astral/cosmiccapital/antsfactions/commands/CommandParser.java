package me.antritus.astral.cosmiccapital.antsfactions.commands;


import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The CommandParser class provides functionality for parsing and executing commands,
 * as well as handling tab completions.
 * It supports registering subcommands and tab completions,
 * and provides methods for parsing and generating tab completions based on the input.
 * <p>
 * This is an optimized version of the CommandParser class.
 * <p>
 *     This is more hardcore source file which may be hard to read at first.
 * </p>
 */
public class CommandParser {
	private final AdvancedFactionsPlugin main;
	private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();
	private final Map<String, Method> subCommandMethods = new LinkedHashMap<>();
	private final Map<String, Object> subCommandInstances = new LinkedHashMap<>();
	private final Map<String, TabCompletion> tabCompletions = new LinkedHashMap<>();
	private final Map<String, Method> tabCompleteMethods = new LinkedHashMap<>();
	private final Map<String, Object> tabCompleteInstances = new LinkedHashMap<>();
	private final Map<String, String> aliasCommands = new LinkedHashMap<>();

	/**
	 * Constructs a CommandParser object with the given Main instance.
	 *
	 * @param main the Main instance
	 */
	public CommandParser(AdvancedFactionsPlugin main) {
		this.main = main;
	}

	/**
	 * Registers a class containing subcommands and tab completions.
	 * It scans the class for methods annotated with @SubCommand and @TabCompletion,
	 * and adds them to the corresponding maps for later usage.
	 *
	 * @param clazz the class to register
	 */
	public void register(Class<?> clazz) {
		try {
			Object obj = clazz.getConstructor(AdvancedFactionsPlugin.class).newInstance(main);
			Method[] methods = clazz.getMethods();

			for (Method method : methods) {
				SubCommand subCommand = method.getAnnotation(SubCommand.class);
				if (subCommand != null) {
					String name = subCommand.name();
					String[] aliases = subCommand.aliases();

					subCommands.put(name, subCommand);
					subCommandMethods.put(name, method);
					subCommandInstances.put(name, obj);
					aliasCommands.put(name, name);

					for (String alias : aliases) {
						aliasCommands.put(alias, name);
					}
				} else {
					TabCompletion tabCompletion = method.getAnnotation(TabCompletion.class);
					if (tabCompletion != null) {
						tabCompletions.put(tabCompletion.name(), tabCompletion);
						tabCompleteMethods.put(tabCompletion.name(), method);
						tabCompleteInstances.put(tabCompletion.name(), obj);
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parses a command and executes the corresponding subcommand based on the command sender and input.
	 *
	 * @param sender the command sender
	 * @param msg    the command input as an array of strings
	 * @return the parse result indicating the outcome of the command parsing
	 */
	public ParseResult parse(CommandSender sender, String[] msg) {
		if (msg.length == 0) {
			return ParseResult.NO_SUB_COMMANDS;
		}

		String commandName = aliasCommands.get(msg[0]);
		if (commandName == null) {
			return ParseResult.UNKNOWN;
		}

		SubCommand subCommand = subCommands.get(commandName);
		boolean isConsole = sender instanceof ConsoleCommandSender;
		boolean isPlayer = sender instanceof Player;
		if (isConsole && subCommand.sender() == SenderType.PLAYER) {
			return ParseResult.PLAYER_ONLY;
		} else if (isPlayer && subCommand.sender() == SenderType.CONSOLE) {
			return ParseResult.CONSOLE_ONLY;
		}

		Method method = subCommandMethods.get(commandName);
		try {
			method.invoke(subCommandInstances.get(commandName), sender, Arrays.copyOfRange(msg, 1, msg.length));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return ParseResult.FOUND;
	}

	public List<String> parseTab(CommandSender sender, String[] msg) {
		if (!(sender instanceof Player)) {
			return Collections.singletonList("");
		}

		if (msg.length == 1) {
			List<String> completions = new ArrayList<>();
			subCommands.forEach((name, command) -> {
				completions.add(name);
			});
			return completions;
		} else {
			String commandName = aliasCommands.get(msg[0]);
			SubCommand subCommand = subCommands.get(commandName);

			if (subCommand == null) {
				return Collections.singletonList("< Unknown Command >");
			}

			TabCompletion tabCompletion = tabCompletions.get(subCommand.name());
			if (tabCompletion == null) {
				return Collections.singletonList("");
			}

			if (tabCompletion.sender() == SenderType.CONSOLE || !sender.hasPermission(subCommand.permission())) {
				return Collections.singletonList("");
			}

			Method method = tabCompleteMethods.get(commandName);
			try {
				String[] parsed = Arrays.stream(Arrays.copyOfRange(msg, 1, msg.length)).filter(s -> !s.isEmpty() && !s.trim().isEmpty()).toArray(String[]::new);
				String[] args = new String[parsed.length+1];
				for (int i = 0; i < parsed.length; i++){
					args[i] = parsed[i];
				}
				if (args.length>1){
					if (args[0].equalsIgnoreCase("")){
						return Collections.singletonList("");
					}
				}
//				if (parsed.length - 1 >= 0) System.arraycopy(parsed, 0, args, 0, parsed.length - 1);
				args[args.length-1] = "";
				Object returnValue = method.invoke(tabCompleteInstances.get(commandName), sender, args);
				if (!(returnValue instanceof List)) {
					throw new RuntimeException("Unknown type!");
				}

				//noinspection unchecked
				return (List<String>) returnValue;
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public List<String> playerCompletions(String command, CommandSender sender, String[] args){
		PlayerTabRequestEvent requestEvent = new PlayerTabRequestEvent(subCommands.get(command), tabCompletions.get(command), sender, args);
		requestEvent.callEvent();
		List<String> returnList = new ArrayList<>();
		requestEvent.getPlayers().forEach(p->returnList.add(p.getName()));
		return returnList;
	}
	public List<OfflinePlayer> playerCompletionsPlayerList(String command, CommandSender sender, String[] args) {
		PlayerTabRequestEvent requestEvent = new PlayerTabRequestEvent(subCommands.get(command), tabCompletions.get(command), sender, args);
		requestEvent.callEvent();
		return requestEvent.getPlayers();
	}

	public List<String> playerCompletions(String command, CommandSender sender, String[] args, Collection<OfflinePlayer> exclusions) {
		PlayerTabRequestEvent requestEvent = new PlayerTabRequestEvent(subCommands.get(command), tabCompletions.get(command), sender, args);
		requestEvent.getPlayers().removeAll(exclusions);
		requestEvent.callEvent();
		List<String> returnList = new ArrayList<>();
		requestEvent.getPlayers().forEach(p->returnList.add(p.getName()));
		return returnList;
	}

	public PlayerTabRequestEvent playerCompletionsNoCall(String command, @NotNull CommandSender sender, String[] args){
		return new PlayerTabRequestEvent(subCommands.get(command), tabCompletions.get(command), sender, args);
	}
	public PlayerTabRequestEvent playerCompletionsNoCall(String command, CommandSender sender, String[] args, @NotNull Collection<OfflinePlayer> exclusions) {
		PlayerTabRequestEvent event = new PlayerTabRequestEvent(subCommands.get(command), tabCompletions.get(command), sender, args);
		event.getPlayers().removeAll(exclusions);
		return event;
	}


	/**
	 * Enum representing the result of a command parsing operation.
	 */
	public enum ParseResult {
		PLAYER_ONLY,
		CONSOLE_ONLY,
		FACTION_ONLY,
		NO_FACTION_ONLY,
		FOUND,
		NO_SUB_COMMANDS,
		UNKNOWN,
	}

	/**
	 * Generates a list of SubCommandHelp objects representing the registered subcommands.
	 *
	 * @return a list of SubCommandHelp objects
	 */
	public List<SubCommandHelp> help() {
		List<SubCommandHelp> subCommandHelps = new ArrayList<>();

		for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
			SubCommand subCommand = entry.getValue();
			subCommandHelps.add(new SubCommandHelp(subCommand));
		}

		return subCommandHelps;
	}

	/**
	 * Helper class representing help information for a subcommand.
	 */
	public static class SubCommandHelp {
		private final String name;
		private final String[] aliases;
		private final String description;
		private final String permission;
		private final boolean playerOnly;
		private final boolean consoleOnly;

		/**
		 * Constructs a SubCommandHelp object with the given subcommand.
		 *
		 * @param subCommand the subcommand
		 */
		public SubCommandHelp(SubCommand subCommand) {
			this.name = subCommand.name();
			this.aliases = subCommand.aliases();
			this.description = subCommand.description();
			this.permission = subCommand.permission();
			this.playerOnly = subCommand.sender() == SenderType.PLAYER;
			this.consoleOnly = subCommand.sender() == SenderType.CONSOLE;
		}

		public String getName() {
			return name;
		}

		public String[] getAliases() {
			return Arrays.copyOf(aliases, aliases.length);
		}

		public String getDescription() {
			return description;
		}

		public String getPermission() {
			return permission;
		}

		public boolean isPlayerOnly() {
			return playerOnly;
		}

		public boolean isConsoleOnly() {
			return consoleOnly;
		}
	}
}