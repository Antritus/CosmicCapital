package me.antritus.astral.cosmiccapital.currencies;

import me.antritus.astral.cosmiccapital.api.IEconomyProvider;
import me.antritus.astral.cosmiccapital.api.types.currency.ICurrency;

public class Currency implements ICurrency {
	private final IEconomyProvider provider;
	private final String name;
	private final String plural;
	private final String singular;
	private final String character;
	private final CharDisplay display;

	public Currency(IEconomyProvider economyProvider, String name, String plural, String singular, String character, CharDisplay display) {
		this.provider = economyProvider;
		this.name = name;
		this.plural = plural;
		this.singular = singular;
		this.character = character;
		this.display = display;
	}

	public String name() {
		return name;
	}

	public String plural() {
		return plural;
	}

	public String singular() {
		return singular;
	}

	public String character() {
		return character;
	}

	@Override
	public ICurrency.CharDisplay display() {
		return display;
	}

	@Override
	public String display(double amount) {
		switch (display){
			case NEVER -> {
				return String.valueOf(amount);
			}
			case BACK -> {
				return amount+character;
			}
			case FRONT -> {
				return character+amount;
			}
		}
		return null;
	}

	@Override
	public Class<? extends IEconomyProvider> getEconomyProvider() {
		return provider.getClass();
	}

	@Override
	public IEconomyProvider getProvider(){
		return provider;
	}
}
