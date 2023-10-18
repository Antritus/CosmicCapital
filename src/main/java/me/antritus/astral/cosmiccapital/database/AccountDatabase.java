package me.antritus.astral.cosmiccapital.database;

import com.google.gson.Gson;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.internal.PlayerAccount;
import me.antritus.astral.cosmiccapital.types.BukkitAccountImpl;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
//AstroSquatters
public abstract class AccountDatabase<T extends IAccount> implements IAccountManager {
	private final Class<T> accountClazz;
	protected final String world;
	protected final String data = "world VARCHAR (50), uniqueId VARCHAR(36), name VARCHAR(17), balance DOUBLE, data JSON";
	protected String CREATE_TABLE_QUERY = null;
	protected String INSERT_TABLE_QUERY = null;
	protected String UPDATE_TABLE_QUERY = null;
	protected String DELETE_QUERY = null;
	protected String DELETE_ONE_QUERY = null;
	protected String GET_QUERY = null;
	protected String GET_ALL_QUERY = null;
	protected String GET_ALL_WORLD = null;
	protected Connection connection;

	private final Map<UUID, T> accounts = new HashMap<>();
	protected final CosmicCapital cosmicCapital;

	public AccountDatabase(@NotNull CosmicCapital cosmicCapital, Class<T> accountClazz, @Nullable String world){
		this.cosmicCapital = cosmicCapital;
		this.world = world != null ? world : "global";
		this.accountClazz = accountClazz;
	}



	/**
	 * Gets from cache if found, else it loads it and returns value.
	 * This method does not use async to load accounts!
	 * @param player player
	 * @return account
	 */
	@Nullable
	public T get(Player player){
		if (accounts.get(player.getUniqueId()) == null){
			load(player.getUniqueId());
		}
		return accounts.get(player.getUniqueId());
	}

	@Override
	public @Nullable T get(String name) {
		OfflinePlayer player = cosmicCapital.getServer().getOfflinePlayer(name);
		return get(player.getUniqueId());
	}

	/**
	 * Gets from cache if found, else it loads it and returns value.
	 * This method does not use async to load accounts!
	 * @param uniqueId playerId
	 * @return account
	 */
	@Nullable
	public T get(UUID uniqueId){
		if (accounts.get(uniqueId) == null){
			load(uniqueId);
		}
		return accounts.get(uniqueId);
	}

	/**
	 * Gets from cache if found, else it returns null;
	 * This method does not use async to load accounts!
	 * @param name name
	 * @return account
	 */
	@Override
	public @NotNull T getKnownNonNull(String name) {
		OfflinePlayer player = cosmicCapital.getServer().getOfflinePlayer(name);
		return Objects.requireNonNull(get(player.getUniqueId()));
	}

	/**
	 * Gets from cache if found, else it returns null;
	 * This method does not use async to load accounts!
	 * @param uniqueId playerId
	 * @return account
	 */
	@Override
	public @NotNull T getKnownNonNull(UUID uniqueId) {
		return accounts.get(uniqueId);
	}

	@Override
	public void createIfNotExists(IAccount iAccount) {
		//noinspection unchecked
		accounts.put(iAccount.uniqueId(), (T) iAccount);
		saveAsync(iAccount.uniqueId(), false);
	}

	protected abstract void connect();

	public void createTable(){
		connect();
		try {
			PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY);
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void load(UUID uuid) {
		try (PreparedStatement statement = connection.prepareStatement(GET_QUERY)) {
			statement.setString(1, uuid.toString());
			ResultSet resultSet = statement.executeQuery();

			if (resultSet != null) {
				T account = null;
				while (resultSet.next()){
					String world = resultSet.getString("world");
					if (!this.world.equalsIgnoreCase(world)){
						continue;
					}
					String data = resultSet.getString("data");
					account = new Gson().fromJson(data, accountClazz);
					accounts.put(uuid, account);
					if (account instanceof BukkitAccountImpl impl) {
						impl.requireSave = false;
					}
					break;
				}
				statement.close();
				resultSet.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't load user for id: " + uuid + " (!)", e);
		}
	}

	private void save(UUID uniqueId) {
		T account = accounts.get(uniqueId);
		if (account == null) {
			return;
		}

		try {
			boolean isNew = true;
			PreparedStatement getStatement = connection.prepareStatement(GET_QUERY);
			getStatement.setString(1, uniqueId.toString());
			ResultSet getResult = getStatement.executeQuery();
			// Check if it needs to be inserted or updated
			if (getResult != null && getResult.next()){
				isNew = false;
			}
			if (getResult != null){
				getResult.close();
			}
			getStatement.close();
			// Prepare saving
			PreparedStatement statement;
			// Turn the account into json
			Gson gson = new Gson();
			String json = gson.toJson(account);
			if (!isNew) {
				statement = connection.prepareStatement(UPDATE_TABLE_QUERY);
				statement.setString(1, world);
				statement.setString(2, json);
				statement.setString(3, uniqueId.toString());
			} else {
				statement = connection.prepareStatement(INSERT_TABLE_QUERY);
				statement.setString(1, world);
				statement.setString(1, uniqueId.toString());
				statement.setString(3, json);
			}
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void unload(UUID uniqueId) {
		accounts.remove(uniqueId);
	}

	@SuppressWarnings("unused")
	public void saveAsync(UUID uniqueId, boolean unload){
		new BukkitRunnable() {
			@Override
			public void run() {
				save(uniqueId);
				if (unload){
					unload(uniqueId);
				}
			}
		}.runTaskAsynchronously(cosmicCapital);
	}
	public void save(UUID uniqueId, boolean unload) {
		save(uniqueId);
		if (unload) {
			unload(uniqueId);
		}
	}

	private void loadAll(){
		try {
			PreparedStatement statement = connection.prepareStatement(GET_ALL_WORLD);
			statement.setString(1, world);
			ResultSet resultSet = statement.executeQuery();
			Gson gson = new Gson();
			if (resultSet != null) {
				while (resultSet.next()) {
					String world = resultSet.getString("world");
					if (!this.world.equalsIgnoreCase(world)){
						continue;
					}
					String id = resultSet.getString("uniqueId");
					try {
						UUID uniqueId = UUID.fromString(id);
						if (accounts.get(uniqueId) != null){
							continue;
						}
					} catch (IllegalArgumentException e){
						continue;
					}
					String json = resultSet.getString("data");
					T account = gson.fromJson(json, accountClazz);
					accounts.put(account.uniqueId(), account);
					if (account instanceof BukkitAccountImpl impl) {
						impl.requireSave = false;
						impl.lifespan = 5000L;
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public final List<T> accounts(){
		loadAll();
		return accounts.values().stream().toList();
	}

	public void remove(PlayerAccount account) {
		accounts.remove(account.uniqueId());
	}
}
