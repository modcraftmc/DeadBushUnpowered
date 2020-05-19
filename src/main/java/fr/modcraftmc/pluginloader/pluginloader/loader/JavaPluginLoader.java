package fr.modcraftmc.pluginloader.pluginloader.loader;

import com.google.gson.Gson;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginBase;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginInformations;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;

public class JavaPluginLoader {

    public File pluginFolder = new File(".", "plugins");
    private final File configFolder = new File(pluginFolder, "configs");

    private final List<PluginClassLoader> loaders = new CopyOnWriteArrayList<>();

    public ArrayList<PluginBase> pluginLoaded = new ArrayList<>();

    private static final Gson gson = new Gson();

    private static final Logger LOGGER = LogManager.getLogger("PluginLoader");

    private static JavaPluginLoader instance;

    public static Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
        e.printStackTrace();
        t.interrupt();

    };

    public JavaPluginLoader() {
        instance = this;

    }


    public void handleStart() {

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
            LOGGER.info("plugins loaded : " + Arrays.toString(pluginLoaded.toArray()));


    }

    public void handleStop() {
        unloadPlugins();

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

    public void unloadPlugins() {
        pluginLoaded.forEach(PluginBase::onDisable);
    }

    public void loadplugin(File file) {

        String json = getPluginJson(file);
        PluginInformations pluginInformations = gson.fromJson(json, PluginInformations.class);

        PluginBase plugin = null;
        try {
            LOGGER.info("loading plugin : " + pluginInformations.getName());
            PluginClassLoader classLoader = new PluginClassLoader(pluginInformations.getMainClass(), getClass().getClassLoader(), pluginInformations , file);
            MinecraftForge.EVENT_BUS.register(classLoader);
            plugin = classLoader.getPlugin();

            pluginLoaded.add(plugin);
            loaders.add(classLoader);
            plugin.loaded = true;


        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage());
            plugin.loaded = false;
        }

    }

    public void unloadPlugin(PluginBase plugin) {
        plugin.onDisable();
        plugin.loaded = false;
        pluginLoaded.remove(plugin);
    }


    private String getPluginJson(File file) {
        try {
            byte[] buffer = new byte[1024];
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

    public static JavaPluginLoader getInstance() {
        return instance;
    }
}
