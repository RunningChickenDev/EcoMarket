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

public enum Resources {
	
	WOOD(Resource.builder()
			.setName("Wood")
			.setId(00)
			.setMinQ(1)
			.setMaxQ(1000)
			.setMinP(1)
			.setMaxP(12)),
	
	STONE(Resource.builder()
			.setName("Stone")
			.setId(01)
			.setMinQ(1)
			.setMaxQ(800)
			.setMinP(2)
			.setMaxP(20));
	
	/**********/
	
	private Resource r;
	
	private Resources(Resource.Builder b) {
		this.r = b.build();
	}
	
	public Resource get() {
		return r;
	}
	
}
