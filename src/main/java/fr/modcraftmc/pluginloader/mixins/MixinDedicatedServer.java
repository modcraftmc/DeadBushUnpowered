package fr.modcraftmc.pluginloader.mixins;

import com.google.gson.Gson;
import fr.modcraftmc.pluginloader.plugin.*;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PendingCommand;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {


    private static File pluginFolder = new File(".", "plugins");
    private static File configFolder = new File(pluginFolder, "configs");

    private static ArrayList<Plugin> pluginLoaded = new ArrayList<>();

    private static Gson gson = new Gson();

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow @Final public List<PendingCommand> pendingCommandList;

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfoReturnable<Boolean> cir) {
        LOGGER.info("DeadBushUnpowered is loading");

        LOGGER.info("loading plugin phase DISCOVERING");
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        Collection<File> pluginToLoad = FileUtils.listFiles(pluginFolder, null, true);

        pluginToLoad.forEach((plugin)-> {

           LOGGER.info("attempting to load" + plugin.getName());

           try {
               checkPlugin(plugin);
           } catch (PluginLoadException e) {
               e.printStackTrace();
               return;
           }

        });

    }

    private void checkPlugin(File file) throws PluginLoadException {

        if (file.isDirectory()) throw new PluginLoadException(file);

        if (!file.getName().endsWith(".jar")) throw new PluginLoadException(file);

        String json = getPluginJson(file);
        PluginInformations pluginInformations = gson.fromJson(json, PluginInformations.class);

        try {
            LOGGER.info("successfully load plugin : " + pluginInformations.getName());
            PluginClassLoader classLoader = new PluginClassLoader(pluginInformations.getMainClass(), getClass().getClassLoader(), pluginInformations ,file);
            PluginBase plugin = classLoader.plugin;

            pluginLoaded.add(plugin);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }




    }

    private String getPluginJson(File file) {
        try {
            byte[] buffer = new byte[16384];
            JarFile in = new JarFile(file);

            try {
                InputStream ein = in.getInputStream(in.getEntry("plugin.json"));
                StringBuilder stringBuilder = new StringBuilder();

                int nr;
                while(0 < (nr = ein.read(buffer))) {
                    stringBuilder.append(new String(buffer, 0, nr));
                }

                in.close();


                return stringBuilder.toString();
            } catch (NullPointerException var15) {
                var15.printStackTrace();
                return null;
            }
        } catch (IOException var16) {
            var16.printStackTrace();
            return null;
        }
    }

}
