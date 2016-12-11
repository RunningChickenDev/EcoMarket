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
	
	public static void setMoney(Player p, double money) {
		balances.put(p.getUniqueId(), money);
	}
	
	public static void addMoney(Player p, double money) {
		double prev = getMoney(p);
		balances.put(p.getUniqueId(), prev + money);
	}
	
	public static double getMoney(Player p) {
		Double money = balances.get(p.getUniqueId());
		if(money == null) {
			money = 0D;
		}
		return money;
	}
	
}
