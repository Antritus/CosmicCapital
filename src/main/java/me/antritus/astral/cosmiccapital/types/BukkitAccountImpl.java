package me.antritus.astral.cosmiccapital.types;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import me.antritus.astral.cosmiccapital.api.IEconomyProvider;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.api.types.currency.CurrencyBundle;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryCurrencyData;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryType;
import me.antritus.astral.cosmiccapital.api.types.operator.Operator;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class BukkitAccountImpl implements IAccount {
	private final Map<String, Double> balances = new HashMap<>();
	@Expose(deserialize = false, serialize = false)
	public boolean requireSave = false;
	@Expose
	protected final BukkitHistoryImpl history = new BukkitHistoryImpl(this);
	@Expose
	protected final UUID uniqueId;
	@Expose
	protected final String name;
	@Expose(deserialize = false, serialize = false)
	public long lifespan = -10000L;
	@Expose
	private long created;

	public BukkitAccountImpl(@NotNull OfflinePlayer player) {
		this.uniqueId = player.getUniqueId();
		this.name = player.getName();
	}

	public BukkitAccountImpl(@NotNull UUID uniqueId, @NotNull String name){
		this.uniqueId = uniqueId;
		this.name = name;
	}
	public BukkitAccountImpl(){
		this.uniqueId = null;
		this.name = null;
	}

	@Override
	public @NotNull BukkitHistoryImpl history() {
		return history;
	}

	@Override
	public @NotNull UUID uniqueId() {
		return uniqueId;
	}

	@Override
	public @NotNull String name() {
		return name;
	}

	@Override
	public long created() {
		return created;
	}

	@Override
	public void resetHistory(@NotNull IEconomyProvider iEconomyProvider) {

	}

	@Override
	public @Nullable ICurrency[] currency() {
		return new ICurrency[0];
	}

	@Override
	public double balance(ICurrency iCurrency) {
		return 0;
	}

	@Override
	public double balance(String s) {
		return 0;
	}

	@Override
	public CurrencyBundle[] currencyBundles() {
		return new CurrencyBundle[0];
	}
	private final CurrencyBundleReflections reflections = new CurrencyBundleReflections();

	static final class CurrencyBundleReflections {
		private final Class<CurrencyBundle> bundleClass = CurrencyBundle.class;
		final private Field amountField;

		{
			try {
				amountField = bundleClass.getDeclaredField("amount");
				amountField.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}

		public void combine(CurrencyBundle original, CurrencyBundle addToOriginal){
			double originalAmount = original.amount();
			double copyAmount = addToOriginal.amount();
			try {
				amountField.set(original, originalAmount+copyAmount);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	CurrencyBundle[] combine(CurrencyBundle... currencyBundles){
		List<CurrencyBundle> bundles = new ArrayList<>();
		for (CurrencyBundle currencyBundle : currencyBundles){
			if (bundles.isEmpty()){
				bundles.add(currencyBundle);
			}
			boolean found = false;
			for (CurrencyBundle bundle : bundles){
				if (bundle.currencyName() .equalsIgnoreCase(currencyBundle.currencyName().toLowerCase())){
					reflections.combine(currencyBundle, bundle);
					found = true;
					break;
				}
			}
			if (!found){
				bundles.add(currencyBundle);
			}
		}
		return Arrays.stream(currencyBundles).toArray(CurrencyBundle[]::new);
	}
	EntryCurrencyData[] addBalance(CurrencyBundle... currencyBundles){
		List<EntryCurrencyData> data = new LinkedList<>();
		for (CurrencyBundle currencyBundle : combine(currencyBundles)){
			this.balances.putIfAbsent(currencyBundle.currencyName().toLowerCase(), 0.0D);
			double balance = balances.get(currencyBundle.currencyName().toLowerCase());
			double amount = currencyBundle.amount();
			double after = balance+amount;
			EntryCurrencyData currencyData = new EntryCurrencyData(
					currencyBundle.currencyName().toLowerCase(),
					balance,
					after
			);
			this.balances.put(currencyBundle.currencyName().toLowerCase(), after);
			data.add(currencyData);
		}
		return data.toArray(new EntryCurrencyData[0]);
	}
	EntryCurrencyData[] setBalance(CurrencyBundle... currencyBundles){
		List<EntryCurrencyData> data = new LinkedList<>();
		for (CurrencyBundle currencyBundle : combine(currencyBundles)){
			this.balances.putIfAbsent(currencyBundle.currencyName().toLowerCase(), 0.0D);
			double balance = balances.get(currencyBundle.currencyName().toLowerCase());
			double after = currencyBundle.amount();
			EntryCurrencyData currencyData = new EntryCurrencyData(
					currencyBundle.currencyName().toLowerCase(),
					balance,
					after
			);
			this.balances.put(currencyBundle.currencyName().toLowerCase(), after);
			data.add(currencyData);
		}
		return data.toArray(new EntryCurrencyData[0]);
	}
	EntryCurrencyData[] removeBalance(CurrencyBundle... currencyBundles){
		List<EntryCurrencyData> data = new LinkedList<>();
		for (CurrencyBundle currencyBundle : combine(currencyBundles)){
			this.balances.putIfAbsent(currencyBundle.currencyName().toLowerCase(), 0.0D);
			double balance = balances.get(currencyBundle.currencyName().toLowerCase());
			double amount = currencyBundle.amount();
			double after = balance-amount;
			EntryCurrencyData currencyData = new EntryCurrencyData(
					currencyBundle.currencyName().toLowerCase(),
					balance,
					after
			);
			this.balances.put(currencyBundle.currencyName().toLowerCase(), after);
			data.add(currencyData);
		}
		return data.toArray(new EntryCurrencyData[0]);
	}
	EntryCurrencyData[] resetBalance(){
		List<EntryCurrencyData> data = new LinkedList<>();
		for (String currency : balances.keySet()){
			data.add(
					new EntryCurrencyData(
							currency,
							balances.get(currency.toLowerCase()),
							0
					)
			);
		}
		return data.toArray(new EntryCurrencyData[0]);
	}

	EntryCurrencyData resetBalance(String currency){
		EntryCurrencyData data = new EntryCurrencyData(
				currency,
				balances.get(currency) != null ? balances.get(currency) : 0D,
				0D);
		balances.remove(currency.toLowerCase());
		return data;
	}

	@Override
	public void transfer(@NotNull IEconomyProvider iEconomyProvider, @NotNull IAccount iAccount, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.REMOVE,
				iAccount,
				null,
				jsonObject,
				removeBalance(currencyBundles));
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void receive(IEconomyProvider iEconomyProvider, IAccount iAccount, JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.ADD,
				iAccount,
				null,
				jsonObject,
				addBalance(currencyBundles));
	}

	@Override
	public void operatorSet(@NotNull IEconomyProvider iEconomyProvider, @NotNull Operator operator, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.SET,
				null,
				operator,
				jsonObject,
				setBalance(currencyBundles));
	}

	@Override
	public void operatorReset(@NotNull IEconomyProvider iEconomyProvider, ICurrency iCurrency, @NotNull Operator operator, @Nullable JsonObject jsonObject) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.RESET,
				null,
				operator,
				jsonObject,
				resetBalance());

	}

	@Override
	public void operatorAdd(@NotNull IEconomyProvider iEconomyProvider, @NotNull Operator operator, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.ADD,
				null,
				operator,
				jsonObject,
				addBalance(currencyBundles));
	}


	@Override
	public void operatorRemove(@NotNull IEconomyProvider iEconomyProvider, @NotNull Operator operator, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		history.newHistoryEntry(
				iEconomyProvider,
				EntryType.REMOVE,
				null,
				operator,
				jsonObject,
				removeBalance(currencyBundles));
	}
	private void custom(IEconomyProvider economyProvider, IAccount account, Operator operator, CustomAction action, JsonObject jsonData, CurrencyBundle... bundles){
		EntryCurrencyData[] data;
		EntryType type = action.entryType();
		switch (action){
			case REMOVE -> {
				data = removeBalance(bundles);
			}
			case ADD -> {
				data = addBalance(bundles);
			}
			case SET -> {
				data = setBalance(bundles);
			}
			default -> throw new IllegalStateException("Unexpected value: " + action);
		}
		history.newHistoryEntry(
				economyProvider,
				type,
				account,
				operator,
				jsonData,
				data);
	}
	private void customReset(IEconomyProvider economyProvider, IAccount account, Operator operator, JsonObject jsonData){
		EntryCurrencyData[] data = resetBalance();
		EntryType type = EntryType.RESET;
		history.newHistoryEntry(
				economyProvider,
				type,
				account,
				operator,
				jsonData,
				data);
		balances.clear();
	}
	private void customReset(IEconomyProvider economyProvider, String currency, IAccount account, Operator operator, JsonObject jsonData){
		EntryCurrencyData[] data = resetBalance();
		EntryType type = EntryType.RESET;
		history.newHistoryEntry(
				economyProvider,
				type,
				account,
				operator,
				jsonData,
				data);
		balances.clear();
	}

	@Override
	public void custom(@NotNull IEconomyProvider iEconomyProvider, @NotNull IAccount iAccount, @NotNull CustomAction customAction, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		this.custom(iEconomyProvider, iAccount, null, customAction, jsonObject, currencyBundles);
	}

	@Override
	public void custom(@NotNull IEconomyProvider iEconomyProvider, @NotNull Operator operator, @NotNull CustomAction customAction, @Nullable JsonObject jsonObject, CurrencyBundle... currencyBundles) {
		this.custom(iEconomyProvider, null, operator, customAction, jsonObject, currencyBundles);
	}

	@Override
	public void customReset(@NotNull IEconomyProvider iEconomyProvider, ICurrency iCurrency, @NotNull Operator operator, @Nullable JsonObject jsonObject) {
		customReset(iEconomyProvider, iCurrency, );
	}

	@Override
	public void customReset(@NotNull IEconomyProvider iEconomyProvider, @NotNull Operator operator, @Nullable JsonObject jsonObject) {

	}

	@Override
	public void customReset(@NotNull IEconomyProvider iEconomyProvider, ICurrency iCurrency, @Nullable JsonObject jsonObject) {

	}

	@Override
	public void customReset(@NotNull IEconomyProvider iEconomyProvider, @Nullable JsonObject jsonObject) {

	}


}
