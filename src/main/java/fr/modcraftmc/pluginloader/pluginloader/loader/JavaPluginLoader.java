package fr.modcraftmc.pluginloader.pluginloader.loader;

import com.google.gson.Gson;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginBase;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginInformations;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;

public class JavaPluginLoader {

    public File pluginFolder = new File(".", "plugins");
    private File configFolder = new File(pluginFolder, "configs");

    public static ArrayList<PluginBase> pluginLoaded = new ArrayList<>();

    private static Gson gson = new Gson();

    private static Logger LOGGER = LogManager.getLogger("PluginLoader");


    public void handleStart() {
        Thread working = new Thread(() -> {

            LOGGER.info("DeadBushUnpowered is loading");

            LOGGER.info("loading plugin phase DISCOVERING");
            if (!pluginFolder.exists()) pluginFolder.mkdirs();
            Collection<File> pluginToLoad = FileUtils.listFiles(pluginFolder, null, true);
            LOGGER.info(String.format("Found %s plugins to load", pluginToLoad.size()));

            LOGGER.info("loading plugin phase LOADING");
            pluginToLoad.forEach((plugin)-> {

                LOGGER.info("attempting to load" + plugin.getName());

                try {
                    checkPlugin(plugin);
                } catch (PluginLoadException e) {
                    e.printStackTrace();
                    pluginToLoad.remove(plugin);
                    return;
                }

                loadPlugins(pluginToLoad);

            });
            LOGGER.info("plugin loading finish");
            Thread.currentThread().interrupt();

        }, "PluginLoaderWorker");
        working.start();
        try {
            working.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void handleStop() {
        pluginLoaded.forEach(PluginBase::onDisable);

    }

    private void checkPlugin(File file) throws PluginLoadException {

        if (file.isDirectory()) throw new PluginLoadException(file);

        if (!file.getName().endsWith(".jar")) throw new PluginLoadException(file);

    }

    public PluginBase getPlugin(String name) {

        return pluginLoaded.stream().filter((pl)-> pl.getPluginInformations().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void loadPlugins(Collection<File> toload) {
        toload.forEach((this::loadplugin));
    }

    public void loadplugin(File file) {

        String json = getPluginJson(file);
        PluginInformations pluginInformations = gson.fromJson(json, PluginInformations.class);

        for (PluginBase pluginBase : pluginLoaded) {
            if (pluginBase.getPluginInformations().getId().equalsIgnoreCase(pluginInformations.getId())) return;

        }

        PluginBase plugin = null;
        try {
            LOGGER.info("loading plugin : " + pluginInformations.getName());
            PluginClassLoader classLoader = new PluginClassLoader(pluginInformations.getMainClass(), getClass().getClassLoader(), pluginInformations , file);
            plugin = classLoader.plugin;
            plugin.loaded = true;

        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage());
            plugin.loaded = false;
        }
        pluginLoaded.add(plugin);
        Thread.currentThread().interrupt();

    }

    public void unloadPlugin(PluginBase plugin) {
        plugin.onDisable();
        plugin.loaded = false;
        pluginLoaded.remove(plugin);
    }


    private String getPluginJson(File file) {
        try {
            byte[] buffer = new byte[16384];
            JarFile in = new JarFile(file);

                InputStream ein = in.getInputStream(in.getEntry("plugin.json"));
                StringBuilder stringBuilder = new StringBuilder();

                int nr;
                while(0 < (nr = ein.read(buffer))) {
                    stringBuilder.append(new String(buffer, 0, nr));
                }

                in.close();

                return stringBuilder.toString();
        } catch (IOException var16) {
            var16.printStackTrace();
            return null;
        }
    }
}
