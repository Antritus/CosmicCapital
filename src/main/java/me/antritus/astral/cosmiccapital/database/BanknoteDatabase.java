package me.antritus.astral.cosmiccapital.database;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.Account;
import me.antritus.astral.cosmiccapital.api.BanknoteAccount;
import me.antritus.astral.cosmiccapital.astrolminiapi.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BanknoteDatabase {
	Configuration databaseYML;
	private final Map<UUID, BanknoteAccount> accounts = new HashMap<>();
	private final CosmicCapital cosmicCapital;

	public BanknoteDatabase(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
		databaseYML = new Configuration(cosmicCapital, "banknote-database.yml");
		try {
			databaseYML.load();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public BanknoteAccount get(UUID uniqueId){
		if (accounts.get(uniqueId) == null){
			load(uniqueId);
		}
		return accounts.get(uniqueId);
	}

	public void load(UUID uuid){
		BanknoteAccount account = new BanknoteAccount(cosmicCapital, uuid);
		account.setLoading(true);
		if (databaseYML.get(uuid.toString()) != null){
			if (databaseYML.isString(account.uniqueId()+".balance") && databaseYML.getString(account.uniqueId()+".balance").equalsIgnoreCase("CLAIMEd")){
				return;
			}
			List<String> historyList = databaseYML.getStringList(uuid.toString()+".history");
			historyList.forEach(history->{
				//TODO
				 });
			//noinspection removal
			account.setBalance(databaseYML.getDouble(uuid.toString()+".balance"));
		}
		accounts.put(account.uniqueId(), account);
		account.setLoading(false);
	}

	public boolean isValid(UUID uuid){
		return databaseYML.isSet(uuid.toString());
	}

	private void save(UUID uniqueId) {
		BanknoteAccount account = accounts.get(uniqueId);
		if (account == null){
			return;
		}
		databaseYML.set(uniqueId.toString()+".balance", account.getBalance());
		databaseYML.set(uniqueId.toString()+".history", "[]");
	}

	private void unload(UUID uniqueId) {
		accounts.remove(uniqueId);
	}

	public void save(UUID uniqueId, boolean unload) {
		new BukkitRunnable() {
			@Override
			public void run() {
				save(uniqueId);
				if (unload){
					unload(uniqueId);
				}
			}
		}.runTaskAsynchronously(cosmicCapital);
	}

	public BanknoteAccount create(Account account, double amount) {
		BanknoteAccount banknoteAccount = new BanknoteAccount(cosmicCapital, UUID.randomUUID());
		account.withdraw(banknoteAccount, amount);
		accounts.put(banknoteAccount.uniqueId(), banknoteAccount);
		return banknoteAccount;
	}

	public void delete(BanknoteAccount banknoteAccount) {
		databaseYML.set(banknoteAccount.uniqueId()+".balance", "CLAIMED");
		databaseYML.set(banknoteAccount.uniqueId()+".history", "CLAIMED");
	}
}
