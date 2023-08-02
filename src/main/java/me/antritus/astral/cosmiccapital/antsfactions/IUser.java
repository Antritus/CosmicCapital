package me.antritus.astral.cosmiccapital.antsfactions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public interface IUser {
	@Nullable
	Property<String, ?> get(@NotNull String key);
	@NotNull
	Map<String, SimpleProperty<?>> get();
	void setting(@NotNull String key, @Nullable Object value);
}
