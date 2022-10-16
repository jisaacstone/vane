package org.oddlama.vane.trifles.items;

import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.oddlama.vane.annotation.item.VaneItem;
import org.oddlama.vane.core.config.recipes.RecipeList;
import org.oddlama.vane.core.config.recipes.ShapelessRecipeDefinition;
import org.oddlama.vane.core.item.CustomItem;
import org.oddlama.vane.core.item.api.InhibitBehavior;
import org.oddlama.vane.core.module.Context;
import org.oddlama.vane.trifles.Trifles;

@VaneItem(name = "pointed_stick", base = Material.WOODEN_SWORD, durability = 42, model_data = 0x760018, version = 1)
public class IceSkate extends CustomItem<Trifles> {
	public IceSkate(Context<Trifles> context) {
		super(context);
	}

	@Override
	public RecipeList default_recipes() {
		return RecipeList.of(new ShapelessRecipeDefinition("generic")
			.add_ingredient(Material.STICK)
			.result(key().toString()));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
	    if (event.getDamager() instanceof Player player){
			final var item = player.getInventory().getItemInMainHand();
			final var custom_item = get_module().core.item_registry().get(item);
			if (!(custom_item instanceof IceSkate stick) || !stick.enabled()) {
				return;
			}
			final var damaged = event.getEntity();
			damaged.setGlowing(damaged.hasGravity());
			damaged.setGravity(!damaged.hasGravity());
	    }
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_interact_block(final PlayerInteractEvent event) {
		// Skip if no block was right-clicked or hand isn't main hand
		if (!event.hasBlock() || event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK )
		{
			return;
		}

		// With a trowel in main hand
		final var player = event.getPlayer();
		final var item_in_hand = player.getEquipment().getItem(EquipmentSlot.HAND);
		final var custom_item = get_module().core.item_registry().get(item_in_hand);
		if (!(custom_item instanceof IceSkate ps) || !ps.enabled()) {
			return;
		}

		// Prevent offhand from triggering (e.g. placing torches)
		event.setUseInteractedBlock(Event.Result.DENY);
		event.setUseItemInHand(Event.Result.DENY);

		// move block up
		final var block = event.getClickedBlock();
		final var world = player.getWorld();
		final var offset = (event.getAction() == Action.RIGHT_CLICK_BLOCK) ? +1 : -1;
		final var new_block = world.getBlockAt(block.getX(), block.getY() + offset, block.getZ());
		if (!new_block.canPlace(block.getBlockData())) {
			return;
		}
		if (new_block.getType() != Material.AIR) {
			return;
		}
		new_block.setType(block.getType());
		new_block.setBlockData(block.getBlockData());
		block.setType(Material.AIR);
		/*
		if (block.getType() == Material.SALMON) {
			block.setType(Material.COOKED_SALMON);
		}
		if (block.getType() == Material.COD) {
			block.setType(Material.COOKED_COD);
		}
		if (block.getType() == Material.POTATO) {
			block.setType(Material.BAKED_POTATO);
		}
		/*

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

	@Override
	public EnumSet<InhibitBehavior> inhibitedBehaviors() {
		return EnumSet.of(InhibitBehavior.USE_OFFHAND);
	}
}
