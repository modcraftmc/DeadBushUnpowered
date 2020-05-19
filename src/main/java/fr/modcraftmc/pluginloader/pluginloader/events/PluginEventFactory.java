package fr.modcraftmc.pluginloader.pluginloader.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;

public class PluginEventFactory {

    public static void onPlayerPreloginEvent(NetworkManager netManager, ServerPlayerEntity playerIn) {

        PlayerEvents.EventPlayerPreLogin event = new PlayerEvents.EventPlayerPreLogin(netManager, playerIn);
        MinecraftForge.EVENT_BUS.post(event);

    }
}
