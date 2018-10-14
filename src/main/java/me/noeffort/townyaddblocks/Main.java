package me.noeffort.townyaddblocks;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


import me.noeffort.townyaddblocks.Main;
import me.noeffort.townyaddblocks.command.BuyBlocksCommand;
import me.noeffort.townyaddblocks.util.MessageUtil;
import me.noeffort.townyaddblocks.util.config.PriceConfig;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	//Making instances of the Main class
	private static Main instance;
	
	//Getting the mobConfig file
	private static PriceConfig priceConfig;
	
	//Logger and economy input
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy economy = null;
	
	@Override
	public void onEnable() {
		//Checks if Vault exists
		if(!setupEconomy()) {
			log.severe("Vault not found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		if(!setupTowny()) {
			log.severe("Towny not found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		instance = this;
		//Registering the command(s)
		registerCommands();
		
		priceConfig = new PriceConfig(instance);
		
		priceConfig.getPriceConfigFile();
		priceConfig.reloadPriceConfig();
	}
	
	public void registerCommands() {
		this.getCommand("buyblocks").setExecutor(new BuyBlocksCommand(this));
	}
	
	//Setting up the economy
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	//Setting up towny
	private boolean setupTowny() {
        if (getServer().getPluginManager().getPlugin("Towny") == null) {
            return false;
        } else {
        	return true;
        }
    }
	
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	//Checks for player
        if(!(sender instanceof Player)) {
        	sender.sendMessage(MessageUtil.translate(Messages.player));
        	return true;
        }
        
        //Sets the player
        Player player = (Player) sender;
        
        //Looks for this command
        if(command.getName().equalsIgnoreCase("reloadaddblocks")) {
        	//Checking for op
        	if(player.isOp()) {
        		//Reload message
				player.sendMessage(MessageUtil.translate(Messages.reload));
				
				//Checking for existance of file
				if(!priceConfig.getPriceConfigFile().exists()) {
					player.sendMessage(MessageUtil.translate(Messages.missingfile));
					saveResource("prices.yml", false);
					player.sendMessage(MessageUtil.translate(Messages.filefound));
					Bukkit.getLogger().log(Level.INFO, "Config files generated!");
					return true;
				} else {
					//Getting and reloading the config
					priceConfig.getPriceConfigFile();
					priceConfig.reloadPriceConfig();
				}
				return true;
			} else {
				//Invalid permissions
				player.sendMessage(MessageUtil.translate(Messages.permissions));
			}
        } else {
        	return true;
        }
		return true;
    }
    
    //Getter for the economy
    public static Economy getEconomy() {
        return economy;
    }
    
    //Getter for the Main class
    public static Main get() {
    	return instance;
    }
	
}
