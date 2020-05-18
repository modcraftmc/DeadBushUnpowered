package fr.modcraftmc.pluginloader.mixins;

import fr.modcraftmc.pluginloader.plugin.JavaPluginLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    private JavaPluginLoader javaPluginLoader = new JavaPluginLoader();


    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfoReturnable<Boolean> cir) {
        javaPluginLoader.handleStart();


    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void stopServer(CallbackInfo ci) {
        javaPluginLoader.handleStop();

    }

}
