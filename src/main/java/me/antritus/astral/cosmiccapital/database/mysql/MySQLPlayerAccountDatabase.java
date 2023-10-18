package me.antritus.astral.cosmiccapital.database.mysql;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.types.account.IAccount;
import me.antritus.astral.cosmiccapital.database.AccountDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLPlayerAccountDatabase<C extends IAccount> extends AccountDatabase<C>  {
	public MySQLPlayerAccountDatabase(@NotNull CosmicCapital cosmicCapital, Class<C> clazz, @Nullable String world) {
		super(cosmicCapital, clazz, world);
		String table = "economy_player_balance";
		CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "+table+" (world VARCHAR(50), uniqueId VARCHAR(36) NOT NULL, name = VARCHAR(17), data JSON, PRIMARY KEY (uniqueId));";
		INSERT_TABLE_QUERY = "INSERT INTO "+table+" (world, uniqueId, name, data) VALUES (?, ?, ?, ?)";
		UPDATE_TABLE_QUERY = "UPDATE "+table+" SET world = ?, name = ?, data = ? where uniqueId = ?";
		DELETE_QUERY = "DELETE TABLE "+table;
		DELETE_ONE_QUERY = "DELETE FROM "+table+" WHERE uniqueId = ?";
		GET_QUERY = "SELECT * FROM "+table+" WHERE uniqueId = ?";
		GET_ALL_QUERY = "SELECT * FROM "+ table;
		GET_ALL_WORLD = "SELECT * FROM " + table + " WHERE world = ?";
	}

	@Override
	protected void connect() {
		String url = cosmicCapital.getConfig().getString("database.player.url", "database.player.url");
		String password = cosmicCapital.getConfig().getString("database.player.password", "database.player.password");
		String user = cosmicCapital.getConfig().getString("database.player.user", "database.player.user");
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
