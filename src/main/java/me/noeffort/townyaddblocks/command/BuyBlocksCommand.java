package me.noeffort.townyaddblocks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import me.noeffort.townyaddblocks.Main;
import me.noeffort.townyaddblocks.Messages;
import me.noeffort.townyaddblocks.util.MessageUtil;
import me.noeffort.townyaddblocks.util.config.PriceConfig;
import net.milkbowl.vault.economy.Economy;

public class BuyBlocksCommand implements CommandExecutor {
	
	Main plugin;
	Towny towny;
	Economy economy;
	PriceConfig priceConfig;
	
	public BuyBlocksCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(MessageUtil.translate(Messages.player));
			return true;
		}
		
		Player player = (Player) sender;
		
		if(command.getName().equalsIgnoreCase("buyblocks") && player.hasPermission("towny.buyblocks")) {
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase(getTownName(player))) {
					if(isInt(args[1])) {
							
						economy = Main.getEconomy();
						priceConfig = new PriceConfig(plugin);
						Double price = priceConfig.getPriceConfig().getDouble("price");
						Double total = Double.parseDouble(args[1]) * price;
							
						try {
							int blocks = Integer.parseInt(args[1]);
							
							if(total <= 0 || blocks <= 0) {
								player.sendMessage(MessageUtil.translate(Messages.prefix + "&cYou cannot buy town blocks less than zero!"));
								return true;
							} else {
								economy.withdrawPlayer(player, total);
								player.sendMessage(MessageUtil.translate("&bYou have purchased " + args[1] + " town blocks for: $" + total + "!"));
								int bonus = TownyUniverse.getDataSource().getTown(getTownName(player)).getBonusBlocks();
								TownyUniverse.getDataSource().getTown(getTownName(player)).setBonusBlocks(bonus + blocks);
								TownyUniverse.getDataSource().saveAll();
								return true;
							}
						} catch (NotRegisteredException e) {
							e.printStackTrace();
						}
					}
					else {
						player.sendMessage(MessageUtil.translate(Messages.prefix + "&cPlease input only integers."));
						return true;
					}
				} else {
					if(!TownyUniverse.getDataSource().getTowns().contains(args[0])) {
						player.sendMessage(MessageUtil.translate(Messages.prefix + "&cThat town does not exist."));
						return true;
					} else {
						player.sendMessage(MessageUtil.translate(Messages.prefix + "&cYou do not belong to that town."));
						return true;
					}
				}
			} else {
				player.sendMessage(MessageUtil.translate(Messages.toolittleargs));
				player.sendMessage(MessageUtil.translate(Messages.prefix + "&cProper usage: /bb <town> <blocks>"));
				return true;
			}
		} else {
			if(!player.hasPermission("towny.buyblocks")) {
				player.sendMessage(MessageUtil.translate(Messages.permissions));
				return true;
			} else {
				player.sendMessage(MessageUtil.translate(Messages.player));
				return true;
			}
		}
		return true;
	}
	
	public String getTownName(Player player) {
		Resident r;
		try {
			r = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			return "none";
		}
		
		Town town;
		try {
			town = r.getTown();
		} catch (NotRegisteredException e) {
			return "none";
		}
		
		if(town == null) {
			return "none";
		} else {
			return town.getName();
		}
	}
	
    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
