package fr.modcraftmc.deadbushloader.pluginloader.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class PlayerEvents extends Event {

    @Cancelable
    public static class EventPlayerPreLogin extends Event{

        private final NetworkManager networkManager;
        private final ServerPlayerEntity playerEntity;

        public EventPlayerPreLogin(NetworkManager netManager, ServerPlayerEntity playerIn) {
            this.networkManager = netManager;
            this.playerEntity = playerIn;

        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }

        public ServerPlayerEntity getPlayer() {
            return playerEntity;
        }
    }
}
