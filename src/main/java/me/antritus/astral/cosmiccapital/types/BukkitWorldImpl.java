package me.antritus.astral.cosmiccapital.types;

import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.IWorld;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitWorldImpl implements IWorld {
	private final World world;
	private final Map<String, IAccountManager> accountManagerMap = new HashMap<>();

	public BukkitWorldImpl(World world) {
		this.world = world;
	}

	@Override
	public String name() {
		return world.getName();
	}

	@Override
	public World world() {
		return world;
	}

	@Override
	public UUID uniqueId() {
		return world.getUID();
	}

	@Override
	public void register(IAccount iAccount, IAccountManager iAccountManager) {
		accountManagerMap.put(iAccount.getClass().getName(), iAccountManager);
	}

	@Override
	public IAccountManager multiAccountManager(IAccount iAccount) {
		return accountManagerMap.get(iAccount.getClass().getName());
	}


}
