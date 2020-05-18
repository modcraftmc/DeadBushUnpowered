package fr.modcraftmc.pluginloader;

import fr.modcraftmc.pluginloader.plugin.commands.PluginCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("deadbushunpowered")
public class DeadBushUnpowered {

    public DeadBushUnpowered() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.register(this);


    }

    @SubscribeEvent
    public void onServerStart(final FMLServerStartingEvent event) {
        PluginCommand.register(event.getCommandDispatcher());
    }

}
