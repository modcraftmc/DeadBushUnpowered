package fr.modcraftmc.deadbushloader.pluginloader.loader;

import fr.modcraftmc.deadbushloader.pluginloader.plugin.MCPlugin;
import fr.modcraftmc.deadbushloader.pluginloader.plugin.PluginInformations;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    private MCPlugin plugin;
    public PluginInformations informations;

    static {
        ClassLoader.registerAsParallelCapable();
    }


    public PluginClassLoader(final String mainClass, final ClassLoader parent, final PluginInformations pluginInformations, final File file) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        this.informations = pluginInformations;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(mainClass, true, this);
            } catch (ClassNotFoundException ex) {
                throw new PluginLoadException();
            }

            Class<? extends MCPlugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(MCPlugin.class);
            } catch (Exception e) {
                throw new PluginLoadException(e.getMessage());
            }


            plugin = pluginClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | PluginLoadException | NoSuchMethodException | InvocationTargetException ignored) {

        }
    }

    public synchronized void initialize(MCPlugin pluginBase) {
        Validate.notNull(pluginBase, "plugin instance is null");
        Validate.isTrue(pluginBase.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
        pluginBase.init(informations);
    }

    public MCPlugin getPlugin() {
        return plugin;
    }
}
