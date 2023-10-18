package me.antritus.astral.cosmiccapital.types;

import com.github.antritus.astral.utils.ShushIDE;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.IEconomyProvider;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import me.antritus.astral.cosmiccapital.api.types.entry.Entry;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryCurrencyData;
import me.antritus.astral.cosmiccapital.api.types.entry.EntryType;
import me.antritus.astral.cosmiccapital.api.types.operator.Operator;
import me.antritus.astral.cosmiccapital.types.operators.BukkitDefaultOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class BukkitEntryImpl implements Entry {
	@Expose
	private final String provider;
	@Expose
	private final String className;
	@Expose
	private final EntryType type;
	@Expose
	private final String[] currencyNames;
	@Expose(serialize = false, deserialize = false)
	private ICurrency[] currencies;
	@Expose
	private boolean isMultiCurrency;


	@Expose(serialize = false, deserialize = false)
	private IAccount account;

	@ShushIDE
	@Expose
	@SerializedName("accountData")
	private final String accountId;

	@Expose(serialize = false, deserialize = false)
	private IAccount otherAcc;

	@ShushIDE
	@Expose
	@SerializedName("otherAccountData")
	private final String otherAccID;

	@Expose(serialize = false, deserialize = false)
	private final Operator operator;

	@ShushIDE
	@Expose
	@SerializedName("operatorData")
	private final String operatorId;

	@Expose
	private final int id;
	@Expose()
	private final long created;

	@Expose
	private final JsonObject object;
	@Expose
	private final double balanceBefore;
	@Expose
	private final double balanceAfter;

	public BukkitEntryImpl(String provider, Class<? extends IEconomyProvider> providerClass, EntryType type, String[] currencyNames, IAccount account, IAccount otherAcc, Operator operator, int id, long created, JsonObject object, double balanceBefore, double balanceAfter) {
		this.provider = provider;
		this.className = providerClass.getName();
		this.type = type;
		this.currencyNames = currencyNames;
		this.account = account;
		HashMap<String, Object> json = new LinkedHashMap<>();
		json.put("id", account.name());
		json.put("clazz", account.getClass());
		json.put("name", account.name());
		JSONObject jsonObject = new JSONObject(json);
		this.accountId = jsonObject.toJSONString();
		this.otherAcc = otherAcc;
		if (otherAcc != null){
			json = new LinkedHashMap<>();
			json.put("id", account.name());
			json.put("clazz", account.getClass());
			json.put("name", account.name());
			jsonObject = new JSONObject(json);
			this.otherAccID = jsonObject.toJSONString();
		} else {
			otherAccID = null;
		}
		this.operator = operator;
		if (operator != null) {
			if (operator instanceof BukkitDefaultOperator bukkitDefaultOperator) {
				this.operatorId = bukkitDefaultOperator.toJson().toJSONString();
			} else {
				this.operatorId = Objects.requireNonNull(operator.uniqueId()).toString();
			}
		} else {
			operatorId = null;
		}
		this.id = id;
		this.created = created;
		this.object = object;
		this.balanceBefore = balanceBefore;
		this.balanceAfter = balanceAfter;

		this.isMultiCurrency = currencyNames.length > 1;
	}
	@SuppressWarnings("unused")
	public BukkitEntryImpl(String provider, Class<? extends IEconomyProvider> providerClass, EntryType type, IAccount account, int id, long created, JsonObject object, double balanceBefore, double balanceAfter, String[] currencyNames) {
		this(provider, providerClass, type, currencyNames, account, null, null, id, created, object, balanceBefore, balanceAfter);
	}
	@SuppressWarnings("unused")
	public BukkitEntryImpl(String provider, Class<? extends IEconomyProvider> providerClass, EntryType type, IAccount account, IAccount otherAcc, int id, long created, JsonObject object, double balanceBefore, double balanceAfter, String[] currencyNames) {
		this(provider, providerClass, type, currencyNames, account, otherAcc, null, id, created, object, balanceBefore, balanceAfter);
	}
	@SuppressWarnings("unused")
	public BukkitEntryImpl(String provider, Class<? extends IEconomyProvider> providerClass, EntryType type, IAccount account, Operator operator, int id, long created, JsonObject object, double balanceBefore, double balanceAfter, String[] currencyNames) {
		this(provider, providerClass, type, currencyNames, account, null, operator, id, created, object, balanceBefore, balanceAfter);
	}

	@ShushIDE
	private BukkitEntryImpl(){
		this.provider = null;
		this.className = null;
		this.type = null;
		this.account = null;
		this.accountId = null;
		this.otherAcc = null;
		this.otherAccID = null;
		this.operator = null;
		this.operatorId = null;
		this.id = 0;
		this.created = 0;
		this.object = null;
		this.balanceBefore = 0;
		this.balanceAfter = 0;
		this.currencyNames = null;
		this.isMultiCurrency = false;
	}

	@Override
	public @NotNull String providerName() {
		return provider;
	}

	@Override
	public @Nullable Class<? extends IEconomyProvider> providerClass() {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IEconomyProvider> economyProvider = (Class<? extends IEconomyProvider>) Class.forName(className);
			return economyProvider;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public @NotNull EntryType type() {
		return type;
	}

	@Override
	public IAccount account() {
		if (accountId != null && account == null){
			CosmicCapital cosmicCapital = CosmicCapital.getPlugin(CosmicCapital.class);
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(accountId);
				String id = (String) jsonObject.get("uniqueId");
				String accountClazzName = (String) jsonObject.get("class");
				@SuppressWarnings("unchecked") Class<? extends IAccount> accountClazz =
						(Class<? extends IAccount>) Class.forName(accountClazzName);
				IAccountManager accountManager =
						cosmicCapital.accountRegistryManager().getManagerByAccount(accountClazz);
				if (accountManager == null){
					return null;
				}
				IAccount account = accountManager.get(UUID.fromString(id));
				if (account == null){
					return null;
				}
				this.account = account;
				return account;
			} catch (ParseException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return account;
	}

	@Override
	public IAccount secondaryAccount() {
		if (otherAccID != null && otherAcc == null){
			CosmicCapital cosmicCapital = CosmicCapital.getPlugin(CosmicCapital.class);
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(otherAccID);
				String id = (String) jsonObject.get("uniqueId");
				String accountClazzName = (String) jsonObject.get("class");
				@SuppressWarnings("unchecked") Class<? extends IAccount> accountClazz =
						(Class<? extends IAccount>) Class.forName(accountClazzName);
				IAccountManager accountManager =
						cosmicCapital.accountRegistryManager().getManagerByAccount(accountClazz);
				if (accountManager == null){
					return null;
				}
				IAccount account = accountManager.get(UUID.fromString(id));
				if (account == null){
					return null;
				}
				this.otherAcc = account;
				return account;
			} catch (ParseException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return otherAcc;
	}

	@Override
	public Operator operator() {
		return operator;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public long created() {
		return created;
	}

	@Override
	public JsonObject info() {
		return JsonParser.parseString(object.toString()).getAsJsonObject();
	}

	@Override
	public EntryCurrencyData[] entryCurrencyData() {
		return new EntryCurrencyData[0];
	}


	@Override
	public boolean multiCurrency() {
		if (currencyNames.length > 1 && !isMultiCurrency){
			isMultiCurrency = true;
		}
		return isMultiCurrency;
	}
}
