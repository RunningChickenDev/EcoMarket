/*
 * Licensed under the self-made Genus-Homo-License:
 * 
 * This license allows all animals of the genus "Homo" to
 * copy, modify, or redistribute
 * this program, library, license, or script.
 * 
 * ANY ANIMALS OTHER THAN THOSE BELOW ARE NOT PERMITTED TO USE, MODIFY, COPY OR DERIVE FROM THIS CODE.
 * PERMITTED SPECIES:
 * H. HABILIS, H. ERECTUS, H. RUDOLFENIS, H. GAUTENGENSIS, H. ERGASTER, H. ANTECESSOR, H. CEPRANENSIS, H. HEIDELBERGENSIS, H. NEANDERTHALIS, H. NALEDI, H. TSAICHANGENSIS, H.RHODESIENSIS, H. SAPIENS, H. FLORESIENSIS, WHERE "H." STANDS FOR "HOMO".
 * 
 * Get the license here:
 * https://github.com/MisterCavespider/Genus-Homo-License
 * 
 * Copyright (c) 2016 RunningChickenDev
 * Find us here: https://github.com/orgs/RunningChickenDev
 * 
 */
package io.github.runningchickendev.ecomarket;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import io.github.runningchickendev.ecomarket.bank.Bank;

@Plugin(id="ecomarket", name="Economy Market", version="0.1")
public class PluginBase {
	
	@Inject
	private Logger logger;
	
	public Logger getLogger() {
		return logger;
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent start) {
		logger.info("-- Economy Market --");
		
		CommandSpec market_sell_cmd = CommandSpec.builder()
				.description(Text.of("Can sell without having to be a Player"))
				.permission("ecomarket.sell.cmd")
				.arguments(
						GenericArguments.onlyOne(GenericArguments.longNum(Text.of("id"))),
						GenericArguments.onlyOne(GenericArguments.catalogedElement(Text.of("item"), ItemType.class)),
						GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity")))
				)
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						if(src instanceof Player) {
							src.sendMessage(Text.of(TextColors.RED, "Sorry, this command is cmd only!"));
							return CommandResult.empty();
						}
						
						long id = args.<Integer>getOne(Text.of("id")).get();
						ItemType item = args.<ItemType>getOne(Text.of("item")).get();
						int quantity = args.<Integer>getOne(Text.of("quantity")).get();
						
						sell(src, id, Resources.translate(item), quantity);
						
						return CommandResult.success();
					}
				})
				.build();
		
		// /market sell
		CommandSpec marktet_sell = CommandSpec.builder()
				.child(market_sell_cmd, "cmd")
				.description(Text.of("Sell your stuff here!"))
				.permission("ecomarket.sell")
				.executor(new CommandExecutor() {
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						if(src instanceof Player) {		//Is it player?
							//It is a player
							Player player = (Player) src;
							Optional<ItemStack> handOpt = player.getItemInHand(HandTypes.MAIN_HAND);	//Optional - might not have something
							if(handOpt.isPresent()) {	//Do they have something in the main hand?
								//They have something in their main hand
								ItemStack hand = handOpt.get();	//Get that item
								ItemType type = hand.getItem();	//Get that type
								if(Resources.presentTranslation(type)) {	//Can you sell it?
									//You can sell it
									Resource res = Resources.translate(type);
									//Tell them the current price
									double price = res.getPrice();
									src.sendMessage(Text.of(TextColors.BLUE, res.getName() + " has a price of: " + StringFormats.noDecimal(price)));
									sell(player, res, hand);
								} else {
									//You can't sell it
									src.sendMessage(Text.of(TextColors.RED, "You can't sell that!"));
								}
							} else {
								//They don't have something in their main hand
								src.sendMessage(Text.of(TextColors.YELLOW, "If you want to sell something, hold it in your main hand"));
							}
						} else {
							//It is not a Player
							src.sendMessage(Text.of(TextColors.RED, "Use /market sell cmd, if you want to sell!"));
						}
						//Done!
						return CommandResult.success();
					}
				})
				.build();
		
		// /market - useless (can't call)
		CommandSpec market = CommandSpec.builder()
			.child(marktet_sell, "sell")
			.permission("ecomarket")
			.description(Text.of("Main market command. Use /market sell to sell something"))
			.build();
		
		CommandSpec money = CommandSpec.builder()
				.executor(new CommandExecutor() {
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						if(src instanceof Player) {
							Player player = (Player) src;
							player.sendMessage(Text.of(TextColors.GOLD, "You have ",
								TextColors.RED, StringFormats.noDecimal(Bank.getMoney(player)),
								TextColors.GOLD, " coins"));
						}
						return null;
					}
				})
				.build();
		
		Sponge.getCommandManager().register(this, market, "market");
		Sponge.getCommandManager().register(this, money, "money");
		logger.info("Everything seems fine... too fine...");
	}
	
	public void sell(CommandSource src, long id, Resource r, int quantity) {
		
		int init_quant = quantity;
		double[] quantYield = calcQuantYield(r, init_quant);
		
		int sold_quant = (int) quantYield[0];
		double yield = quantYield[1];
		
		Bank.addMoney(id, yield);
		
		src.sendMessage(Text.of(TextColors.BLUE, "Sold ",
				TextColors.LIGHT_PURPLE, StringFormats.noDecimal(sold_quant), " " , r.getName(),
				TextColors.BLUE, " for ",
				TextColors.LIGHT_PURPLE, StringFormats.noDecimal(yield),
				", on account id ", Long.toString(id)));
	}
	
	public void sell(Player p, Resource r, ItemStack stack) {
		
		int init_quant = stack.getQuantity();
		double[] quantYield = calcQuantYield(r, init_quant);
		
		int sold_quant = (int) quantYield[0];
		double yield = quantYield[1];
		
		stack.setQuantity(0);
		p.setItemInHand(HandTypes.MAIN_HAND, stack);
		
		p.sendMessage(Text.of(TextColors.BLUE, "Sold ",
			TextColors.LIGHT_PURPLE, StringFormats.noDecimal(sold_quant), " " , r.getName(),
			TextColors.BLUE, " for ",
			TextColors.LIGHT_PURPLE, StringFormats.noDecimal(yield)));
		
		Bank.addMoney(p, yield);
	}
	
	private double[] calcQuantYield(Resource r, int quantity) {
		double yield = 0;
		
		for(int i=0;i<quantity;i++) {
			yield += r.getPrice();
			Resource.Circulation.addQuantity(r, 1);
			if(r.isBlocked()) {
				return new double[] {i, yield};
			}
		}
		
		return new double[] {quantity, yield};
	}
}
