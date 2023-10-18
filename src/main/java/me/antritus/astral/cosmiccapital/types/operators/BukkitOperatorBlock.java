package me.antritus.astral.cosmiccapital.types.operators;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BukkitOperatorBlock extends BukkitDefaultOperator{

	@Expose
	@NotNull
	private final String name;
	@Expose
	@NotNull
	private final String world;
	@Expose
	private final double x, y, z;
	public BukkitOperatorBlock(@NotNull String name, @NotNull String world, double x, double y, double z) {
		super(null, "block");
		this.name  = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public BukkitOperatorBlock(@NotNull String name, Location location) {
		super(null, "block");
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.name = name;
	}

	public String name() {
		return name;
	}

	public String world() {
		return world;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	@Override
	public JSONObject toJson(){
		try {
			return (JSONObject) new JSONParser().parse(new Gson().toJson(this));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
