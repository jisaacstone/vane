package org.oddlama.imex.core.config;

import org.oddlama.imex.core.Module;

import java.lang.StringBuilder;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.reflections.ReflectionUtils.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.apache.commons.lang.WordUtils;

import org.oddlama.imex.annotation.ConfigDouble;
import org.oddlama.imex.annotation.ConfigLong;
import org.oddlama.imex.annotation.ConfigString;
import org.oddlama.imex.annotation.ConfigVersion;
import org.oddlama.imex.annotation.LangMessage;
import org.oddlama.imex.annotation.LangString;
import org.oddlama.imex.annotation.LangVersion;

public class ConfigLongField extends ConfigField<Long> {
	public ConfigLong annotation;

	public ConfigLongField(Module module, Field field, ConfigLong annotation) {
		super(module, field, Long.class);
		this.annotation = annotation;
	}

	@Override
	public void generate_yaml(StringBuilder builder) {
		// Description
		append_description(builder, annotation.desc());

		// Valid values
		append_value_range(builder, annotation.min(), annotation.max(), Long.MIN_VALUE, Long.MAX_VALUE);

		// Default
		append_default_value(builder, annotation.def());

		// Definition
		append_field_definition(builder, annotation.def());
	}

	@Override
	public void check_loadable(YamlConfiguration yaml) throws LoadException {
		check_yaml_path(yaml);

		if (!(yaml.get(get_yaml_path()) instanceof Number)) {
			throw new LoadException("Invalid type for yaml path '" + get_yaml_path() + "', expected long");
		}

		var val = yaml.getLong(get_yaml_path());
		if (annotation.min() != Long.MIN_VALUE && val < annotation.min()) {
			throw new LoadException("Configuration '" + get_yaml_path() + "' has an invalid value: Value must be >= " + annotation.min());
		}
		if (annotation.max() != Long.MAX_VALUE && val > annotation.max()) {
			throw new LoadException("Configuration '" + get_yaml_path() + "' has an invalid value: Value must be <= " + annotation.max());
		}
	}

	public void load(YamlConfiguration yaml) {
		try {
			field.setLong(module, yaml.getLong(get_yaml_path()));
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Invalid field access on '" + field.getName() + "'. This is a bug.");
		}
	}
}

