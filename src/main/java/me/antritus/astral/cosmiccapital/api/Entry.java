package me.antritus.astral.cosmiccapital.api;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class Entry {
	private final EntryType type;
	private final double amount;
	private final Account account;
	private final Account otherAcc;
	private long created;
	private final String info;


	private Entry(@NotNull EntryType type, double amount, @NotNull Account account, @Nullable Account otherAcc, String info) {
		this.type = type;
		this.amount = amount;
		this.account = account;
		this.otherAcc = otherAcc;
		this.info = info;
	}

	protected static Entry receiveTransfer(@NotNull Account account, @NotNull Account from, double amount){
		return new Entry(EntryType.TRANSFER_RECEIVE, amount, account, from, null);
	}

	protected static Entry sendTransfer(@NotNull Account account, @NotNull Account to, double amount){
		return new Entry(EntryType.TRANSFER_SEND, amount, account, to, null);
	}
	protected static Entry operatorRemove(@NotNull Account account, CommandSender sender, double amount){
		return new Entry(EntryType.OPERATOR_REMOVE, 0, account, null, new JSONObject(commandSenderData(sender)).toString());
	}

	protected static Entry operatorAdd(@NotNull Account account, CommandSender sender, double amount){
		return new Entry(EntryType.OPERATOR_ADD, 0, account, null, new JSONObject(commandSenderData(sender)).toString());
	}
	protected static Entry operatorSet(@NotNull Account account, CommandSender sender, double amount){
		return new Entry(EntryType.OPERATOR_SET, 0, account, null, new JSONObject(commandSenderData(sender)).toString());
	}
	protected static Entry operatorReset(@NotNull Account account, CommandSender sender){
		return new Entry(EntryType.OPERATOR_RESET, 0, account, null, new JSONObject(commandSenderData(sender)).toString());
	}

	private static HashMap<String, Object> commandSenderData(CommandSender sender){
		HashMap<String, Object> jsonObject = new HashMap<String, Object>();
		if (sender instanceof Player pl){
			jsonObject.put("id", pl.getUniqueId().toString());
			jsonObject.put("name", pl.getName());
		} else if (sender instanceof BlockCommandSender block){
			Location location = block.getBlock().getLocation();
			HashMap<String, Object> jsonLocation = new HashMap<>();
			jsonLocation.put("x",location.getX());
			jsonLocation.put("y", location.getY());
			jsonLocation.put("z", location.getZ());
			jsonLocation.put("world", location.getWorld().getName());
			jsonObject.put("location", jsonLocation);
			jsonObject.put("id", "COMMAND_BLOCK");
			jsonObject.put("type", block.getBlock().getType().toString());
			jsonObject.put("name", block.getName());
		} else {
			jsonObject.put("id", "console");
		}
		return jsonObject;
	}

	public static Entry createBanknote(Account account, BanknoteAccount banknoteAccount, double amount) {
		return new Entry(EntryType.WITHDRAW, amount, account, banknoteAccount, null);
	}

	@Override
	public String toString(){
		switch (type){
			case TRANSFER_SEND -> {
				assert otherAcc != null;
				return account.uniqueId()+" ("+account.name()+") sent transfer of "+amount+" to " + otherAcc.uniqueId()+" ("+account.name()+")";
			}
			case TRANSFER_RECEIVE -> {
				assert otherAcc != null;
				return account.uniqueId()+" ("+account.name()+") received transfer of "+amount+"to " + otherAcc.uniqueId()+" ("+account.name()+")";
			}
			case OPERATOR_SET -> {
				String deObj;
			}
		}
		return "Entry{Null}";
	}

	public JSONObject toJSON(){
		HashMap<String, Object> json = new HashMap<>();
		HashMap<String, Object> accountJson = new HashMap<>();
		accountJson.put("name", account.name());
		accountJson.put("type", account.getType());
		accountJson.put("id", account.uniqueId().toString());
		json.put("account", accountJson);
		if (otherAcc != null){
			HashMap<String, Object> otherJson = new HashMap<>();
			otherJson.put("name", otherAcc.name());
			otherJson.put("type", otherAcc.getType());
			otherJson.put("id", otherAcc.uniqueId().toString());
			json.put("other-account", otherJson);
		}
		json.put("created", created);
		json.put("amount", amount);
		if (info != null){
			json.put("data", info);
		}
		return new JSONObject(json);
	}



	@NotNull
	public EntryType getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}

	public Account getAccount() {
		return account;
	}

	public Account getOtherAcc() {
		return otherAcc;
	}

	enum EntryType {
		WITHDRAW,
		DEPOSIT_BANKNOTE,

		TRANSFER_SEND,
		TRANSFER_RECEIVE,

		TRANSACTION_SEND,
		TRANSACTION_RECEIVE,

		OPERATOR_ADD,
		OPERATOR_REMOVE,
		OPERATOR_SET,
		OPERATOR_RESET,


		PLUGIN,
	}
}
