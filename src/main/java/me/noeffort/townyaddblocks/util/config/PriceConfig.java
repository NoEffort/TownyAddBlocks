package me.noeffort.townyaddblocks.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import me.noeffort.townyaddblocks.Main;

public class PriceConfig implements Listener {

	private static FileConfiguration config = null;
	private static File configFile = null;
	
	//Initiating the Main class
	Main plugin;
	
	//Constructor
	public PriceConfig(Main instance) {
		this.plugin = instance;
	}
	
	//Used to create and reload the config file
	public void reloadPriceConfig() {
		//Checking for file
		if(configFile == null) {
			//Making new config file
			configFile = new File(plugin.getDataFolder(), "prices.yml");
			//Checking for existence
			if(!configFile.exists()) {
				//File not found
				plugin.saveResource("prices.yml", false);
				Bukkit.getLogger().log(Level.INFO, "Prices.yml config file generated!");
			} else {
				//File found
				savePriceConfig();
				Bukkit.getLogger().log(Level.INFO, "Prices.yml file found, no worries!");
			}
		}
		//Setting file
		config = YamlConfiguration.loadConfiguration(configFile);
		
		//Allowing inputs to file
		Reader defaultConfigStream = new InputStreamReader(plugin.getResource("prices.yml"));
		if(defaultConfigStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
			defaultConfig.addDefault("useDefaults", true);
			defaultConfig.addDefault("price", 25.0);
			defaultConfig.options().copyDefaults(true);
			config.setDefaults(defaultConfig);
		}
	}
	
	//Used to get the custom config file
	public FileConfiguration getPriceConfig() {
		//Checking for file
		if(config == null) {
			reloadPriceConfig();
		}
		//Returning the file
		return config;
	}
	
	//Used to save the custom file
	public void savePriceConfig() {
		//Checking for file
		if(config == null || configFile == null) {
			return;
		}
		//Saving file
		try {
			getPriceConfig().save(configFile);
		} catch (IOException e) {
			//Error boi
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
	
	//Saving the defaults of the file, reverting to base settings
	public void saveDefaultPriceConfig() {
		//Checking for file
		if(configFile == null) {
			configFile = new File(plugin.getDataFolder(), "prices.yml");
			plugin.saveResource("prices.yml", false);
			Bukkit.getLogger().log(Level.INFO, "Prices.yml config file generated!");
		}
		//Saving defaults
		if(configFile.exists()) {
			plugin.saveResource("prices.yml", false);
		}
	}
	
	public File getPriceConfigFile() {
		return configFile;
	}
	
}
