package org.oddlama.vane.trifles;

import java.util.logging.Logger;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
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
	
	public static final double maxSpeed = 50;

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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on_player_move(final PlayerMoveEvent event) {
		// Players mustn't be flying
		final var player = event.getPlayer();
		if (!player.isInsideVehicle()) {
			return;
		}
		final var vehicle = player.getVehicle();
		// no horse or pig
		if (vehicle == null || vehicle instanceof LivingEntity) {
			return;
		}
		final var data = vehicle.getPersistentDataContainer();
		final var velocity = get_stored_velocity(data);
		// Check for snow
		var mat = vehicle.getLocation().getBlock().getType();
		if (!sledMaterials.contains(mat)) {
		    var block = vehicle.getLocation().clone().subtract(0.0, 1, 0.0).getBlock();
		    if (!sledMaterials.contains(block.getType())) {
		    	logger.info("sled not on snpw");
		    	if (velocity.length() > 0) {
		    		// sled was moving, slow it down
		    		if (!block.isPassable()) {
		    			velocity.multiply(0.5);
		    		}
		    		velocity.multiply(0.1);
		    		if (velocity.length() > 0.1) {
		    			vehicle.setVelocity(velocity);
		    			store_velocity(data, velocity);
		    		} else { // slow enough, set to zero
		    			store_velocity(data, new Vector(0, 0, 0));
		    		}
		    	}
		    	return;
		    }
		    logger.info("sled on snow");
		}
		else {
			logger.info("sled in snow");
		}
		
		if (velocity.length() == 0) {
			var from = event.getFrom();
			var to = event.getTo();
		    velocity.setX(to.getX() - from.getX());
		    velocity.setY(to.getY() - from.getY());
		    velocity.setZ(to.getZ() - from.getZ());
		    velocity.multiply(1.1); // A little extra oomph
		}
		logger.info("sled speed = " + velocity.toString());
		
		// Check if 
		Block block = vehicle.getLocation().clone().getBlock();
		if (Math.abs(velocity.getX()) > Math.abs(velocity.getZ())) {
			var xOffset = velocity.getX() > 0 ? -1.0 : 1.0;
			block = vehicle.getLocation().clone().add(xOffset, 0.1, 0.0).getBlock();
		} else {
			var yOffset = velocity.getZ() > 0 ? -1.0 : 1.0;
			block = vehicle.getLocation().clone().add(0.0, 0.1, yOffset).getBlock();
		}
		
		if (block.isPassable() && velocity.length() < maxSpeed) {
			logger.info("increasing speed!");
			velocity.multiply(1.5);
		}
		vehicle.setVelocity(velocity);
		store_velocity(data, velocity);
		logger.info("velocity relly set to " + vehicle.getVelocity().toString());
	}
}
