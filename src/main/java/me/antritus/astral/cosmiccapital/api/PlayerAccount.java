package me.antritus.astral.cosmiccapital.api;

import me.antritus.astral.cosmiccapital.CosmicCapital;

import java.util.UUID;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class PlayerAccount extends Account{
	private final UUID uniqueId;
	private final String name;
	public PlayerAccount(CosmicCapital cosmicCapital, UUID uniqueId) {
		super(cosmicCapital, "player");
		this.uniqueId = uniqueId;
		this.name = cosmicCapital.getServer().getOfflinePlayer(uniqueId).getName();
	}
	@Override
	public UUID uniqueId() {
		return uniqueId;
	}

	@Override
	public String name() {
		return name;
	}
}
