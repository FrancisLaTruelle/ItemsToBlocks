package fr.francislatruelle.itemstoblocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);	
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (sender instanceof ConsoleCommandSender) {
			      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
							this.getConfig().getString("messages.notplayer")));
			      return false;
			}
			
			Player player = (Player)sender;
			int totalChangedItems = 0;
			int totalChangedIBlocks = 0;
			
			int totalI = 0;
			
			for (String key : this.getConfig().getStringList("itemstoblocks")) {

				byte b;
		        int i;
		        int PlayerItemNb = 0;
		        
		        String[] keySplit = key.split(":");
		        
		        String itemName = keySplit[0];
		        int itemNb = Integer.parseInt(keySplit[1].split(" -> ")[0]);
		        String blockName = keySplit[1].split(" -> ")[1];
		        int blockNb = Integer.parseInt(keySplit[2]);
		        
		        ItemStack[] arrayOfItemStack;
				
				
		        for (i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i; ) {
		          ItemStack is = arrayOfItemStack[b];
		          if (is != null) {
		            if (is.getType() == Material.matchMaterial(itemName)) {
		              player.getInventory().remove(is);
		              PlayerItemNb += is.getAmount();
		            } 
		          } 
		          b = (byte)(b + 1);
		        }
		        
		        int itemT = PlayerItemNb / itemNb * blockNb;
		        int itemO = PlayerItemNb % itemNb;
		        
		        int itemsChanged = PlayerItemNb -= itemO;
		        
		        player.getInventory().addItem(new ItemStack[] { new ItemStack((itemT > 0) ? Material.matchMaterial(blockName) : Material.AIR, itemT) });       
		        player.getInventory().addItem(new ItemStack[] { new ItemStack((itemO > 0) ? Material.matchMaterial(itemName) : Material.AIR, itemO) });

		        if (!(itemsChanged == 0)) {
		        	totalChangedItems = totalChangedItems + itemsChanged;
		        	totalChangedIBlocks = totalChangedIBlocks + itemT;
		        }
		        
		        if (totalI++ == this.getConfig().getStringList("itemstoblocks").size() - 1) {
		        	String message = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.finally"));
		        	message = message.replaceAll("\\{totalChangedItems\\}", String.valueOf(totalChangedItems));
		        	message = message.replaceAll("\\{totalChangedIBlocks\\}", String.valueOf(totalChangedIBlocks));
		        	player.sendMessage(message);
		        }
			};
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("itemstoblocks.reload")) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
						this.getConfig().getString("messages.notpermission")));
				return false;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
					this.getConfig().getString("messages.reloaded")));
			this.reloadConfig();
		}
		return false;
	}
}