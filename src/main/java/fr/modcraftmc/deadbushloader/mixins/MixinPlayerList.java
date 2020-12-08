package fr.modcraftmc.deadbushloader.mixins;

import fr.modcraftmc.deadbushloader.pluginloader.events.PluginEventFactory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Inject(method = "initializeConnectionToPlayer", at = @At("HEAD"))
    public void firePreLoginEvent(NetworkManager netManager, ServerPlayerEntity playerIn, CallbackInfo ci) {
        PluginEventFactory.onPlayerPreloginEvent(netManager, playerIn);

    }
}
