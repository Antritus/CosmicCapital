package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.managers.IWorldManager;
import me.antritus.astral.cosmiccapital.api.types.IWorld;
import me.antritus.astral.cosmiccapital.types.BukkitWorldImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldManagerImpl implements IWorldManager, Listener {
	private final List<String> ownWorldAccounts;
	private final boolean ownWorldAccountsEnabled;
	private final Map<String, BukkitWorldImpl> worlds = new HashMap<>();
	private final CosmicCapital cosmicCapital;
	public WorldManagerImpl(CosmicCapital cosmicCapital){
		for (World world : Bukkit.getWorlds()){
			BukkitWorldImpl bukkitWorld = new BukkitWorldImpl(world);
			worlds.put(world.getName().toLowerCase(), bukkitWorld);
		}
		this.cosmicCapital = cosmicCapital;

		Configuration config = cosmicCapital.getConfig();
		ownWorldAccountsEnabled = config.getBoolean("per-world-player-accounts.enabled", false);
		if (!ownWorldAccountsEnabled){
			ownWorldAccounts = Collections.emptyList();
		} else {
			ownWorldAccounts = config.getStringList("per-world.player-accounts.worlds");
		}
	}

	public List<String> ownWorldAccounts() {
		return ownWorldAccounts;
	}

	public boolean ownWorldAccountsEnabled() {
		return ownWorldAccountsEnabled;
	}

	public CosmicCapital cosmicCapital() {
		return cosmicCapital;
	}

	@Override
	@Nullable
	public IWorld world(String world) {
		return worlds.get(world.toLowerCase());
	}

	@Override
	public IWorld world(Object o) {
		if (o instanceof World world){
			return worlds.get(world.getName().toLowerCase());
		}
		return null;
	}

	public IWorld world(World o) {
		return worlds.get(o.getName().toLowerCase());
	}

	@Override
	public Collection<IWorld> worlds() {
		return new ArrayList<>(this.worlds.values());
	}
}
