package me.antritus.astral.cosmiccapital;

import me.antritus.astral.cosmiccapital.antsfactions.FactionsPlugin;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalCommand;
import me.antritus.astral.cosmiccapital.astrolminiapi.CommandConfiguration;
import me.antritus.astral.cosmiccapital.commands.cash.CMDWithdraw;
import me.antritus.astral.cosmiccapital.commands.economy.CMDBalance;
import me.antritus.astral.cosmiccapital.commands.economy.CMDEconomy;
import me.antritus.astral.cosmiccapital.commands.economy.CMDPay;
import me.antritus.astral.cosmiccapital.database.BanknoteDatabase;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import me.antritus.astral.cosmiccapital.manager.BanknoteAccountManager;
import me.antritus.astral.cosmiccapital.manager.PlayerAccountManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class CosmicCapital extends FactionsPlugin {
	private final CommandConfiguration commandConfiguration;
	private PlayerAccountDatabase playerAccountDatabase;
	private PlayerAccountManager playerAccountManager;
	private BanknoteAccountManager banknoteAccountManager;
	private BanknoteDatabase banknoteDatabase;

	public CosmicCapital(){
		commandConfiguration = new CommandConfiguration(this);
		try {
			commandConfiguration.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}

	}


	@Override
	public void updateConfig(@Nullable String oldVersion, String newVersion) {
	}

	@Override
	public void enable() {
//		enableDatabase();
		enableCommands();
		playerAccountDatabase = new PlayerAccountDatabase(this);
		playerAccountManager = new PlayerAccountManager(this);
		playerAccountManager.onEnable();
		banknoteDatabase = new BanknoteDatabase(this);
		banknoteAccountManager = new BanknoteAccountManager(this);
		getServer().getPluginManager().registerEvents(playerAccountManager, this);
		getServer().getPluginManager().registerEvents(banknoteAccountManager, this);
	}

	@Override
	public void startDisable() {

	}

	@Override
	public void disable() {
		playerAccountManager.onDisable();
	}





	private void enableCommands(){
		List<CosmicCapitalCommand> commands = new ArrayList<>();
		commands.add(new CMDEconomy(this));
		commands.add(new CMDBalance(this));
		commands.add(new CMDPay(this));
		commands.add(new CMDWithdraw(this));
		commands.forEach(command->{
			commandConfiguration.load(command);
			command.registerCommand();
		});
	}

	public CommandConfiguration getCommandConfiguration() {
		return commandConfiguration;
	}

	public PlayerAccountDatabase getPlayerDatabase() {
		return playerAccountDatabase;
	}

	public PlayerAccountManager getPlayerManager() {
		return playerAccountManager;
	}

	public BanknoteAccountManager getBanknoteAccountManager() {
		return banknoteAccountManager;
	}

	public BanknoteDatabase getBankNoteDatabase() {
		return banknoteDatabase;
	}
}
