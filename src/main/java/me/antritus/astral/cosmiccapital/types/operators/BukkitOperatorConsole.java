package me.antritus.astral.cosmiccapital.types.operators;

import me.antritus.astral.cosmiccapital.api.types.operator.OnlineOperator;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitOperatorConsole extends BukkitDefaultOperator implements OnlineOperator {
	protected static BukkitOperatorConsole bukkitOperatorConsole;

	public static BukkitOperatorConsole getConsole(){
		if (bukkitOperatorConsole == null){
			bukkitOperatorConsole = new BukkitOperatorConsole();
		}
		return bukkitOperatorConsole;
	}
	protected BukkitOperatorConsole() {
		super(null, "console");
		if (bukkitOperatorConsole != null){
			throw new IllegalStateException("BukkitOperatorConsole has already been initialized!");
		}
		bukkitOperatorConsole = this;
	}

	@Override
	public @NotNull ConsoleCommandSender get() {
		return Bukkit.getConsoleSender();
	}

	@Override
	public Audience getAudience() {
		return Bukkit.getConsoleSender();
	}
}
