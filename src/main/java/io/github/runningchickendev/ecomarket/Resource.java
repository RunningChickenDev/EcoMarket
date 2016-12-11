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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

public interface Resource {
	
	public static class Circulation {
		
		private static Map<Resource, Long> data = new HashMap<Resource, Long>();
		private static Map<Resource, Boolean> block= new HashMap<Resource, Boolean>();
		
		private static final int delay = 1000;
		private static final float restore_percentage = 0.4f;
		
		/**
		 * Only internally!
		 * 
		 * @param r
		 */
		private static void addResource(Resource r) {
			if(data.get(r) == null) {
				data.put(r, 0L);
			}
		}
		
		public static long getQuantity(Resource r) {
			return data.get(r);
		}
		
		private static void setQuantity(Resource r, long v) {
			data.put(r, v);
		}
		
		public static void addQuantity(Resource r, long amount) {
			long previous = data.get(r);
			data.put(r, previous + amount);
		}
		
		public static boolean isTooMuch(Resource r) {
			return data.get(r) > r.getMaxQuantity();
		}
		
		public static boolean isTooFew(Resource r) {
			return data.get(r) < r.getMinQuantity();
		}
		
		public static void fix(Resource r) {
			//Too few?
			if(isTooFew(r)) {
				//Set it to min
				setQuantity(r, r.getMinQuantity());
			}
			
			if(isTooMuch(r)) {
				//Set it to max
				setQuantity(r, r.getMaxQuantity());
				block(r);
			}
		}
		
		private static void block(Resource r) {
			block.put(r, true);
			Scheduler scheduler = Sponge.getScheduler();
			Task.Builder task = scheduler.createTaskBuilder();
			task.execute(new Runnable() {
				@Override
				public void run() {
					block.put(r, false);
					restore(r);
				}
			})
			.delayTicks(delay);
		}
		
		public static boolean isBlocked(Resource r) {
			Boolean is = block.get(r);
			if(is == null) {
				return false;
			} else {
				return is;
			}
		}
		
		public static void restore(Resource r) {
			long delta = r.getMaxQuantity() - r.getMinQuantity();
			long newQuantity = (long) (restore_percentage * delta + r.getMinQuantity());
			setQuantity(r, newQuantity);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private long id;
		private long minQ;
		private long maxQ;
		private double minP;
		private double maxP;
		
		public Builder() {}
		
		public Resource build() {
			Resource r = new Resource() {
				
				/**
				 * Linearly: know the min and max of Quantity (Q) and Price (P).<BR>
				 * <BR>
				 * a = delta_y / delta_x ; a = deltaP / deltaQ<BR>
				 * a = (maxP - minP)/(minQ - maxQ)<BR>
				 * <BR>
				 * ax + b = y ; b = y - ax ; b = maxP - a*minQ || b = minP - a*maxQ<BR>
				 * b = maxP - a*minQ<BR>
				 * <BR>
				 * P(Q) = a*Q + b = (maxP-minP)/(maxQ-minQ)*Q + maxP-(maxP-minP)/(maxQ-minQ)*minQ<BR>
				 */
				public double getPrice() {
					//Fix before calculating
					Circulation.fix(this);
					if(isBlocked()) {
						return 0;
					}
					
					double a = (getMaxPrice() - getMinPrice()) / (getMinQuantity() - getMaxQuantity());
					double b = getMaxPrice() - a*getMinQuantity();
					
					double Q = Circulation.getQuantity(this);
					double P = a*Q + b;
					
					return P;
				}
				
				public String getName() {
					return Builder.this.name;
				}
				
				public long getMinQuantity() {
					return Builder.this.minQ;
				}
				
				public double getMinPrice() {
					return Builder.this.minP;
				}
				
				public long getMaxQuantity() {
					return Builder.this.maxQ;
				}
				
				public double getMaxPrice() {
					return Builder.this.maxP;
				}
				
				public long getId() {
					return Builder.this.id;
				}

				public double getQuantity() {
					return Circulation.getQuantity(this);
				}
				
				public String toString() {
					String s =
							"Resource{"+
								"name="+getName()+",id="+getId()+
								",minQ="+getMinQuantity()+",maxQ="+getMaxQuantity()+
								",minP="+getMinPrice()+",maxP="+getMaxPrice()+
							"}";
					return s;
				}

				@Override
				public boolean isBlocked() {
					return Circulation.isBlocked(this);
				}
			};
			
			Circulation.addResource(r);
			return r;
		}
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		public Builder setId(long id) {
			this.id = id;
			return this;
		}
		public Builder setMinQ(long minQ) {
			this.minQ = minQ;
			return this;
		}
		public Builder setMaxQ(long maxQ) {
			this.maxQ = maxQ;
			return this;
		}
		public Builder setMinP(double minP) {
			this.minP = minP;
			return this;
		}
		public Builder setMaxP(double maxP) {
			this.maxP = maxP;
			return this;
		}
	}
	
	public String getName();
	public long getId();
	
	/**
	 * Minimum amount allowed.
	 * Below this is not possible.
	 */
	public long getMinQuantity();
	/**
	 * Maximum amount allowed.
	 * If there's too much, all trade
	 * with this good will be halted for
	 * a while, and it will be reset.
	 */
	public long getMaxQuantity();
	
	/**
	 * Lowest price of a resource
	 */
	public double getMinPrice();
	public double getMaxPrice();
	
	/**
	 * Calculates.
	 * 
	 * @return	the price for 1 piece (not 1 {@link ItemStack}!)
	 */
	/* COMMENT:
	 * 
	 * Linearly: know the min and max of Quantity (Q) and Price (P).
	 * 
	 * a = delta_y / delta_x ; a = deltaP / deltaQ
	 * a = (maxP - minP)/(minQ - maxQ)
	 * 
	 * ax + b = y ; b = y - ax ; b = maxP - a*minQ || b = minP - a*maxQ
	 * b = maxP - a*minQ
	 * 
	 * P(Q) = a*Q + b = (maxP-minP)/(maxQ-minQ)*Q + maxP-(maxP-minP)/(maxQ-minQ)*minQ
	 */
	public double getPrice();
	public double getQuantity();
	
	/**
	 * Too much stuff!
	 * @return
	 */
	public boolean isBlocked();
}
