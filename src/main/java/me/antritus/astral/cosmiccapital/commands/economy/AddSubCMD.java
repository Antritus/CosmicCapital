package me.antritus.astral.cosmiccapital.commands.economy;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.github.antritus.astral.messages.MessageManager;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import me.antritus.astral.cosmiccapital.manager.OperatorManagerImpl;
import me.antritus.astral.cosmiccapital.types.BukkitEntryImpl;
import me.antritus.astral.cosmiccapital.types.operators.BukkitDefaultOperator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static me.antritus.astral.cosmiccapital.commands.Command.placeholder;

public class AddSubCMD {
	private final MessageManager messageManager;
	private final CosmicCapital main;
	private final Command.Builder<CommandSender> builder;
	private final PaperCommandManager<CommandSender> manager;

	public AddSubCMD(MessageManager messageManager, CosmicCapital main, Command.Builder<CommandSender> builder, PaperCommandManager<CommandSender> manager) {
		this.messageManager = messageManager;
		this.main = main;
		this.builder = builder;
		this.manager = manager;
		subAdd();
		subAddAllOnline();
		subAddAllOffline();
		subAddAllOfflineOnline();
	}

	private void subAdd() {
		manager.command(builder.literal("add",
								ArgumentDescription.of("Allows operators add to accounts of players."),
								"give"
						)
						.argument(OfflinePlayerArgument.of("who"))
						.argument(DoubleArgument.of("amount"))
						.handler(handler -> {
							CommandSender sender = handler.getSender();
							OfflinePlayer who = handler.get("who");
							final double amount = handler.get("amount");
							AccountDatabase accountDatabase = main.playerManager();
							OperatorManagerImpl operatorManager = main.operatorManager();
							Bukkit.getScheduler().runTaskAsynchronously(
									main,
									() -> {
										PlayerAccount account = accountDatabase.get(who.getUniqueId());
										if (account == null) {
											accountDatabase.load(who.getUniqueId());
										}
										assert account != null;
										account.requireSave = false;

										BukkitDefaultOperator operator;
										if (sender instanceof Player player) {
											operator = operatorManager.getPlayerOperator(player.getUniqueId());
										} else if (sender instanceof ConsoleCommandSender) {
											operator = operatorManager.getConsoleOperator();
										} else {
											operator = BukkitDefaultOperator.toOperator(sender);
										}
										if (operator == null) {
											messageManager.message(sender, "economy-remove-unknown-operator-error");
											return;
										}
										account.operatorAdd(main, operator, amount, null);
										BukkitEntryImpl entry = account.history().latest();

										String senderName = (sender instanceof ConsoleCommandSender ? "CONSOLE" :
												sender.getName());
										ICurrency currency = account.currency();
										assert currency != null;
										String currencyPlural = currency.plural();
										String currencySingular = currency.singular();
										String currencyName = currency.name();
										String currencyChar = currency.character();
										messageManager.message(sender, "economy-add-sender",
												placeholder("player", account.name()),
												placeholder("amount", amount),
												placeholder("who", senderName),
												placeholder("new-balance", entry.balanceAfter()),
												placeholder("old-balance", entry.balanceBefore()),
												placeholder("change-balance", entry.balanceChange()),
												placeholder("currency-name", currencyName),
												placeholder("currency-plural", currencyPlural),
												placeholder("currency-singular", currencySingular),
												placeholder("currency-char", currencyChar)
										);
										if (who instanceof Player) {
											messageManager.message(who.getPlayer(), "economy-add-received",
													placeholder("player", who.getUniqueId()),
													placeholder("amount", amount),
													placeholder("who", senderName),
													placeholder("new-balance", entry.balanceAfter()),
													placeholder("old-balance", entry.balanceAfter()),
													placeholder("change-balance", entry.balanceChange()),													placeholder("currency-name", currencyName),
													placeholder("currency-plural", currencyPlural),
													placeholder("currency-singular", currencySingular),
													placeholder("currency-char", currencyChar)
											);
										}
									}
							);
						})
		);
	}

	private void subAddAllOnline() {
		manager.command(builder.literal("add",
								ArgumentDescription.of("Allows operators add to accounts of players."),
								"give"
						)
						.literal("-online",
								ArgumentDescription.of("Allows operators add to accounts of all online players")
						)
						.argument(DoubleArgument.of("amount"))
						.handler(handler -> {
							CommandSender sender = handler.getSender();
							double amount = handler.get("amount");
							AccountDatabase accountDatabase = main.playerManager();
							OperatorManagerImpl operatorManager = main.operatorManager();
							Collection<? extends Player> players = Bukkit.getOnlinePlayers();
							Bukkit.getScheduler().runTaskAsynchronously(
									main,
									() -> {
										BukkitDefaultOperator operator;
										if (sender instanceof Player onlinePlayer) {
											operator = operatorManager.getPlayerOperator(onlinePlayer.getUniqueId());
										} else if (sender instanceof ConsoleCommandSender) {
											operator = operatorManager.getConsoleOperator();
										} else {
											operator = BukkitDefaultOperator.toOperator(sender);
										}
										if (operator == null) {
											messageManager.message(sender, "economy-add-unknown-operator-error");
											return;
										}
										String senderName = (sender instanceof ConsoleCommandSender ? "CONSOLE" :
												sender.getName());

										for (Player player : players) {
											PlayerAccount account = accountDatabase.get(player.getUniqueId());
											if (account == null) {
												accountDatabase.load(player.getUniqueId());
											}
											assert account != null;
											account.requireSave = false;

											account.operatorAdd(main, operator, amount, null);
											BukkitEntryImpl entry = account.history().latest();
											ICurrency currency = account.currency();
											assert currency != null;
											String currencyPlural = currency.plural();
											String currencySingular = currency.singular();
											String currencyName = currency.name();
											String currencyChar = currency.character();
											messageManager.message(sender, "economy-add-sender",
													placeholder("player", account.name()),
													placeholder("amount", amount),
													placeholder("who", senderName),
													placeholder("new-balance", entry.balanceAfter()),
													placeholder("old-balance", entry.balanceBefore()),
													placeholder("change-balance", entry.balanceChange()),
													placeholder("currency-name", currencyName),
													placeholder("currency-plural", currencyPlural),
													placeholder("currency-singular", currencySingular),
													placeholder("currency-char", currencyChar)
											);
											messageManager.message(player, "economy-add-received",
													placeholder("player", account.name()),
													placeholder("amount", amount),
													placeholder("who", senderName),
													placeholder("new-balance", entry.balanceAfter()),
													placeholder("old-balance", entry.balanceBefore()),
													placeholder("change-balance", entry.balanceChange()),
													placeholder("currency-name", currencyName),
													placeholder("currency-plural", currencyPlural),
													placeholder("currency-singular", currencySingular),
													placeholder("currency-char", currencyChar)
											);

										}
									}
							);
						})
		);
	}

	private void subAddAllOffline() {
		manager.command(builder.literal("add",
								ArgumentDescription.of("Allows operators add to accounts of players."),
								"give"
						)
						.literal("-offline",
								ArgumentDescription.of("Allows operators add to accounts of all offline players.")
						)
						.argument(DoubleArgument.of("amount"))
						.handler(handler -> {
							CommandSender sender = handler.getSender();
							double amount = handler.get("amount");
							OperatorManagerImpl operatorManager = main.operatorManager();
							Collection<? extends Player> players = Bukkit.getOnlinePlayers();

							Bukkit.getScheduler().runTaskAsynchronously(
									main,
									() -> {
										String senderName = (sender instanceof ConsoleCommandSender ? "CONSOLE" :
												sender.getName());

										List<PlayerAccount> accounts = main.playerManager().accounts();
										accounts.removeIf(Objects::isNull);
										accounts.removeIf(account -> players.stream().anyMatch(player -> account.uniqueId() == player.getUniqueId()));


										BukkitDefaultOperator operator;
										if (sender instanceof Player onlinePlayer) {
											operator = operatorManager.getPlayerOperator(onlinePlayer.getUniqueId());
										} else if (sender instanceof ConsoleCommandSender) {
											operator = operatorManager.getConsoleOperator();
										} else {
											operator = BukkitDefaultOperator.toOperator(sender);
										}
										if (operator == null) {
											messageManager.message(sender, "economy-add-unknown-operator-error");
											return;
										}

										for (PlayerAccount account : accounts) {
											if (account == null) {
												continue;
											}

											account.requireSave = false;
											account.operatorAdd(main, operator, amount, null);
											BukkitEntryImpl entry = account.history().latest();
											ICurrency currency = account.currency();
											assert currency != null;
											String currencyPlural = currency.plural();
											String currencySingular = currency.singular();
											String currencyName = currency.name();
											String currencyChar = currency.character();
											messageManager.message(sender, "economy-add-sender",
													placeholder("player", account.name()),
													placeholder("amount", amount),
													placeholder("who", senderName),
													placeholder("new-balance", entry.balanceAfter()),
													placeholder("old-balance", entry.balanceBefore()),
													placeholder("change-balance", entry.balanceChange()),
													placeholder("currency-name", currencyName),
													placeholder("currency-plural", currencyPlural),
													placeholder("currency-singular", currencySingular),
													placeholder("currency-char", currencyChar)

											);
										}
									}
							);
						})
		);
	}

	private void subAddAllOfflineOnline() {
		manager.command(builder.literal("add",
								ArgumentDescription.of("Allows operators add to accounts of players."),
								"give"
						)
						.literal("-all",
								ArgumentDescription.of("Allows operators add to all accounts.")
						)
						.argument(DoubleArgument.of("amount"))
						.handler(handler -> {
							CommandSender sender = handler.getSender();
							double amount = handler.get("amount");
							OperatorManagerImpl operatorManager = main.operatorManager();

							Bukkit.getScheduler().runTaskAsynchronously(
									main,
									() -> {
										String senderName = (sender instanceof ConsoleCommandSender ? "CONSOLE" :
												sender.getName());

										List<PlayerAccount> accounts = main.playerManager().accounts();
										accounts.removeIf(Objects::isNull);

										BukkitDefaultOperator operator;
										if (sender instanceof Player onlinePlayer) {
											operator = operatorManager.getPlayerOperator(onlinePlayer.getUniqueId());
										} else if (sender instanceof ConsoleCommandSender) {
											operator = operatorManager.getConsoleOperator();
										} else {
											operator = BukkitDefaultOperator.toOperator(sender);
										}
										if (operator == null) {
											messageManager.message(sender, "economy-add-unknown-operator-error");
											return;
										}

										for (PlayerAccount account : accounts) {
											if (account == null) {
												continue;
											}

											account.requireSave = false;
											account.operatorAdd(main, operator, amount, null);
											BukkitEntryImpl entry = account.history().latest();
											ICurrency currency = account.currency();
											assert currency != null;
											String currencyPlural = currency.plural();
											String currencySingular = currency.singular();
											String currencyName = currency.name();
											String currencyChar = currency.character();
											messageManager.message(sender, "economy-add-sender",
													placeholder("player", account.name()),
													placeholder("amount", amount),
													placeholder("who", senderName),
													placeholder("new-balance", entry.balanceAfter()),
													placeholder("old-balance", entry.balanceBefore()),
													placeholder("change-balance", entry.balanceChange()),
													placeholder("currency-name", currencyName),
													placeholder("currency-plural", currencyPlural),
													placeholder("currency-singular", currencySingular),
													placeholder("currency-char", currencyChar)
											);
											Player player = Bukkit.getPlayer(account.uniqueId());
											if (player != null) {
												messageManager.message(sender, "economy-add-received",
														placeholder("player", player.getName()),
														placeholder("amount", amount),
														placeholder("who", senderName),
														placeholder("new-balance", entry.balanceAfter()),
														placeholder("old-balance", entry.balanceBefore()),
														placeholder("change-balance", entry.balanceChange()),
														placeholder("currency-name", currencyName),
														placeholder("currency-plural", currencyPlural),
														placeholder("currency-singular", currencySingular),
														placeholder("currency-char", currencyChar)
												);
											}
										}
									}
							);
						})
		);
	}
}