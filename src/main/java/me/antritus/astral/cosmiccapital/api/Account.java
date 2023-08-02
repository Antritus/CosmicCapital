package me.antritus.astral.cosmiccapital.api;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.antsfactions.IUser;
import me.antritus.astral.cosmiccapital.antsfactions.Property;
import me.antritus.astral.cosmiccapital.antsfactions.SimpleProperty;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class Account implements IUser {
	private final CosmicCapital cosmicCapital;
	private final HashMap<String, SimpleProperty<?>> nonSavedSettings = new HashMap<>();
	private final List<Entry> history = new ArrayList<>();
	private final String type;
	private final double maxBalance = 1_000_000_000_000D;
	private double balance = 0.0;
	private boolean isLoading;

	public Account(CosmicCapital cosmicCapital, String type){
		this.cosmicCapital = cosmicCapital;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@CreatesEntry(createsEntry = true)
	private void receiveTransfer(Account from, double amount) {
		balance+=amount;
		history.add(Entry.receiveTransfer(this, from, amount));
		checkBalance();

	}

	@CreatesEntry(createsEntry = true)
	public void sendTransfer(Account to, double amount){
		balance-=amount;
		to.receiveTransfer(this, amount);
		history.add(Entry.sendTransfer(this, to, amount));
		checkBalance();
	}

	@CreatesEntry(createsEntry = true)
	public void setOperator(CommandSender operator, double amount){
		balance=amount;
		history.add(Entry.operatorSet(this, operator, amount));
	}
	@CreatesEntry(createsEntry = true)
	public void addOperator(CommandSender operator, double amount){
		balance+=amount;
		history.add(Entry.operatorAdd(this, operator, amount));
	}
	@CreatesEntry(createsEntry = true)
	public void removeOperator(CommandSender operator, double amount){
		balance-=amount;
		history.add(Entry.operatorRemove(this, operator, amount));
	}
	@CreatesEntry(createsEntry = true)
	public void resetOperator(CommandSender operator){
		balance=0;
		history.add(Entry.operatorReset(this, operator));
	}

	@CreatesEntry(createsEntry = true)
	public void withdraw(BanknoteAccount account, double amount){
		balance-=amount;
		account.withdrawReceive(this, amount);
		history.add(Entry.createBanknote(this, account, amount));
	}
	@CreatesEntry(createsEntry = true)
	protected void withdrawReceive(Account account, double amount){
		balance+=amount;
		history.add(Entry.receiveTransfer(this, account, amount));
	}


	private void checkBalance(){
		if (balance>maxBalance){
			balance = maxBalance;
		}
	}

	public double getBalance() {
		return balance;
	}

	@Override
	public @Nullable Property<String, ?> get(@NotNull String key) {
		return nonSavedSettings.get(key);
	}

	@Override
	public @NotNull Map<String, SimpleProperty<?>> get() {
		return nonSavedSettings;
	}

	@Override
	public void setting(@NotNull String key, @Nullable Object value) {
		nonSavedSettings.put(key, new SimpleProperty<>(key, value));
	}

	/**
	 * This method is used to directly set balance the balance of the account.
	 * This does not make any changes to the Entry.java history.
	 * This should only be used for debugging and setting the load amount of money.
	 * (When loading from the database!)
	 * <p>
	 *     Please use the methods which make new entries
	 * </p>
	 * @param balance new Balance
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@CreatesEntry(createsEntry = false)
	@Deprecated(forRemoval = true)
	public void setBalance(double balance){
		this.balance = balance;
	}
	public abstract UUID uniqueId();
	public abstract String name();

	public CosmicCapital getCosmicCapital() {
		return cosmicCapital;
	}

	public void setLoading(boolean b) {
		isLoading = b;
	}
	public boolean isLoading(){
		return isLoading;
	}
}
