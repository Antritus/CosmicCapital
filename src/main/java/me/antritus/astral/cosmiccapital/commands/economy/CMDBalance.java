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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class CMDBalance extends CosmicCapitalCommand {
	public CMDBalance(CosmicCapital main) {
		super(main, "balance");
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		MessageManager messageManager = main.getMessageManager();
		if (!(sender instanceof Player player)){
			messageManager.message(sender, "command-parse.player-only", "%command%=balance");
			return true;
		}
		PlayerAccountDatabase database = main.getPlayerDatabase();
		if (args.length == 0){
			PlayerAccount playerAccount = database.get(player);
			messageManager.message(sender, "balance.self", "%balance%="+playerAccount.getBalance());
		} else {
			if (args[0].equalsIgnoreCase(player.getName())){
				PlayerAccount playerAccount = database.get(player);
				messageManager.message(sender, "balance.self", "%balance%="+playerAccount.getBalance());
				return true;
			}
			OfflinePlayer oPlayer = main.getServer().getOfflinePlayer(args[0]);
			Player otherPlayer = main.getServer().getPlayer(args[0]);
			if (otherPlayer== null){
				messageManager.message(player, "balance.unknown-player", "%who%="+args[0], "%command%=balance [player]");
				return true;
			}
			CosmicCapitalPlayerParseEvent event = new CosmicCapitalPlayerParseEvent(main, this, otherPlayer);
			event.callEvent();
			if (event.isCancelled()){
				return true;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					PlayerAccount playerAccount = database.get(oPlayer.getUniqueId());
					messageManager.message(player, "balance.other", "%balance%="+playerAccount.getBalance(), "%who%="+oPlayer.getName());
				}
			}.runTaskAsynchronously(main);
		}
		return true;
	}
	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1){
			List<String> players = new ArrayList<>();
			main.getServer().getOnlinePlayers().forEach(p->{players.add(p.getName());});
			return players;
		}
		return Collections.singletonList("");
	}
}
