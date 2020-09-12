package org.oddlama.vane.portals.portal;
import org.oddlama.vane.portals.PortalConstructor;
import org.oddlama.vane.portals.Portals;

import java.io.IOException;
import static org.oddlama.vane.core.persistent.PersistentSerializer.from_json;
import static org.oddlama.vane.core.persistent.PersistentSerializer.to_json;

import java.util.UUID;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.oddlama.vane.core.persistent.PersistentSerializer;
import org.oddlama.vane.external.json.JSONObject;

public class PortalBlockLookup {
	public static Object serialize(@NotNull final Object o) throws IOException {
		final var lookup = (PortalBlockLookup)o;
		final var json = new JSONObject();
		json.put("portal_id", to_json(UUID.class,             lookup.portal_id));
		json.put("type",      to_json(PortalBlock.Type.class, lookup.type));
		return json;
	}

	public static PortalBlockLookup deserialize(@NotNull final Object o) throws IOException {
		final var json = (JSONObject)o;
		final var portal_id = from_json(UUID.class,             json.get("portal_id"));
		final var type      = from_json(PortalBlock.Type.class, json.get("type"));
		return new PortalBlockLookup(portal_id, type);
	}

	private UUID portal_id;
	private Type type;

	public PortalBlockLookup(final UUID portal_id, final Type type) {
		this.portal_id = portal_id;
		this.type = type;
	}

	public UUID portal_id() { return portal_id; }
	public Type type() { return type; }
}
