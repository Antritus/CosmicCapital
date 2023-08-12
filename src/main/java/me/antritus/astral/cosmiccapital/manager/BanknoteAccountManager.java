package me.antritus.astral.cosmiccapital.manager;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.Account;
import me.antritus.astral.cosmiccapital.api.BanknoteAccount;
import me.antritus.astral.cosmiccapital.api.NumberUtils;
import me.antritus.astral.cosmiccapital.astrolminiapi.ColorUtils;
import me.antritus.astral.cosmiccapital.database.BanknoteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.codehaus.plexus.util.dag.DAG;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
public class BanknoteAccountManager implements Listener {
	private final NamespacedKey KEY_ID;
	private final NamespacedKey KEY_AMOUNT;
	private final CosmicCapital cosmicCapital;
	private final BukkitTask task;
	public BanknoteAccountManager(CosmicCapital cosmicCapital){
		this.cosmicCapital = cosmicCapital;
		KEY_ID = new NamespacedKey(cosmicCapital, "id");
		KEY_AMOUNT = new NamespacedKey(cosmicCapital, "amount");
		task = new BukkitRunnable() {
			@Override
			public void run() {
				cosmicCapital.getLogger().info("Saved banknotes players..!");
				BanknoteDatabase database = cosmicCapital.getBankNoteDatabase();
				database.save();
				cosmicCapital.getLogger().info("Saved banknotes players..!");
			}
		}.runTaskTimerAsynchronously(cosmicCapital, 600, 18_000);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if (event.getItem()==null){
			return;
		}
		if (!event.getAction().name().startsWith("RIGHT_CLICK")){
			return;
		}
		if (event.getHand()!= EquipmentSlot.HAND){
			return;
		}
		if (event.getItem().getType().equals(Material.MAP)) {
			ItemStack item = event.getItem();
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.get(KEY_ID, PersistentDataType.STRING) == null) {
				return;
			}
			if (container.get(KEY_AMOUNT, PersistentDataType.DOUBLE) == null) {
				return;
			}
			UUID uniqueId = UUID.fromString(Objects.requireNonNull(container.get(KEY_ID, PersistentDataType.STRING)));
			cosmicCapital.getBankNoteDatabase().load(uniqueId);
			double amount = container.get(KEY_AMOUNT, PersistentDataType.DOUBLE);
			if (!cosmicCapital.getBankNoteDatabase().isValid(uniqueId)) {
				warnStaffPossibleDupe("claim.non-valid", event.getPlayer(), cosmicCapital.getBankNoteDatabase().get(uniqueId), item);
				event.setCancelled(true);
				return;
			}
			BanknoteAccount banknoteAccount = cosmicCapital.getBankNoteDatabase().get(uniqueId);
			if (amount!=banknoteAccount.getBalance()){
				warnStaffPossibleDupe("claim.active-different-amount", event.getPlayer(), banknoteAccount, item);
				event.setCancelled(true);
			return;
			}
			event.getPlayer().sendMessage("Received: "+banknoteAccount.getBalance());
			Account account = cosmicCapital.getPlayerDatabase().get(event.getPlayer());
			banknoteAccount.sendTransfer(account, banknoteAccount.getBalance());
			cosmicCapital.getBankNoteDatabase().delete(banknoteAccount);
			event.getPlayer().getInventory().remove(item);
			event.setCancelled(true);
		}
	}
	public void onEnable(){
	}
	public void onDisable(){
		task.cancel();
	}

	public ItemStack create(Account account, double amount){
		BanknoteAccount banknote =  cosmicCapital.getBankNoteDatabase().create(account, amount);
		System.out.println(amount);
		System.out.println(banknote.getBalance());
		cosmicCapital.getBankNoteDatabase().save(banknote.uniqueId(), false);
		ItemStack itemStack = new ItemStack(Material.MAP);
		ItemMeta meta = itemStack.getItemMeta();
		meta.displayName(ColorUtils.translateComp("<gold><b>Banknote"));
		meta.lore(Collections.singletonList(ColorUtils.translateComp("Worth: "+ NumberUtils.properFormat(amount))));
		PersistentDataContainer container = meta.getPersistentDataContainer();
		container.set(KEY_ID, PersistentDataType.STRING, banknote.uniqueId().toString());
		container.set(KEY_AMOUNT, PersistentDataType.DOUBLE, banknote.getBalance());
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private void warnStaffPossibleDupe(String errorCode, Player player, BanknoteAccount banknoteAccount, ItemStack itemStack){
		ItemMeta meta = itemStack.getItemMeta();
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		double balance = dataContainer.get(KEY_AMOUNT, PersistentDataType.DOUBLE);
		String id= dataContainer.get(KEY_ID, PersistentDataType.STRING);
		String name = ColorUtils.deserialize(meta.displayName());

		switch (errorCode){
			case "claim.non-valid" ->{
				if (banknoteAccount != null){
					Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("cosmiccapital.anticheat")).forEach(p->cosmicCapital.getMessageManager().message(
							p,
							"anti-cheat.banknote.claim.de-activated",
							"%who%="+player.getName(),
							"%note:name%="+name,
							"%note:id%="+id,
							"%note:balance%="+balance,
							"%account:id%="+banknoteAccount.uniqueId(),
							"%account:name%="+banknoteAccount.name(),
							"%account:balance%="+banknoteAccount.getBalance()));
				}else {
					Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("cosmiccapital.anticheat")).forEach(p->cosmicCapital.getMessageManager().message(
							p,
							"anti-cheat.banknote.claim.de-activated",
							"%who%="+player.getName(),
							"%note:name%=" + name,
							"%note:id%=" + id,
							"%note:balance%=" + balance
					));
				}
			}
			case "claim.non-valid-account-exists", "claim.active-different-amount", "claim.de-activated" -> {
				Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("cosmiccapital.anticheat")).forEach(p->cosmicCapital.getMessageManager().message(
						p,
						"anti-cheat.banknote.claim.de-activated",
						"%who%="+player.getName(),
						"%note:name%="+name,
						"%note:id%="+id,
						"%note:balance%="+balance,
						"%account:id%="+banknoteAccount.uniqueId(),
						"%account:name%="+banknoteAccount.name(),
						"%account:balance%="+banknoteAccount.getBalance()
				));
			}
		}
	}
}
