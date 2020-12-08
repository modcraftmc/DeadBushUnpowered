package fr.modcraftmc.deadbushloader;

import fr.modcraftmc.deadbushloader.pluginloader.commands.PluginsCommand;
import fr.modcraftmc.deadbushloader.pluginloader.commands.TpsCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod("deadbushunpowered")
public class DeadBushUnpowered {

    public static boolean CommandBlockEnabled = false;

    public DeadBushUnpowered() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    public void onServerStart(final FMLServerStartingEvent event) {
        PluginsCommand.register(event.getCommandDispatcher());
        TpsCommand.register(event.getCommandDispatcher());
    }
}
