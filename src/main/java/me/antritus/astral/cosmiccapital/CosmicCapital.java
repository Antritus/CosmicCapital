package me.antritus.astral.cosmiccapital;

import com.github.antritus.astral.messages.MessageManager;
import me.antritus.astral.cosmiccapital.api.*;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.providers.CosmicCapitalProvider;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import me.antritus.astral.cosmiccapital.commands.Command;
import me.antritus.astral.cosmiccapital.currencies.Currency;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import me.antritus.astral.cosmiccapital.database.h2.H2PlayerAccountDatabase;
import me.antritus.astral.cosmiccapital.database.mysql.MySQLPlayerAccountDatabase;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import me.antritus.astral.cosmiccapital.manager.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class CosmicCapital extends JavaPlugin implements CosmicCapitalAPI, IEconomyProvider {
	private MessageManager messageManager;
	private PlayerAccountListener playerAccountListener;
	private AccountManagerRegistryManager accountRegistryManager;
	private AccountDatabase<PlayerAccount> playerAccountManager;
	private CurrencyManager currencyManager;
	private OperatorManagerImpl operatorManager;
	private BankManagerImpl bankManager;
	private WorldManagerImpl worldManager;

	@Override
	public void onEnable() {
		switch (getConfig().getString("database.player.type", "database.player.type").toLowerCase()){
			case "mysql" -> {
				playerAccountManager = new MySQLPlayerAccountDatabase<>(this, PlayerAccount.class, "global");
			}
			case "h2", "default" -> {
				playerAccountManager = new H2PlayerAccountDatabase<>(this, PlayerAccount.class, "global");
			}
			default -> {
				playerAccountManager = null;
				getServer().getPluginManager().disablePlugin(this);
				throw  new IllegalArgumentException("Illegal configuration! Database type is unknown for database.player.type!");
			}
		}
		playerAccountManager.createTable();
		ICurrency.CharDisplay charDisplay = null;
		try {
			charDisplay = ICurrency.CharDisplay.valueOf(getConfig().getString("currency-default.display", "FRONT").toUpperCase());
		} catch (IllegalArgumentException ignore){
			charDisplay = ICurrency.CharDisplay.NEVER;
		}
		Currency currencyDefault = new Currency(this,
				getConfig().getString("currency-default.name", "Dollar"),
				getConfig().getString("currency-default.plural", "dollars"),
				getConfig().getString("currency-default.singular", "dollar"),
				getConfig().getString("currency-default.character", "$"),
				charDisplay
				);
		currencyManager = new CurrencyManager(currencyDefault);

		accountRegistryManager = new AccountManagerRegistryManager();
		// Make sure no other plugins try to register the player account manager for normal currencies.
		accountRegistryManager.register(playerAccountManager, PlayerAccount.class);

		operatorManager = new OperatorManagerImpl();

		messageManager = new MessageManager(this);

		playerAccountListener = new PlayerAccountListener(this);
		playerAccountListener.onEnable();

		new Command(this);


		worldManager = new WorldManagerImpl(this);
		registerListener(worldManager);


		Class<CosmicCapitalProvider> providerClass = CosmicCapitalProvider.class;
		try {
			Field apiField =providerClass.getDeclaredField("api");
			apiField.setAccessible(true);
			apiField.set(null, this);
			apiField.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	public void registerListener(Listener listener){
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@Override
	public void onDisable(){
		playerAccountListener.onDisable();
	}

	public MessageManager messageManager(){
		return messageManager;
	}
	public PlayerAccountListener playerAccountListener() {
		return playerAccountListener;
	}

	public AccountManagerRegistryManager accountRegistryManager() {
		return accountRegistryManager;
	}


	@Override
	public OperatorManagerImpl operatorManager() {
		return operatorManager;
	}

	@Override
	public @NotNull AccountDatabase<PlayerAccount> playerManager() {
		return playerAccountManager;
	}

	@Override
	public @NotNull IAccountManager banknoteManager() {
		return null;
	}

	@Override
	public @NotNull IAccountManager bankManager() {
		return multiBankManager;
	}

	@Override
	public @NotNull CurrencyManager currencyManager() {
		return currencyManager;
	}

	@Override
	public WorldManagerImpl worldManager() {
		return null;
	}

	@Override
	public @NotNull String getVersion() {
		return getPluginMeta().getVersion();
	}

	@Override
	public @Nullable CosmicCapital getPlugin() {
		return this;
	}
}
