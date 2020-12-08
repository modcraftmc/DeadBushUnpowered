package fr.modcraftmc.deadbushloader.pluginloader.plugin;

import fr.modcraftmc.deadbushloader.pluginloader.loader.PluginClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public abstract class MCPlugin implements IPlugin {

    protected File pluginFolder = new File(".", "plugins");
    protected File configFolder = new File(pluginFolder, "configs");
    protected Logger LOGGER;
    public boolean loaded = false;

    private PluginInformations pluginInformations;

    public MCPlugin() {
        ClassLoader classLoader = this.getClass().getClassLoader();

        if (!(classLoader instanceof PluginClassLoader)) {
            throw new IllegalStateException("invalid classloader");
        }

        ((PluginClassLoader) classLoader).initialize(this);
    }

    public void init(PluginInformations informations) {
        pluginInformations = informations;

        LOGGER = LogManager.getLogger(pluginInformations.getName());
        this.onEnable();
    }


    public PluginInformations getPluginInformations() {
        return pluginInformations;
    }
}
