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
package io.github.runningchickendev.ecomarket.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

public class Bank {
	
	/** UNSAFE, but it doesn't matter */
	private static Map<UUID, Double> balances = new HashMap<UUID, Double>();
	
	/**
	 * Creates a uuid from a long
	 */
	private static UUID form(long id) {
		return UUID.fromString(Long.toString(id));
	}
	
	/**
	 * Sets money for a player.
	 * 
	 * @param p	Player with account
	 * @param money	The amount of money to set
	 */
	public static void setMoney(Player p, double money) {
		balances.put(p.getUniqueId(), money);
	}
	
	/**
	 * Adds an amount of money using an id.
	 * 
	 * @param id	The account id
	 * @param money	The amount of money to add
	 */
	public static void addMoney(long id, double money) {
		addMoney(form(id), money);
	}
	
	/**
	 * Adds an amount of money using a unique id.
	 * 
	 * @param id	The unique account id
	 * @param money	The amount of money to add
	 */
	public static void addMoney(UUID id, double money) {
		double prev = getMoney(id);
		balances.put(id, prev + money);
	}
	
	/**
	 * Adds an amount of money to a Player's account.
	 * It just calls {@link Bank#addMoney(UUID, double)}
	 * using {@link Player#getUniqueId()}
	 * 
	 * @param p		The Player with a Bank account
	 * @param money	The amount of money to add
	 */
	public static void addMoney(Player p, double money) {
		addMoney(p.getUniqueId(), money);
	}
	
	/**
	 * Returns how much money a Player has.
	 * 
	 * @param p	The Player with a Bank account
	 * @return	The amount of money
	 */
	public static double getMoney(Player p) {
		Double money = balances.get(p.getUniqueId());
		if(money == null) {
			money = 0D;
		}
		return money;
	}
	
	/**
	 * Returns how much money is stored at a unique id.
	 * 
	 * @param id	The unique id
	 * @return		The amount of money
	 */
	public static double getMoney(UUID id) {
		Double money = balances.get(id);
		if(money == null) {
			money = 0D;
		}
		return money;
	}
}
