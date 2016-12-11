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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

@Plugin(id="ecomarket", name="Economy Market", version="0.1")
public class PluginBase {
	
	private Map<ItemType, Resource> item_translation;
	
	@Inject
	private Logger logger;
	
	public Logger getLogger() {
		return logger;
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent start) {
		logger.info("-- Economy Market --");
		
		//Add all the things to the translation table
		item_translation = new HashMap<ItemType, Resource>();
		item_translation.put(ItemTypes.LOG, Resources.WOOD.get());
		item_translation.put(ItemTypes.STONE, Resources.STONE.get());
		
		// /market sell
		CommandSpec marktet_sell = CommandSpec.builder()
				.description(Text.of("Sell your stuff here!"))
				.permission("ecomarket.sell")
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
						if(src instanceof Player) {		//Is it player?
							//It is a player
							Player player = (Player) src;
							Optional<ItemStack> handOpt = player.getItemInHand(HandTypes.MAIN_HAND);	//Optional - might not have something
							if(handOpt.isPresent()) {	//Do they have something in the main hand?
								//They have something in their main hand
								ItemStack hand = handOpt.get();	//Get that item
								ItemType type = hand.getItem();	//Get that type
								if(presentTranslation(type)) {	//Can you sell it?
									//You can sell it
									Resource res = item_translation.get(type);
									//Tell them the current price
									double price = res.getPrice();
									String formatted_price = String.format("%.0f\n", price);
									src.sendMessage(Text.of(TextColors.BLUE, res + " costs " + formatted_price));
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
							src.sendMessage(Text.of(TextColors.YELLOW, "Console market is not implemented yet"));
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
		
		Sponge.getCommandManager().register(this, market, "market");
	}
	
	public boolean presentTranslation(ItemType type) {
		return item_translation.containsKey(type);
	}
	
	@Listener
	public void onUse(InteractItemEvent event) {
		ItemType type = event.getItemStack().getType();
		logger.warn(type.getName());
		logger.warn(item_translation.get(type).toString());
	}
	
}
