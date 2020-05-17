package fr.modcraftmc.pluginloader;

import fr.modcraftmc.pluginloader.plugin.PluginBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("deadbushunpowered")
public class DeadBushUnpowered {

    public DeadBushUnpowered() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(PluginBase::onServerShutdown);


    }

}
