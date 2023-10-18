package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.managers.IAccountRegistryManager;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AccountManagerRegistryManager implements IAccountRegistryManager {
	Map<String, IAccountManager> managerByManagerClass = new HashMap<>();
	Map<String, IAccountManager> managerByAccountClass = new HashMap<>();


	public void register(@NotNull IAccountManager manager, @NotNull Class<? extends IAccount> accountClazz) throws IllegalStateException {
		String clazz = manager.getClass().getName();
		if (managerByManagerClass.get(clazz) != null) {
			throw new IllegalStateException("There already is an account manager for class: " + clazz);
		}
		if (managerByAccountClass.get(accountClazz.getName()) != null){
			throw new IllegalStateException("There already is an account manager for class: "+ clazz);
		}
		managerByAccountClass.put(accountClazz.getName(), manager);
		managerByManagerClass.put(clazz, manager);
	}

	@Nullable
	public IAccountManager getManagerByAccount(@NotNull Class<?> accountClazz){
		return managerByAccountClass.get(accountClazz.getName());
	}

	@Nullable
	public IAccountManager getManager(@NotNull Class<? extends IAccountManager> manager){
		return managerByManagerClass.get(manager.getName());
	}
}
