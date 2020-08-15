package org.oddlama.imex.core;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.oddlama.imex.annotation.ConfigString;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public abstract class ModuleBase extends JavaPlugin {
	private static final int CONFIG_VERSION = 1;
	private static final int LANG_VERSION = 1;

	private Core core = null;
	public Core get_core() {
		return core;
	}

	@Override
	public void onEnable() {
		// Get core plugin reference, important for inherited configuration
		if (this.getName().equals("imex-core")) {
			core = (Core)this;
		} else {
			core = (Core)getServer().getPluginManager().getPlugin("imex-core");
		}

		reload_configuration();
	}

	public void reload_configuration() {
		// Get data directory
		var data_folder = module.getDataFolder();
		if (!data_folder.exists()) {
			 data_folder.mkdirs();
		}

		// Generate new file if not existing
		var file = new File(data_folder, "config.yml");
		if (!file.exists()) {
			var builder = new StringBuilder();
			generate_configuration(builder);
			var contents = builder.toString();

			// Save contents to file
			Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
		}

		// Load config file
		var yaml = YamlConfiguration.loadConfiguration(file);

		// Check config file version
		if (version != CONFIG_VERSION) {
			if (version < CONFIG_VERSION) {
				// TODO message config needs to be regenerated.
				// save it somewhere, start once, merge changes.
			} else {
				// TODO this configuration file is for a future version
				// of imex. Please use the correct config file, or delete
				// it and it will be regenerated.
			}

			// TODO force stop server
		}

		// Reload automatic variables
		on_reload_configuration();

		// Reload localization
		reload_localization();
	}

	public void reload_localization() {
		// Get data directory
		var data_folder = module.getDataFolder();
		if (!data_folder.exists()) {
			 data_folder.mkdirs();
		}

		// TODO for all embedded lang files, copy if version is greater.

		// Get configured language code
		var lang = config_lang();
		if ("inherit".equals(lang)) {
			lang = module.core.config_lang();

			// Fallback to en in case 'inherit' is used in imex-core.
			if ("inherit".equals(lang)) {
				lang = "en";
			}
		}

		// Generate new file if not existing
		var file = new File(data_folder, "lang-" + lang + ".yml");
		if (!file.exists()) {
			// TODO error, stop server
		}

		// Load config file
		var yaml = YamlConfiguration.loadConfiguration(file);

		// Reload automatic variables
		on_reload_localization();
	}

	// Will be generated by @ConfigString in Module.
	public abstract String config_lang();

	protected void on_reload_configuration() {
		// Automatically generated by @Config*
	}

	protected void on_reload_localization() {
		// Automatically generated by @Lang*
	}

	protected void generate_configuration(StringBuilder builder) {
		// Automatically generated by @Config*
	}
}
