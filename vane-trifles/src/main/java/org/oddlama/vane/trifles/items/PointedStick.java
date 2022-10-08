package org.oddlama.vane.trifles.items;

import static org.oddlama.vane.util.PlayerUtil.swing_arm;
import static org.oddlama.vane.util.PlayerUtil.give_items;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.oddlama.vane.annotation.item.VaneItem;
import org.oddlama.vane.core.config.recipes.RecipeList;
import org.oddlama.vane.core.config.recipes.ShapelessRecipeDefinition;
import org.oddlama.vane.core.item.CustomItem;
import org.oddlama.vane.core.module.Context;
import org.oddlama.vane.core.resourcepack.ResourcePackGenerator;
import org.oddlama.vane.trifles.Trifles;
import org.oddlama.vane.util.StorageUtil;

@VaneItem(name = "pointed_stick", base = Material.STICK, durability = 100, model_data = 0x760018, version = 1)
public class PointedStick extends CustomItem<Trifles> {
	public PointedStick(Context<Trifles> context) {
		super(context);
	}

	@Override
	public RecipeList default_recipes() {
		return RecipeList.of(new ShapelessRecipeDefinition("generic")
			.add_ingredient(Material.STICK)
			.result(key().toString()));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_interact_entity(final PlayerInteractEntityEvent event) {
		final var entity = event.getRightClicked();
		if (entity instanceof Slime slime) {
			return;
		}
    return;

		/*/ With a empty bucket in main hand /*
		final var player = event.getPlayer(); /*
		final var item_in_hand = player.getEquipment().getItem(event.getHand());
		if (item_in_hand.getType() != Material.BUCKET) {
			return;
		} 

		entity.remove();
		swing_arm(player, event.getHand());
		player.playSound(player, Sound.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1.0f, 2.0f);
    return; 

		final var new_stack = newStack();
		new_stack.editMeta(meta -> {
			final var correct_model_data = player.getChunk().isSlimeChunk() ? CUSTOM_MODEL_DATA_JUMPY : CUSTOM_MODEL_DATA_QUIET;
			meta.setCustomModelData(correct_model_data);
		});

		if (item_in_hand.getAmount() == 1) {
			// Replace with Slime Bucket
			player.getEquipment().setItem(event.getHand(), new_stack);
		} else {
			// Reduce amount and add SlimeBucket to inventory
			item_in_hand.setAmount(item_in_hand.getAmount() - 1);
			give_items(player, new_stack, 1);
		}*/
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_interact(final PlayerInteractEvent event) {
		final var player = event.getPlayer(); /*
		final var item_in_hand = player.getEquipment().getItem(event.getHand());
		if (item_in_hand.getType() != Material.BUCKET) {
			return;
		} */

		//entity.remove();
		swing_arm(player, event.getHand());
		player.playSound(player, Sound.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1.0f, 2.0f);
    return; /*
		// Skip if no block was right-clicked
		if (!event.hasBlock() || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final var player = event.getPlayer();
		final var item_in_hand = player.getEquipment().getItem(event.getHand());
		final var custom_item = get_module().core.item_registry().get(item_in_hand);
		if (!(custom_item instanceof SlimeBucket slime_bucket) || !slime_bucket.enabled()) {
			return;
		}

		// Prevent offhand from triggering (e.g. placing torches)
		event.setUseInteractedBlock(Event.Result.DENY);
		event.setUseItemInHand(Event.Result.DENY);

		// Place slime back into world
		final var loc = event.getInteractionPoint();
		loc.getWorld().spawnEntity(loc, EntityType.SLIME, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
			if (entity instanceof Slime slime) {
				slime.setSize(1);
			}
		});

		player.playSound(player, Sound.ENTITY_SLIME_JUMP, SoundCategory.MASTER, 1.0f, 2.0f);
		swing_arm(player, event.getHand());
		if (item_in_hand.getAmount() == 1) {
			// Replace with empty bucket
			player.getEquipment().setItem(event.getHand(), new ItemStack(Material.BUCKET));
		} else {
			// Reduce amount and add empty bucket to inventory
			item_in_hand.setAmount(item_in_hand.getAmount() - 1);
			give_items(player, new ItemStack(Material.BUCKET), 1);
		} */
	}
}
