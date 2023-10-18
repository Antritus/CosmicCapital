package me.antritus.astral.cosmiccapital.types.operators;

import me.antritus.astral.cosmiccapital.api.types.operator.OnlineOperator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitOnlineOperator extends BukkitOfflineOperator implements OnlineOperator {
	public BukkitOnlineOperator(UUID uniqueId) {
		super(uniqueId);
	}

	@Override
	public Player get() {
		if (super.get() == null){
			return null;
		}
		return (Player) super.get();
	}

	@Override
	public Player getAudience() {
		return (Player) super.get();
	}
}
