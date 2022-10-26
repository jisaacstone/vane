package org.oddlama.vane.trifles;

import java.util.logging.Logger;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.oddlama.vane.core.Listener;
import org.oddlama.vane.core.module.Context;

public class SledListener extends Listener<Trifles> {
	private static Logger logger = Bukkit.getLogger();
	public static final Set<Material> sledMaterials = Set.of(
			Material.SNOW,
			Material.SNOW_BLOCK,
			Material.POWDER_SNOW,
			Material.FROSTED_ICE
		);
	
	public static final double maxSpeed = 10;

	public SledListener(Context<Trifles> context) {
		super(context.group(
			"sleds",
			"Enable using boats and minecarts on snow as sleds"
		));
		logger.info("sled listener inisializsed");
	}
	
	private Vector get_stored_velocity(PersistentDataContainer data) {
		var x = data.getOrDefault(
				new NamespacedKey(get_module(), "velX"),
				PersistentDataType.DOUBLE,
				0.0);
		var y = data.getOrDefault(
				new NamespacedKey(get_module(), "velY"),
				PersistentDataType.DOUBLE,
				0.0);
		var z = data.getOrDefault(
				new NamespacedKey(get_module(), "velZ"),
				PersistentDataType.DOUBLE,
				0.0);
		return new Vector(x, y, z);
	}
	
	private void store_velocity(PersistentDataContainer data, Vector velocity) {
		data.set(
				new NamespacedKey(get_module(), "velX"),
				PersistentDataType.DOUBLE,
				velocity.getX());
		data.set(
				new NamespacedKey(get_module(), "velY"),
				PersistentDataType.DOUBLE,
				velocity.getY());
		data.set(
				new NamespacedKey(get_module(), "velZ"),
				PersistentDataType.DOUBLE,
				velocity.getZ());
	}
	
	private boolean is_snow(Location loc) {
		if (sledMaterials.contains(loc.getBlock().getType())) {
			return true;
		}
		if (sledMaterials.contains(loc.clone().subtract(0, 0.2, 0).getBlock().getType())) {
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_interact_block(final PlayerInteractEvent event) {
		// Skip if no block was right-clicked or hand isn't main hand
		if (!event.hasBlock() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
			return;
		}

		final var player = event.getPlayer();
		final var item_in_hand = player.getEquipment().getItem(EquipmentSlot.HAND);
		if (item_in_hand.getType() != Material.MINECART) {
			return;
		}
		
		final var block = event.getClickedBlock();
		if (!sledMaterials.contains(block.getType())) {
			return;
		}
		var location = block.getLocation().clone();
		if (!block.isPassable()) {
			location.add(0, 1, 0);
		} else {
		    location.add(0, 0.2, 0);
		}
		location.setYaw(player.getEyeLocation().getYaw());
		location.setDirection(player.getEyeLocation().getDirection());
		player.getWorld().spawn(location, Minecart.class);
		if (player.getGameMode() != GameMode.CREATIVE) {
			player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_enter_vehicle(final VehicleEnterEvent event) {
		final var vehicle = event.getVehicle();
		if (vehicle instanceof Minecart cart) {
			cart.setMaxSpeed(10);
			if (event.getEntered() instanceof Player player) {
				if (is_snow(cart.getLocation())) {
					cart.setVelocity(cart.getFacing().getDirection());
				}
			}
		} else if (vehicle instanceof Boat boat) {
			if (event.getEntered() instanceof Player player) {
				if (is_snow(boat.getLocation())) {
					store_velocity(boat.getPersistentDataContainer(), boat.getLocation().getDirection());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_sled_move(final VehicleMoveEvent event) {
		final var vehicle = event.getVehicle();
		if (vehicle.getPassengers().size() == 0) {
			return;
		}
		if (vehicle instanceof Minecart cart) {
			on_cart_move(event, cart);
		} else if (vehicle instanceof Boat boat) {
			on_boat_move(event, boat);
		}
	}

	public void on_cart_move(final VehicleMoveEvent event, final Minecart cart) {
		var vel = cart.getVelocity();
		logger.info("Cart Start vel=" + cart.getVelocity());
		if (vel.length() > 0) {
			if (is_snow(cart.getLocation())) {
				if (vel.getY() < 0) {
					// transfer falling momentum to forward momentum;
					var transf = 1 - vel.getY();
				    vel.setY(0);
				    vel.setX(vel.getX() * transf);
				    vel.setZ(vel.getZ() * transf);
				}
				cart.setVelocity(vel.multiply(1.7));
			}
		}
		logger.info("Cart Set vel=" + cart.getVelocity());
	}
	
	public void on_boat_move(final VehicleMoveEvent event, final Boat boat) {
		final var data = boat.getPersistentDataContainer();
		final var velocity = get_stored_velocity(data);
		
		logger.info("stored speed = " + velocity.toString());
		// Check for snow
		if (!is_snow(boat.getLocation())) {
	    	if (velocity.length() > 0) {
	    		if (velocity.getY() == 0) {
	    			velocity.setY(-0.1);
	    		}
	    		velocity.setX(velocity.getX() * 0.91);
	    		velocity.setY(velocity.getX() * 0.91);
	    		if (boat.getLocation().clone().add(velocity).getBlock().isPassable() && velocity.length() > 1) {
		    		velocity.setY(velocity.getY() * 1.1);
	    			boat.setVelocity(velocity);
	    			store_velocity(data, velocity);
	    		} else { // slow enough, set to zero
	    			store_velocity(data, new Vector(0, 0, 0));
	    		}
	    	}
	    	return;
		}
		var from = event.getFrom();
		var to = event.getTo();
		
		final var moveVec = new Vector(
		    to.getX() - from.getX(),
		    to.getY() - from.getY(),
		    to.getZ() - from.getZ());
		
		logger.info("move speed = " + moveVec.toString());
		if (velocity.length() < 1) {
			velocity.add(moveVec);
		}
		
		if (velocity.getY() < 0 && !boat.getLocation().clone().add(velocity).getBlock().isPassable()) {

			var transf = 1 - velocity.getY() * 0.8;
			velocity.setY(0);
			velocity.setX(velocity.getX() * transf);
			velocity.setZ(velocity.getZ() * transf);
		}
		
		boat.setVelocity(velocity);
		store_velocity(data, velocity);
		logger.info("velocity relly set to " + boat.getVelocity().toString());
	}
}
