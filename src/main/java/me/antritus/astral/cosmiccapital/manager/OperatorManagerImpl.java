package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.api.managers.IOperatorManager;
import me.antritus.astral.cosmiccapital.api.types.operator.Operator;
import me.antritus.astral.cosmiccapital.types.operators.BukkitDefaultOperator;
import me.antritus.astral.cosmiccapital.types.operators.BukkitOfflineOperator;
import me.antritus.astral.cosmiccapital.types.operators.BukkitOperatorConsole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OperatorManagerImpl implements IOperatorManager {
	private final Map<UUID, BukkitOfflineOperator> byId = new HashMap<>();
	private final Map<String, BukkitOfflineOperator> byName = new HashMap<>();
	private final BukkitOperatorConsole console = BukkitOperatorConsole.getConsole();
	@Override
	public @Nullable BukkitOfflineOperator getPlayerOperator(UUID uuid) {
		if (byId.get(uuid) == null){
			BukkitOfflineOperator defaultOperator;
			OfflinePlayer player = Bukkit.getPlayer(uuid);
			if (player == null){
				player = Bukkit.getOfflinePlayer(uuid);
			}
			defaultOperator = (BukkitOfflineOperator) BukkitDefaultOperator.toOperator(player);
			byId.put(uuid, defaultOperator);
		}
		return byId.get(uuid);
	}

	@Override
	public @Nullable BukkitOfflineOperator getPlayerOperator(String name) {
		if (byName.get(name) == null){
			BukkitOfflineOperator defaultOperator;
			OfflinePlayer player = Bukkit.getPlayer(name);
			if (player == null){
				player = Bukkit.getOfflinePlayer(name);
			}
			defaultOperator = (BukkitOfflineOperator) BukkitDefaultOperator.toOperator(player);
			byName.put(name, defaultOperator);
		}
		return byName.get(name);
	}

	@Override
	public @NotNull BukkitOperatorConsole getConsoleOperator() {
		return console;
	}

	@Override
	public @Nullable OfflinePlayer getPlayer(Operator operator) {
		return (OfflinePlayer) operator.get();
	}

	@Override
	public @NotNull ConsoleCommandSender getConsole() {
		return console.get();
	}
}
