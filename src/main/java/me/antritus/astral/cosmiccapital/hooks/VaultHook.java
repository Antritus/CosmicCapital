package me.antritus.astral.cosmiccapital.hooks;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import me.antritus.astral.cosmiccapital.manager.WorldManagerImpl;
import me.antritus.astral.cosmiccapital.types.BukkitAccountImpl;
import me.antritus.astral.cosmiccapital.utils.NumberUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VaultHook implements Economy {
	private final @NotNull CosmicCapital cosmicCapital;

	public VaultHook(@NotNull CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
	}

	@Override
	public boolean isEnabled() {
		return cosmicCapital.isEnabled();
	}

	@Override
	public String getName() {
		return cosmicCapital.getName();
	}

	@Override
	public boolean hasBankSupport() {
		return cosmicCapital.singleBankManager().isEnabled();
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public String format(double amount) {
		return NumberUtils.properFormat(amount);
	}

	@Override
	public String currencyNamePlural() {
		return cosmicCapital.mainCurrency().plural();
	}

	@Override
	public String currencyNameSingular() {
		return cosmicCapital.mainCurrency().singular();
	}

	@Override
	public boolean hasAccount(String playerName) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
		if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()){
			return false;
		}
		AccountDatabase<PlayerAccount> database = cosmicCapital.playerManager();
		PlayerAccount playerAccount = (PlayerAccount) database.get(playerName);
		return playerAccount != null;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		if (!player.hasPlayedBefore() && !player.isOnline()){
			return false;
		}
		AccountDatabase<PlayerAccount> database = cosmicCapital.playerManager();
		PlayerAccount playerAccount = database.get(player.getUniqueId());
		return playerAccount != null;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		WorldManagerImpl worldManager = cosmicCapital.worldManager();

		return false;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return false;
	}

	@Override
	public double getBalance(String playerName) {
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return 0;
	}

	@Override
	public double getBalance(String playerName, String world) {
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return 0;
	}

	@Override
	public boolean has(String playerName, double amount) {
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return false;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return false;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return null;
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return null;
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return null;
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return null;
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return null;
	}

	@Override
	public List<String> getBanks() {
		return null;
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return false;
	}
}
