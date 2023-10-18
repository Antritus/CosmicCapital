package me.antritus.astral.cosmiccapital.types.operators;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import me.antritus.astral.cosmiccapital.api.types.operator.Operator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.UUID;

public class BukkitDefaultOperator implements Operator {
	/**
	 * Used internally to detect a right type of player when loading from json.
	 */
	@SuppressWarnings("unused")
	@Expose
	private final String type;
	@Expose
	private final UUID uniqueId;

	public BukkitDefaultOperator(UUID uniqueId, String type){
		this.type = type;
		this.uniqueId = uniqueId;
	}
	protected BukkitDefaultOperator(){
		this.type = null;
		this.uniqueId = null;
	}

	@Override
	public Object get() {
		return null;
	}

	@Override
	public @Nullable UUID uniqueId() {
		return uniqueId;
	}

	/**
	 * Loads user from given json object.
	 * Deserializes using gson
	 * @param json json
	 * @return correct operator, else null
	 */
	@Nullable
	public static BukkitDefaultOperator load(JSONObject json){
		String type = (String) json.get("type");
		if (type.contentEquals("console")){
			return BukkitOperatorConsole.getConsole();
		} else if (type.contentEquals("block")){
			return new Gson().fromJson(json.toJSONString(), BukkitOperatorBlock.class);
		} else if (type.contentEquals("player")){
			UUID uniqueId = UUID.fromString((String) json.get("uniqueId"));
			if (Bukkit.getPlayer(uniqueId) != null){
				return new Gson().fromJson(json.toJSONString(), BukkitOnlineOperator.class);
			} else {
				return new Gson().fromJson(json.toJSONString(), BukkitOfflineOperator.class);
			}
		} else if (type.contentEquals("rcon")){
			return new BukkitRConConsoleOperator(null);
		}
		return null;
	}

	/**
	 * Loads user from given json object.
	 * Deserializes using gson
	 * @param object object
	 * @return correct operator, else null
	 */
	@Nullable
	public static BukkitDefaultOperator toOperator(Object object){
		if (object instanceof ConsoleCommandSender){
			return BukkitOperatorConsole.getConsole();
		} else if (object instanceof BlockCommandSender block){
			return new BukkitOperatorBlock(block.getName(), block.getBlock().getLocation());
		} else if (object instanceof OfflinePlayer oPlayer) {
			if (oPlayer instanceof Player) {
				return new BukkitOnlineOperator(oPlayer.getUniqueId());
			} else {
				return new BukkitOfflineOperator(oPlayer.getUniqueId());
			}
		} else if (object instanceof RemoteConsoleCommandSender remoteConsoleCommandSender){
			return new BukkitRConConsoleOperator(remoteConsoleCommandSender);
		}
		return null;
	}


	public JSONObject toJson(){
		try {
			return (JSONObject) new JSONParser().parse((new Gson()).toJson(this));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
