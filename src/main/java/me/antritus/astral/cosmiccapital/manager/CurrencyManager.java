package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.api.managers.ICurrencyManager;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CurrencyManager implements ICurrencyManager {
	private final Map<String, ICurrency> byName = new HashMap<>();
	private final ICurrency mainCurrency;

	public CurrencyManager(ICurrency currency){
		this.mainCurrency = currency;
		byName.put(mainCurrency.name().toLowerCase(), mainCurrency);
	}
	@Override
	public @NotNull ICurrency getMainCurrency() {
		return mainCurrency;
	}

	@Override
	public @Nullable ICurrency getCurrency(String name) {
		return byName.get(name.toLowerCase());
	}

	@Override
	public boolean createCurrency(ICurrency currency) throws IllegalStateException{
		if (byName.get(currency.name().toLowerCase()) != null){
			return false;
		}
		byName.put(currency.name().toLowerCase(), currency);
		return true;
	}

	@Override
	public boolean isRegistered(ICurrency currency) {
		return byName.get(currency.name().toLowerCase()) != null;
	}
}
