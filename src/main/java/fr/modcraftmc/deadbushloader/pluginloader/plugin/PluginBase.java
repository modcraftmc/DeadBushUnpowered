package fr.modcraftmc.deadbushloader.pluginloader.plugin;

import fr.modcraftmc.deadbushloader.pluginloader.loader.PluginClassLoader;
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


    public PluginInformations getPluginInformations() {
        return pluginInformations;
    }
}
