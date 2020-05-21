package fr.modcraftmc.deadbushloader.mixins;

import fr.modcraftmc.deadbushloader.DeadBushUnpowered;
import fr.modcraftmc.deadbushloader.pluginloader.loader.JavaPluginLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    private final JavaPluginLoader javaPluginLoader = new JavaPluginLoader();


    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfoReturnable<Boolean> cir) {
        javaPluginLoader.handleStart();

    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void stopServer(CallbackInfo ci) {
        javaPluginLoader.handleStop();

    }

    @Inject(method = "isCommandBlockEnabled", at = @At("RETURN"))
    public void commandBlock(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(DeadBushUnpowered.CommandBlockEnabled);

    }

}
