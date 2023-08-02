package me.antritus.astral.cosmiccapital.api;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.astrolminiapi.CoreCommand;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public abstract class CosmicCapitalCommand extends CoreCommand {
	public final CosmicCapital main;
	protected CosmicCapitalCommand(CosmicCapital main, @NotNull String name) {
		super(main, name);
		this.main = main;
		setPermission("cosmiccapital."+name.toLowerCase());
	}
}
