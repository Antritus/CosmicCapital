package me.antritus.astral.cosmiccapital.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import me.antritus.astral.cosmiccapital.api.IEconomyProvider;
import me.antritus.astral.cosmiccapital.api.types.IHistory;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.api.types.currency.CurrencyBundle;
import me.antritus.astral.cosmiccapital.api.types.entry.Entry;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryCurrencyData;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryType;
import me.antritus.astral.cosmiccapital.api.types.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BukkitHistoryImpl implements IHistory {
	@Expose(serialize = false, deserialize = false)
	private final IAccount account;
	@Expose()
	private final List<BukkitEntryImpl> entries = new LinkedList<>();
	@Expose(deserialize = false, serialize = false)
	private BukkitEntryImpl latest;
	protected BukkitHistoryImpl(IAccount account){
		this.account = account;
	}

	protected void reset(IEconomyProvider economyProvider, CurrencyBundle... bundles){
		entries.clear();
		entries.add(new BukkitEntryImpl(economyProvider.getName(), economyProvider.getClass(), EntryType.RESET, currencyNames, account, null, null, 0, System.currentTimeMillis(), null, balance, 0));
		if (account instanceof BukkitAccountImpl bukkitAccount){
			bukkitAccount.requireSave = true;
		}
	}


	private void orderLowToHigh(){
		ArrayList<BukkitEntryImpl> entries = new ArrayList<>(internalEntries());
		entries.sort(Comparator.comparingLong(Entry::created));
		this.entries.clear();
		this.entries.addAll(entries);
	}
	@Override
	public @NotNull List<Entry> entries() {
		return new ArrayList<>(this.entries);
	}

	private List<BukkitEntryImpl> internalEntries() {
		return entries;
	}

	@Override
	public @NotNull List<Entry> entriesOf(EntryType type) {
		List<Entry> entries = new ArrayList<>(internalEntries());
		entries.removeIf(entry->entry.type()!=type);
		return entries;
	}

	@Override
	public @NotNull List<Entry> entriesFrom(long from, long to) {
		List<Entry> entries = new ArrayList<>(internalEntries());
		entries.removeIf(entry -> entry.created() < from);
		entries.removeIf(entry -> entry.created() > to);
		return entries;
	}

	@Override
	public @NotNull List<Entry> entriesFromToCurrent(long from) {
		List<Entry> entries = new ArrayList<>(internalEntries());
		entries.removeIf(entry -> entry.created() < from);
		return entries;
	}

	@Override
	public @Nullable Entry entry(int id) {
		return entries.stream().filter(entry->entry.id()==id).findAny().orElse(null);
	}

	@Override
	public void newHistoryEntry(IEconomyProvider iEconomyProvider, EntryType entryType, IAccount iAccount, Operator operator, JsonObject jsonObject, EntryCurrencyData... entryCurrencyData) {
		if (jsonObject == null){
			jsonObject = JsonParser.parseString("{}").getAsJsonObject();
		}
		int id = entries.size();
		BukkitEntryImpl entry = new BukkitEntryImpl(name, aClass, entryType, currencies, account, to, operator, id, created, jsonObject, oldBalance, newBalance);
		entries.add(entry);
		orderLowToHigh();
		if (this.account instanceof BukkitAccountImpl){
			((BukkitAccountImpl) this.account).requireSave = true;
		}
		latest = entry;
	}

	public BukkitEntryImpl latest(){
		return latest;
	}
}
