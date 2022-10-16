package org.oddlama.vane.enchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.loot.LootTables;
import org.oddlama.vane.annotation.enchantment.Rarity;
import org.oddlama.vane.annotation.enchantment.VaneEnchantment;
import org.oddlama.vane.core.config.loot.LootDefinition;
import org.oddlama.vane.core.config.loot.LootTableList;
import org.oddlama.vane.core.config.recipes.RecipeList;
import org.oddlama.vane.core.config.recipes.ShapedRecipeDefinition;
import org.oddlama.vane.core.enchantments.CustomEnchantment;
import org.oddlama.vane.core.module.Context;
import org.oddlama.vane.enchantments.Enchantments;

@VaneEnchantment(
	name = "grappling_hook",
	max_level = 3,
	rarity = Rarity.UNCOMMON,
	treasure = true,
	target = EnchantmentTarget.ARMOR_FEET
)
public class FireWalker extends CustomEnchantment<Enchantments> {

	public FireWalker(Context<Enchantments> context) {
		super(context);
	}

	@Override
	public RecipeList default_recipes() {
		return RecipeList.of(new ShapedRecipeDefinition("generic")
			.shape("u", "b", "u")
			.set_ingredient('b', "vane_enchantments:ancient_tome_of_knowledge")
			.set_ingredient('l', Material.LAVA_BUCKET)
			.result(on("vane_enchantments:enchanted_ancient_tome_of_knowledge")));
	}
	
	@Override
	public LootTableList default_loot_tables() {
		return LootTableList.of(
			new LootDefinition("generic")
				.in(LootTables.BURIED_TREASURE)
				.in(LootTables.PILLAGER_OUTPOST)
				.in(LootTables.RUINED_PORTAL)
				.in(LootTables.SHIPWRECK_TREASURE)
				.in(LootTables.STRONGHOLD_LIBRARY)
				.in(LootTables.UNDERWATER_RUIN_BIG)
				.in(LootTables.UNDERWATER_RUIN_SMALL)
				.in(LootTables.VILLAGE_TEMPLE)
				.in(LootTables.WOODLAND_MANSION)
				.add(1.0 / 70, 1, 1, on("vane_enchantments:enchanted_ancient_tome_of_knowledge")),
			new LootDefinition("bastion")
				.in(LootTables.BASTION_TREASURE)
				.add(1.0 / 20, 1, 1, on("vane_enchantments:enchanted_ancient_tome_of_knowledge"))
			);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_move_event(final PlayerMoveEvent event) {
		// Get enchantment level
		final var player = event.getPlayer();
		final var boots = player.getEquipment().getBoots();
		if (boots == null) {
			return; // not wearing boots
		}

		// Check enchantment
		if (boots.getEnchantmentLevel(this.bukkit()) == 0) {
			return; // not firewalker boots
		}
		
		Location location = player.getLocation().clone(); // Cloned location
		location.subtract(0, 1, 0); // Move one block down
		final var block_under_player = location.getBlock();
		if (block_under_player.getType() != Material.LAVA) { // If this is the material -> return true (break/exit loop)
            return;
        }
		if (Math.random() > 0.91) {
			block_under_player.setType(Material.CRYING_OBSIDIAN);
		} else {
		    block_under_player.setType(Material.OBSIDIAN);
		}
	}
}
