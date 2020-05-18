package fr.modcraftmc.pluginloader.pluginloader.plugin;

import fr.modcraftmc.pluginloader.pluginloader.loader.PluginClassLoader;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public abstract class PluginBase implements Plugin {

    protected File pluginFolder = new File(".", "plugins");
    protected File dataFolder = new File(pluginFolder, "configs");
    protected Logger LOGGER;
    public static PluginBase instance;
    public boolean loaded = false;

    private PluginInformations pluginInformations;

    public PluginBase() {
        ClassLoader classLoader = this.getClass().getClassLoader();

        if (!(classLoader instanceof PluginClassLoader)) {
            throw new IllegalStateException("invalid class loader");
        }

        instance = this;
        ((PluginClassLoader) classLoader).initialize(this);
    }

    public void init(PluginInformations informations) {
        pluginInformations = informations;
        LOGGER = LogManager.getLogger("PluginThread/" + pluginInformations.getName());
        onEnable();
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



    public PluginInformations getPluginInformations() {
        return pluginInformations;
    }
}
