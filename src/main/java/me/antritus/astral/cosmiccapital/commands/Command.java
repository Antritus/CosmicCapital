package me.antritus.astral.cosmiccapital.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.commands.balance.BalanceCMD;
import me.antritus.astral.cosmiccapital.commands.economy.AddSubCMD;
import me.antritus.astral.cosmiccapital.commands.economy.RemoveSubCMD;
import me.antritus.astral.cosmiccapital.commands.economy.ResetSubCommand;
import me.antritus.astral.cosmiccapital.commands.economy.SetSubCMD;
import me.antritus.astral.cosmiccapital.patches.MessageManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

public class Command {
	private final CosmicCapital main;
	private final PaperCommandManager<CommandSender> manager;
	private final BukkitAudiences bukkitAudiences;
	private final MessageManager messageManager;


	public Command(CosmicCapital main) {
		this.messageManager = main.messageManager();
		PaperCommandManager<CommandSender> manager1 = null;
		this.main = main;
		bukkitAudiences = BukkitAudiences.create(main);
		final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
				AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
		final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
		try {
			manager1 = new PaperCommandManager<>(
					main,
					executionCoordinatorFunction,
					mapperFunction,
					mapperFunction
			);
		} catch (final Exception e) {
			main.getLogger().severe("Failed to initialize the command manager");
			main.getServer().getPluginManager().disablePlugin(main);
			return;
		} finally {
			this.manager = manager1;
		}
		;
		this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
				FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
		));

		if (this.manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
			this.manager.registerBrigadier();
		}

		if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
			this.manager.registerAsynchronousCompletions();
		}

		new MinecraftExceptionHandler<CommandSender>()
				.withInvalidSyntaxHandler()
				.withInvalidSenderHandler()
				.withNoPermissionHandler()
				.withArgumentParsingHandler()
				.withCommandExecutionHandler()
				.withDecorator(
						component -> main.messageManager().parse("command-parse.exception").replaceText(consumer -> {
							consumer.match("%exception%").replacement(component);
						})
				).apply(this.manager, bukkitAudiences::sender);


		this.registerCommands();
	}

	private void registerCommands() {
		final cloud.commandframework.Command.Builder<CommandSender> builderEconomy = this.manager.commandBuilder("economy",
				ArgumentDescription.of("Economy command. Allows operators to give, take, set, reset balances of players or even all players!"),
				"eco").permission("cosmiccapital.economy");
		new AddSubCMD(messageManager, main, builderEconomy, manager);
		new RemoveSubCMD(messageManager, main, builderEconomy, manager);
		new SetSubCMD(messageManager, main, builderEconomy, manager);
		new ResetSubCommand(messageManager, main, builderEconomy, manager);
		new MinecraftHelp<>("/economy help", bukkitAudiences::sender, manager);
		new MinecraftHelp<>("/economy", bukkitAudiences::sender, manager);

		final cloud.commandframework.Command.Builder<CommandSender> builderBalance = this.manager.commandBuilder("balance",
				ArgumentDescription.of("Economy command. Allows players to see their balance or other players balance!"),
				"bal", "money", "purse").permission("cosmiccapital.economy");
		new BalanceCMD(messageManager, main, builderBalance, manager );
	}


	public static String placeholder(String key, Object value) {
		return "%" + key + "%=" + value;
	}
}
