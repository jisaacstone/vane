package org.oddlama.vane.trifles.items;

import static org.oddlama.vane.util.Conversions.ms_to_ticks;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.oddlama.vane.annotation.item.VaneItem;
import org.oddlama.vane.core.config.recipes.RecipeList;
import org.oddlama.vane.core.config.recipes.ShapedRecipeDefinition;
import org.oddlama.vane.core.item.CustomItem;
import org.oddlama.vane.core.module.Context;
import org.oddlama.vane.trifles.Trifles;

@VaneItem(name = "ice_skates", base = Material.LEATHER_BOOTS, durability = 42, model_data = 0x760019, version = 1)
public class IceSkate extends CustomItem<Trifles> {
	public static final Set<Material> skateMaterials = Set.of(
		Material.ICE,
		Material.BLUE_ICE,
		Material.FROSTED_ICE,
		Material.PACKED_ICE
	);
	
	private static final PotionEffect slide_effect =
		new PotionEffect(PotionEffectType.SPEED, (int) ms_to_ticks(300), 3)
			.withAmbient(false)
			.withParticles(false)
			.withIcon(false);

	private static final PotionEffect slow_effect =
		new PotionEffect(PotionEffectType.SPEED, (int) ms_to_ticks(300), 1)
			.withAmbient(false)
			.withParticles(false)
			.withIcon(false);

	private static final PotionEffect jump_effect =
		new PotionEffect(PotionEffectType.JUMP, (int) ms_to_ticks(200), 1)
			.withAmbient(false)
			.withParticles(false)
			.withIcon(false);

	public IceSkate(Context<Trifles> context) {
		super(context);
	}

	@Override
	public RecipeList default_recipes() {
		return RecipeList.of(new ShapedRecipeDefinition("generic")
			.shape("b", "s")
			.set_ingredient('b', Material.LEATHER_BOOTS)
			.set_ingredient('s', Material.IRON_SWORD)
			.result(key().toString()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on_player_move(final PlayerMoveEvent event) {
		// Players mustn't be flying
		final var player = event.getPlayer();
		if (player.isGliding()) {
			return;
		}

		if (player.isInsideVehicle()) {
			return;
		}
		
		//check if wearing skates?
		if (!isInstance(player.getEquipment().getBoots())) {
			return;
		}

		// Inspect block type just a little below
		var block = player.getLocation().clone().subtract(0.0, 0.1, 0.0).getBlock();
		if (!block.isSolid()) {
			return;
		}
		
		if (skateMaterials.contains(block.getType())) { // ice material
			player.addPotionEffect(slide_effect);
			if (player.isSneaking()) {
				player.addPotionEffect(jump_effect);
			}
		} else {
			player.addPotionEffect(slow_effect);
		}
	}
}
