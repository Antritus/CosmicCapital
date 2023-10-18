package me.antritus.astral.cosmiccapital.commands.balance;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.github.antritus.astral.messages.MessageManager;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.antritus.astral.cosmiccapital.commands.Command.placeholder;

public class BalanceCMD {
	private final MessageManager messageManager;
	private final CosmicCapital main;
	private final Command.Builder<CommandSender> builder;
	private final PaperCommandManager<CommandSender> manager;

	public BalanceCMD(MessageManager messageManager, CosmicCapital main, Command.Builder<CommandSender> builder, PaperCommandManager<CommandSender> manager) {
		this.messageManager = messageManager;
		this.main = main;
		this.builder = builder;
		this.manager = manager;
		balance();
		balanceOther();
	}

	private void balance(){
		manager.command(builder
				.senderType(Player.class)
				.handler(handler->{
					Player player = (Player) handler.getSender();
					AccountDatabase database = main.playerManager();
					PlayerAccount account = database.getKnownNonNull(player.getName());
					ICurrency currency = account.currency();
					if (currency == null){
						player.sendRichMessage("Unknown Error");
					}
					assert currency != null;
					messageManager.message(player,
							"balance-player",
							placeholder("player", player.getName()),
							placeholder("balance", account.balance()),
							placeholder("currency-name", currency.name()),
							placeholder("currency-char", currency.character()),
							placeholder("currency-singular", currency.singular()),
							placeholder("currency-plural", currency.plural())
							);
				}));
	}
	private void balanceOther(){
		manager.command(builder.argument(
				OfflinePlayerArgument.of("player"))
				.handler(handler->{
					OfflinePlayer player = handler.get("player");
					Bukkit.getScheduler().runTaskAsynchronously(main,
							() -> {
								String name = (handler.getSender() instanceof ConsoleCommandSender ? "CONSOLE" : handler.getSender().getName());
								AccountDatabase accountDatabase = main.playerManager();
								PlayerAccount playerAccount = accountDatabase.get(player.getUniqueId());
								if (playerAccount == null){
									messageManager.message(handler.getSender(),
											"balance-unknown-error-account");
									return;
								}
								ICurrency currency = playerAccount.currency();
								assert currency != null;

								if (handler.getSender() instanceof Player senderPlayer
										&& senderPlayer.getUniqueId().equals(playerAccount.uniqueId())){
									messageManager.message(handler.getSender(),
											placeholder("balance-player", player.getName()),
											placeholder("balance", playerAccount.balance()),
											placeholder("who", name),
											placeholder("player", player.getName()),
											placeholder("currency-name", currency.name()),
											placeholder("currency-char", currency.character()),
											placeholder("currency-singular", currency.singular()),
											placeholder("currency-plural", currency.plural())

									);
									return;
								}
								messageManager.message(handler.getSender(),
										placeholder("balance-other", player.getName()),
										placeholder("balance", playerAccount.balance()),
										placeholder("who", name),
										placeholder("player", player.getName()),
										placeholder("currency-name", currency.name()),
										placeholder("currency-char", currency.character()),
										placeholder("currency-singular", currency.singular()),
										placeholder("currency-plural", currency.plural())
								);
							}
					);
				})
		);
	}
}
