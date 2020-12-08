package fr.modcraftmc.deadbushloader.pluginloader.loader;

import java.io.File;
import java.io.IOException;

public class PluginLoadException extends IOException {

    public PluginLoadException() {

    }

    public PluginLoadException(String name) {
        super(name);
    }

    public PluginLoadException(File file) {
        super("unable to load plugin " + file.getName());
    }
}
