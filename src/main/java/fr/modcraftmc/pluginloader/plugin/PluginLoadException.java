package fr.modcraftmc.pluginloader.plugin;

import java.io.File;

public class PluginLoadException extends Throwable {

    public PluginLoadException() {

    }

    public PluginLoadException(File file) {
        super("unable to load plufin " + file.getName());
    }
}
