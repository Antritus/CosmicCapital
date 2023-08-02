package me.antritus.astral.cosmiccapital.commands.economy;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.antsfactions.MessageManager;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalCommand;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import me.antritus.astral.cosmiccapital.events.CosmicCapitalPlayerParseEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class CMDPay extends CosmicCapitalCommand {
	public CMDPay(CosmicCapital main) {
		super(main, "pay");
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		MessageManager messageManager = main.getMessageManager();
		if (!(sender instanceof Player player)){
			messageManager.message(sender, "command-parse.player-only", "%command%=balance");
			return true;
		}
		if (args.length==0){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=pay <player> <amount>");
			return true;
		}
		Player other = main.getServer().getPlayer(args[0]);
		if (other == null){
			messageManager.message(sender, "pay.unknown-player", "%command%=/pay <player> <amount>");
			return true;
		}
		if (args.length==1){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=/pay <player> <amount>");
			return true;
		}
		if (!isDouble(args[1]) || args[1].equalsIgnoreCase("nan")|| args[1].equalsIgnoreCase("-nan")){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=/pay <player> <amount>");
			return true;
		}
		double parsed = Double.parseDouble(args[1]);
		if (Double.isNaN(parsed)){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=/pay <player> <amount>");
			return true;
		}
		if(parsed<=0){
			messageManager.message(sender, "pay.negative", "%command%=/pay <player> <amount>", "%amount%="+parsed);
			return true;
		}
		PlayerAccountDatabase database = main.getPlayerDatabase();
		PlayerAccount account = database.get(player);
		if (account.getBalance()<parsed){
			messageManager.message(player, "pay.no-funds", "%command%=/pay <player> <amount>", "%amount%="+parsed);
			return true;
		}
		PlayerAccount otherAcc = database.get(other);
		account.sendTransfer(otherAcc, parsed);
		messageManager.message(player, "pay.send", "%amount%="+parsed, "%who%="+other.getName());
		messageManager.message(other, "pay.received", "%amount%="+parsed, "%who%="+player.getName());
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1){
			List<String> players = new ArrayList<>();
			main.getServer().getOnlinePlayers().forEach(p->{players.add(p.getName());});
			return players;
		}
		if (args.length==2){
			return List.of(10+"", 25+"", 50+"", 75+"", 100+"", 125+"", 150+"", 250+"", 1000+"", 5000+"",10000+"");
		}
		return Collections.singletonList("");
	}
}
