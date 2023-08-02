package me.antritus.astral.cosmiccapital.api;

import me.antritus.astral.cosmiccapital.CosmicCapital;

import java.util.UUID;

public class BanknoteAccount extends Account{
	private final UUID uuid;
	private final String name;
	public BanknoteAccount(CosmicCapital cosmicCapital, UUID uuid) {
		super(cosmicCapital, "banknote");
		this.uuid = uuid;
		this.name = "Banknote";
	}

	@Override
	public UUID uniqueId() {
		return uuid;
	}

	@Override
	public String name() {
		return name;
	}
}
