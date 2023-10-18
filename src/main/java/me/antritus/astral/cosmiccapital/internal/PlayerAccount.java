package me.antritus.astral.cosmiccapital.internal;

import me.antritus.astral.cosmiccapital.types.BukkitAccountImpl;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerAccount extends BukkitAccountImpl {
	public PlayerAccount(@NotNull OfflinePlayer player) {
		super(player);
	}

	public PlayerAccount(@NotNull UUID uniqueId, @NotNull String name) {
		super(uniqueId, name);
	}
	private PlayerAccount() {}
}
