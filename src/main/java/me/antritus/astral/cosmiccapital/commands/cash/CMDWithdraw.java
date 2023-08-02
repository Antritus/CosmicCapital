package me.antritus.astral.cosmiccapital.commands.cash;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.antsfactions.MessageManager;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalCommand;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CMDWithdraw extends CosmicCapitalCommand {
	public CMDWithdraw(CosmicCapital main) {
		super(main, "withdraw");
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
		if (!isDouble(args[0]) || args[0].equalsIgnoreCase("nan")|| args[0].equalsIgnoreCase("-nan")){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=withdraw <amount>");
			return true;
		}
		double parsed = Double.parseDouble(args[0]);
		if (Double.isNaN(parsed)){
			messageManager.message(sender, "command-parse.incorrect-format", "%command%=withdraw <amount>");
			return true;
		}
		if(parsed<=0){
			messageManager.message(sender, "withdraw.negative", "%command%=/withdraw <amount>", "%amount%="+parsed);
			return true;
		}
		PlayerAccountDatabase database = main.getPlayerDatabase();
		PlayerAccount account = database.get(player);
		if (account.getBalance()<parsed){
			messageManager.message(player, "withdraw.no-funds", "%command%=/withdraw <amount>", "%amount%="+parsed);
			return true;
		}

		ItemStack itemStack = main.getBanknoteAccountManager().create(account, parsed);
		HashMap<Integer, ItemStack> items = player.getInventory().addItem(itemStack);
		if (items.size()>0){
			items.forEach((id, item)->{
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			});
		}
		messageManager.message(player, "withdraw.create", "%amount%="+parsed);
		return true;
	}
}
