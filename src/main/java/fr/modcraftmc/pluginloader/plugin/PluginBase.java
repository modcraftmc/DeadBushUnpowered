package fr.modcraftmc.pluginloader.plugin;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.List;

public abstract class PluginBase implements Plugin {

    protected File pluginFolder = new File(".", "plugins");
    protected File dataFolder = new File(pluginFolder, "configs");

    private ClassLoader classLoader = null;

    private String id;
    private String name;
    private String version;
    private String description;
    private List<String> authors;

    public PluginBase() {
         classLoader = this.getClass().getClassLoader();

        if (!(classLoader instanceof PluginClassLoader)) {
            throw new IllegalStateException("invalid class loader");
        }

        ((PluginClassLoader) classLoader).initialize(this);
    }

    public void init() {

    }

    public static <T extends PluginBase> T getPlugin(Class<T> clazz) {
        Validate.notNull(clazz, "can't be null");

        if (!PluginBase.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not extend " + PluginBase.class);
        }
        final ClassLoader cl = clazz.getClassLoader();
        if (!(cl instanceof PluginClassLoader)) {
            throw new IllegalArgumentException(clazz + " is not initialized by " + PluginClassLoader.class);
        }
        PluginBase plugin = ((PluginClassLoader) cl).plugin;
        if (plugin == null) {
            throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
        }
        return clazz.cast(plugin);
    }


}
