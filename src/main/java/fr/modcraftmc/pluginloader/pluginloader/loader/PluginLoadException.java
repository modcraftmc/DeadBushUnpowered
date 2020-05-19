package fr.modcraftmc.pluginloader.pluginloader.loader;

import java.io.File;

public class PluginLoadException extends Throwable {

    public PluginLoadException() {

    }

    public PluginLoadException(File file) {
        super("unable to load plugin " + file.getName());
    }
}
