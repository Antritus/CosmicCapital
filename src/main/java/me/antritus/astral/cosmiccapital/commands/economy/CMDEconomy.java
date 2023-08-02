package me.antritus.astral.cosmiccapital.commands.economy;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.antsfactions.MessageManager;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalCommand;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import me.antritus.astral.cosmiccapital.astrolminiapi.ColorUtils;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class CMDEconomy extends CosmicCapitalCommand {
	public CMDEconomy(CosmicCapital main) {
		super(main, "economy");
	}
	private final String gradient = "#1c00a4:#1e008a:#3314a8:#481fb9:#5928c3:#6a3ad2:#8150e3:#a06eff:#beaaff";
	private final Component component = ColorUtils.translateComp("<gradient:"+gradient+"><b>CosmicCapital v"+main.getPluginMeta().getVersion());

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (args.length==0){
			sender.sendMessage(component);
			return true;
		}
		MessageManager messageManager = main.getMessageManager();
		String action = args[0];
		PlayerAccountDatabase database = main.getPlayerDatabase();
		String msg = "economy.";
		if (action.equalsIgnoreCase("give") ||action.equalsIgnoreCase("add")){
			msg = "economy.give.";
			if (!sender.hasPermission("cosmiccapital.economy.give")){
				messageManager.message(sender, msg+"permission", "%command%=economy give <player|-all|-offline|-online> <amount>");
				return true;
			}
			if (args.length==1){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=economy give <player> <amount>");
				return true;
			}
			String arg = args[1];
			if (arg.equalsIgnoreCase("-all") || arg.equalsIgnoreCase("-offline") || arg.equalsIgnoreCase("-online")){
				if (args.length==2){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy give "+args[1] + " <amount>");
					return true;
				}
				if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy give "+args[1] + " <amount>");
					return true;
				}
				double amount = Double.parseDouble(args[2]);
				switch (arg.toLowerCase()){
					case "-all" -> {
						if (!sender.hasPermission("cosmiccapital.economy.give.all")){
							messageManager.message(sender, msg+"all.permission", "%command%=economy give <player|-all|-offline|-online> <amount>");
							return true;
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getOnlinePlayers().forEach(p->{
									PlayerAccount account = database.get(p);
									account.addOperator(sender, amount);
									messageManager.message(p, "economy.give.receive", "%who%="+sender.getName(), "%amount%="+amount);
								});
							}
						}.runTaskAsynchronously(main);
						messageManager.message(sender, "economy.give.send", "%who%=all offline and online players", "%amount%="+amount);
					}
					case "-offline" -> {
						if (!sender.hasPermission("cosmiccapital.economy.give.offline")) {
							messageManager.message(sender, msg + "offline.permission", "%command%=economy give <player|-all|-offline|-online> <amount>");
							return true;
						}
						for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
							new BukkitRunnable() {
								PlayerAccount account = database.get(offlinePlayer.getUniqueId());
								@Override
								public void run() {
									if (account.isLoading()){
										return;
									}
									account.addOperator(sender, amount);
									cancel();
								}
							}.runTaskTimerAsynchronously(main, 0, 20);
						}
						messageManager.message(sender, "economy.give.send", "%who%=all online players", "%amount%="+amount);
					}
					case "-online" -> {
						if (!sender.hasPermission("cosmiccapital.economy.give.online")){
							messageManager.message(sender, msg+"online.permission", "%command%=economy give <player|-all|-offline|-online> <amount>");
							return true;
						}
						Bukkit.getOnlinePlayers().forEach(p->{
							PlayerAccount account = database.get(p);
							account.addOperator(sender, amount);
							messageManager.message(p, "economy.give.receive", "%who%="+sender.getName(), "%amount%="+amount);
						});
					}
				}
				return true;
			}
			OfflinePlayer player = main.getServer().getOfflinePlayer(arg);
			if (!player.hasPlayedBefore()){

				messageManager.message(sender, msg+".player.unknown-player");
				return true;
			}
			if (args.length==2){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy give "+args[1] + " <amount>");
				return true;
			}
			if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy give "+args[1] + " <amount>");
				return true;
			}
			double amount = Double.parseDouble(args[2]);
			if (Double.isNaN(amount)) {
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy give "+args[1] + " <amount>");
				return true;
			}
			PlayerAccount playerAccount = database.get(player.getUniqueId());
			playerAccount.addOperator(sender, amount);
			if (player.isOnline()) {
				messageManager.message((Player) player, "economy.give.receive", "%who%=" + sender.getName(), "%amount%=" + amount);
				messageManager.message(sender, "economy.give.send", "%who%="+player.getName(), "%amount%="+amount);
			}
		}
		if (action.equalsIgnoreCase("set") ||action.equalsIgnoreCase("put")){
			msg = "economy.set.";
			if (!sender.hasPermission("cosmiccapital.economy.set")){
				messageManager.message(sender, msg+"permission", "%command%=economy set <player|-all|-offline|-online> <amount>");
				return true;
			}
			if (args.length==1){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=economy set <player> <amount>");
				return true;
			}
			String arg = args[1];
			if (arg.equalsIgnoreCase("-all") || arg.equalsIgnoreCase("-offline") || arg.equalsIgnoreCase("-online")){
				if (args.length==2){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy set "+args[1] + " <amount>");
					return true;
				}
				if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy set "+args[1] + " <amount>");
					return true;
				}
				double amount = Double.parseDouble(args[2]);
				switch (arg.toLowerCase()){
					case "-all" -> {
						if (!sender.hasPermission("cosmiccapital.economy.set.all")){
							messageManager.message(sender, msg+"all.permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
							return true;
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getOnlinePlayers().forEach(p->{
									PlayerAccount account = database.get(p);
									account.setOperator(sender, amount);
									messageManager.message(p, "economy.set.set", "%who%="+sender.getName(), "%amount%="+amount);
								});
							}
						}.runTaskAsynchronously(main);
						messageManager.message(sender, "economy.set.operator", "%who%=all offline and online players", "%amount%="+amount);
					}
					case "-offline" -> {
						if (!sender.hasPermission("cosmiccapital.economy.set.offline")) {
							messageManager.message(sender, msg + "offline.permission", "%command%=economy set <player|-all|-offline|-online> <amount>");
							return true;
						}
						for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
							new BukkitRunnable() {
								PlayerAccount account = database.get(offlinePlayer.getUniqueId());
								@Override
								public void run() {
									if (account.isLoading()){
										return;
									}
									account.removeOperator(sender, amount);
									cancel();
								}
							}.runTaskTimerAsynchronously(main, 0, 20);
						}
						messageManager.message(sender, "economy.set.removed", "%who%=all offline players", "%amount%="+amount);
					}
					case "-online" -> {
						if (!sender.hasPermission("cosmiccapital.economy.set.online")){
							messageManager.message(sender, msg+"online.permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
							return true;
						}
						Bukkit.getOnlinePlayers().forEach(p->{
							PlayerAccount account = database.get(p);
							account.removeOperator(sender, amount);
							messageManager.message(p, "economy.set.set", "%who%="+sender.getName(), "%amount%="+amount);
						});
						messageManager.message(sender, "economy.set.set", "%who%=all online players", "%amount%="+amount);
					}
				}
				return true;
			}
			OfflinePlayer player = main.getServer().getOfflinePlayer(arg);
			if (!player.hasPlayedBefore()){

				messageManager.message(sender, msg+".player.unknown-player");
				return true;
			}
			if (args.length==2){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy set "+args[1] + " <amount>");
				return true;
			}
			if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy set "+args[1] + " <amount>");
				return true;
			}
			double amount = Double.parseDouble(args[2]);
			if (Double.isNaN(amount)) {
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy set "+args[1] + " <amount>");
				return true;
			}
			PlayerAccount playerAccount = database.get(player.getUniqueId());
			playerAccount.removeOperator(sender, amount);
			if (player.isOnline()) {
				messageManager.message((Player) player, "economy.set.set", "%who%=" + sender.getName(), "%amount%=" + amount);
				messageManager.message(sender, "economy.set.operator", "%who%="+player.getName(), "%amount%="+amount);
			}
		}
		if (action.equalsIgnoreCase("take") ||action.equalsIgnoreCase("remove")){
			msg = "economy.take.";
			if (!sender.hasPermission("cosmiccapital.economy.take")){
				messageManager.message(sender, msg+"permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
				return true;
			}
			if (args.length==1){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=economy take <player> <amount>");
				return true;
			}
			String arg = args[1];
			if (arg.equalsIgnoreCase("-all") || arg.equalsIgnoreCase("-offline") || arg.equalsIgnoreCase("-online")){
				if (args.length==2){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy take "+args[1] + " <amount>");
					return true;
				}
				if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
					messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy take "+args[1] + " <amount>");
					return true;
				}
				double amount = Double.parseDouble(args[2]);
				switch (arg.toLowerCase()){
					case "-all" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.all")){
							messageManager.message(sender, msg+"all.permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
							return true;
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getOnlinePlayers().forEach(p->{
									PlayerAccount account = database.get(p);
									account.removeOperator(sender, amount);
									messageManager.message(p, "economy.take.removed", "%who%="+sender.getName(), "%amount%="+amount);
								});
							}
						}.runTaskAsynchronously(main);
						messageManager.message(sender, "economy.take.remove", "%who%=all offline and online players", "%amount%="+amount);
					}
					case "-offline" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.offline")) {
							messageManager.message(sender, msg + "offline.permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
							return true;
						}
						for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
							new BukkitRunnable() {
								final PlayerAccount account = database.get(offlinePlayer.getUniqueId());
								@Override
								public void run() {
									if (account.isLoading()){
										return;
									}
									account.removeOperator(sender, amount);
									cancel();
								}
							}.runTaskTimerAsynchronously(main, 0, 20);
						}
						messageManager.message(sender, "economy.take.removed", "%who%=all offline players", "%amount%="+amount);
					}
					case "-online" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.online")){
							messageManager.message(sender, msg+"online.permission", "%command%=economy take <player|-all|-offline|-online> <amount>");
							return true;
						}
						Bukkit.getOnlinePlayers().forEach(p->{
							PlayerAccount account = database.get(p);
							account.removeOperator(sender, amount);
							messageManager.message(p, "economy.take.removed", "%who%="+sender.getName(), "%amount%="+amount);
						});
						messageManager.message(sender, "economy.take.remove", "%who%=all online players", "%amount%="+amount);
					}
				}
				return true;
			}
			OfflinePlayer player = main.getServer().getOfflinePlayer(arg);
			if (!player.hasPlayedBefore()){

				messageManager.message(sender, msg+".player.unknown-player");
				return true;
			}
			if (args.length==2){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy take "+args[1] + " <amount>");
				return true;
			}
			if (!isDouble(args[2]) || args[2].equalsIgnoreCase("nan")|| args[2].equalsIgnoreCase("-nan")){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy take "+args[1] + " <amount>");
				return true;
			}
			double amount = Double.parseDouble(args[2]);
			if (Double.isNaN(amount)) {
				messageManager.message(sender, msg+"incorrect-usage", "%command%=/economy take "+args[1] + " <amount>");
				return true;
			}
			PlayerAccount playerAccount = database.get(player.getUniqueId());
			playerAccount.removeOperator(sender, amount);
			if (player.isOnline()) {
				messageManager.message((Player) player, "economy.take.removed", "%who%=" + sender.getName(), "%amount%=" + amount);
				messageManager.message(sender, "economy.take.remove", "%who%="+player.getName(), "%amount%="+amount);
			}
		}
		if (action.equalsIgnoreCase("delete") ||action.equalsIgnoreCase("reset")){
			msg = "economy.take.";
			if (!sender.hasPermission("cosmiccapital.economy.take")){
				messageManager.message(sender, msg+"permission", "%command%=economy reset <player|-all|-offline|-online>");
				return true;
			}
			if (args.length==1){
				messageManager.message(sender, msg+"incorrect-usage", "%command%=economy reset <player|-all|-offline|-online>");
				return true;
			}
			String arg = args[1];
			if (arg.equalsIgnoreCase("-all") || arg.equalsIgnoreCase("-offline") || arg.equalsIgnoreCase("-online")){
				switch (arg.toLowerCase()){
					case "-all" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.all")){
							messageManager.message(sender, msg+"all.permission", "%command%=economy take <player|-all|-offline|-online>");
							return true;
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getOnlinePlayers().forEach(p->{
									PlayerAccount account = database.get(p);
									account.resetOperator(sender);
									messageManager.message(p, "economy.take.removed", "%who%="+sender.getName());
								});
							}
						}.runTaskAsynchronously(main);
						messageManager.message(sender, "economy.take.remove", "%who%=all offline and online players");
					}
					case "-offline" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.offline")) {
							messageManager.message(sender, msg + "offline.permission", "%command%=economy take <player|-all|-offline|-online>");
							return true;
						}
						for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
							new BukkitRunnable() {
								final PlayerAccount account = database.get(offlinePlayer.getUniqueId());
								@Override
								public void run() {
									if (account.isLoading()){
										return;
									}
									account.resetOperator(sender);
									cancel();
								}
							}.runTaskTimerAsynchronously(main, 0, 20);
						}
						messageManager.message(sender, "economy.take.removed", "%who%=all offline players");
					}
					case "-online" -> {
						if (!sender.hasPermission("cosmiccapital.economy.take.online")){
							messageManager.message(sender, msg+"online.permission", "%command%=economy take <player|-all|-offline|-online>");
							return true;
						}
						Bukkit.getOnlinePlayers().forEach(p->{
							PlayerAccount account = database.get(p);
							account.resetOperator(sender);
							messageManager.message(p, "economy.take.removed", "%who%="+sender.getName());
						});
						messageManager.message(sender, "economy.take.remove", "%who%=all online players");
					}
				}
				return true;
			}
			OfflinePlayer player = main.getServer().getOfflinePlayer(arg);
			if (!player.hasPlayedBefore()){
				messageManager.message(sender, msg+".player.unknown-player");
				return true;
			}
			PlayerAccount playerAccount = database.get(player.getUniqueId());
			playerAccount.resetOperator(sender);
			if (player.isOnline()) {
				messageManager.message((Player) player, "economy.reset.reset", "%who%=" + sender.getName());
				messageManager.message(sender, "economy.reset.operator", "%who%="+player.getName());
			}
		}
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1){
			// 0
			return List.of("add", "remove", "set", "reset");
		}
		if (args.length==2){
			// 1
			List<String> players = new ArrayList<>();
			main.getServer().getOnlinePlayers().forEach(p->{players.add(p.getName());});
			players.addAll(List.of("-offline", "-online", "-all"));
			return players;
		}
		if (args.length==3){
			String arg = args[0];
			if (is(arg, "add") || is(arg, "give") || is(arg, "remove")
			|| is(arg, "take") || is(arg, "set") || is(arg, "put")){
				return List.of(10+"", 25+"", 50+"", 75+"", 100+"", 125+"", 150+"", 250+"", 1000+"");
			}
		}
		return Collections.singletonList("");
	}
	private boolean is(String string, String what){
		return string.equalsIgnoreCase(what);
	}
}
