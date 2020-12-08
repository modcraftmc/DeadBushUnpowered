package fr.modcraftmc.deadbushloader.pluginloader.loader;

import com.google.gson.Gson;
import fr.modcraftmc.deadbushloader.pluginloader.plugin.MCPlugin;
import fr.modcraftmc.deadbushloader.pluginloader.plugin.PluginInformations;
import fr.modcraftmc.modcraftforge.theading.ModcraftThreadFactory;
import fr.modcraftmc.modcraftforge.theading.ThreadSettings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.jar.JarFile;

public class JavaPluginLoader {

    public File pluginFolder = new File(".", "plugins");
    private final List<PluginClassLoader> loaders = new CopyOnWriteArrayList<>();

    public final ArrayList<MCPlugin> pluginLoaded = new ArrayList<>();

    private final Gson gson = new Gson();

    private final Logger LOGGER = LogManager.getLogger("DeadBushUnPowered");

    private static JavaPluginLoader instance;

    public ExecutorService executor = ModcraftThreadFactory.registerExecutor(new ThreadSettings("DeadBushLoader", ThreadSettings.Type.CACHED, -1));

    public void handleStart() {

            LOGGER.info("DeadBushUnpowered is loading");
            LOGGER.info("loading plugin phase DISCOVERING");

            if (!pluginFolder.exists()) pluginFolder.mkdirs();
            Collection<File> pluginToLoad = FileUtils.listFiles(pluginFolder, null, true);
            if (pluginToLoad.isEmpty()) {
                LOGGER.info("0 plugin to load");
                return;
            }
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
            LOGGER.info("plugins loaded : " + Arrays.toString(pluginLoaded.toArray()));
    }

    public void handleStop() {
        unloadPlugins();
    }

    private void checkPlugin(File file) throws PluginLoadException {

        if (file.isDirectory()) throw new PluginLoadException(file);

        if (!file.getName().endsWith(".jar")) throw new PluginLoadException(file);
    }

    public MCPlugin getPlugin(String name) {

        return pluginLoaded.stream().filter((pl)-> pl.getPluginInformations().getId().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void loadPlugins(Collection<File> toload) {
        toload.forEach((this::loadplugin));
    }

    public void unloadPlugins() {
        pluginLoaded.forEach(this::unloadPlugin);
    }

    public void loadplugin(File file) {

        MCPlugin plugin = null;
        String json = null;
        PluginInformations pluginInformations = null;
        try {
            json = getPluginJson(file);
            pluginInformations = gson.fromJson(json, PluginInformations.class);

        } catch (IOException e) {
            LOGGER.error("Failed to read plugin.json of {}. valid deadbush plugin ? : {}", file.getName(), e.getMessage());
        }
        try {

            LOGGER.info("loading plugin : " + pluginInformations.getName());

            PluginClassLoader classLoader = new PluginClassLoader(pluginInformations.getMainClass(), getClass().getClassLoader(), pluginInformations , file);
            plugin = classLoader.getPlugin();

            pluginLoaded.add(plugin);
            loaders.add(classLoader);
            plugin.loaded = true;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            plugin.loaded = false;
        }

    }

    public synchronized void unloadPlugin(MCPlugin plugin) {
        synchronized (pluginLoaded) {
            plugin.onDisable();
            plugin.loaded = false;
            pluginLoaded.remove(plugin);
        }
    }


    private String getPluginJson(File file) throws IOException {
        JarFile jar = new JarFile(file);
        InputStream in = jar.getInputStream(jar.getEntry("plugin.json"));
        return IOUtils.toString(in, StandardCharsets.UTF_8);
    }

    public static JavaPluginLoader getInstance() {
        return instance == null ? instance = new JavaPluginLoader() : instance;
    }
}
