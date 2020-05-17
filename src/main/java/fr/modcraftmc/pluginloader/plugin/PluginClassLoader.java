package fr.modcraftmc.pluginloader.plugin;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    public PluginBase plugin;

    static {
        ClassLoader.registerAsParallelCapable();
    }


    public PluginClassLoader(final String mainClass,final ClassLoader parent, final File file) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(mainClass, true, this);
            } catch (ClassNotFoundException ex) {
                throw new PluginLoadException();
            }

            Class<? extends PluginBase> pluginClass = null;
            try {
                pluginClass = jarClass.asSubclass(PluginBase.class);
            } catch (ClassCastException ex) {

            }

            plugin = pluginClass.newInstance();
        } catch (IllegalAccessException | InstantiationException | PluginLoadException ex) {

        }
    }


    synchronized void initialize(PluginBase pluginBase) {
        Validate.notNull(pluginBase, "plugin null");
        Validate.isTrue(pluginBase.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");

        pluginBase.init();

    }
}