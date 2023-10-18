package me.antritus.astral.cosmiccapital.types.operators;

import me.antritus.astral.cosmiccapital.api.types.operator.OnlineOperator;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

public class BukkitRConConsoleOperator extends BukkitDefaultOperator implements OnlineOperator {
	@Nullable
	private final RemoteConsoleCommandSender commandSender;
	protected BukkitRConConsoleOperator(@Nullable RemoteConsoleCommandSender remoteConsoleCommandSender) {
		super(null, "rcon");
		commandSender = remoteConsoleCommandSender;
	}

	@Override
	@Nullable
	public Object get() {
		return commandSender;
	}

	@Override
	@Nullable
	public Audience getAudience() {

		return commandSender;
	}
}
