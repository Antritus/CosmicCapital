package me.antritus.astral.cosmiccapital.database;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.antsfactions.Property;
import me.antritus.astral.cosmiccapital.api.Account;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
//AstroSquatters
public class PlayerAccountDatabase {
	private final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS cosmiccapital_economy (uniqueId VARCHAR(50) NOT NULL, balance DOUBLE, history JSON, PRIMARY KEY (uniqueId));";
	private final static String INSERT_TABLE_QUERY = "INSERT INTO cosmiccapital_economy (uniqueId, balance, history) VALUES (?, ?, ?)";
	private final static String UPDATE_TABLE_QUERY = "UPDATE cosmiccapital_economy SET balance = ?, history = ? where uniqueId = ?";
	private final static String DELETE_QUERY = "DELETE TABLE cosmiccapital_economy";
	private final static String DELETE_ONE_QUERY = "DELETE FROM cosmiccapital_economy WHERE uniqueId = ?";
	private final static String GET_QUERY = "SELECT * FROM cosmiccapital_economy WHERE uniqueId = ?";
	private final Field balanceField;

	{
		try {
			balanceField = Account.class.getDeclaredField("balance");
			balanceField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private final Map<UUID, PlayerAccount> accounts = new HashMap<>();
	private final CosmicCapital cosmicCapital;

	public PlayerAccountDatabase(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
	}

	public void create(){
		Connection connection = cosmicCapital.getCoreDatabase().getConnection();
		try {
			connection.prepareStatement(CREATE_TABLE_QUERY).execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public PlayerAccount get(Player player){
		if (accounts.get(player.getUniqueId()) == null){
			load(player.getUniqueId());
		}
		return accounts.get(player.getUniqueId());
	}
	@NotNull
	public PlayerAccount get(UUID uniqueId){
		if (accounts.get(uniqueId) == null){
			load(uniqueId);
		}
		return accounts.get(uniqueId);
	}

	public void load(UUID uuid) {
		PlayerAccount account = new PlayerAccount(cosmicCapital, uuid);
		account.setLoading(true);

		try {
			Connection connection = cosmicCapital.getCoreDatabase().getConnection();
			PreparedStatement statement = connection.prepareStatement(GET_QUERY);
			try {
				statement.setString(1, uuid.toString());
				ResultSet resultSet = statement.executeQuery();

				if (resultSet != null) {
					if (resultSet.next()) {
						double bal = resultSet.getDouble("balance");
						String history = resultSet.getString("history");
						try {
							balanceField.set(account, bal);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
						account.loadHistory(history);
					} else {
						account.setting("new", true);
					}
					resultSet.close();
				} else {
					account.setting("new", true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		accounts.put(uuid, account);
		account.setLoading(false);
	}

	private void save(UUID uniqueId) {
		Connection connection = cosmicCapital.getCoreDatabase().getConnection();
		Account account = accounts.get(uniqueId);
		if (account == null) {
			return;
		}

		try {
			PreparedStatement statement;

			Property<String, ?> property = account.get("new");
			if (property == null || property.getValue() == null || !(Boolean) property.getValue()) {
				statement = connection.prepareStatement(UPDATE_TABLE_QUERY);
				statement.setDouble(1, account.getBalance());  // Index starts from 1
				statement.setString(2, "[]");
				statement.setString(3, uniqueId.toString());
			} else {
				statement = connection.prepareStatement(INSERT_TABLE_QUERY);
				statement.setString(1, uniqueId.toString());
				statement.setDouble(2, account.getBalance());  // Index starts from 1
				statement.setString(3, "[]");
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

	public void save(UUID uniqueId, boolean unload) {
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

	public void disable(){
		accounts.forEach((id, user)->{
			save(id);
		});
	}
}
