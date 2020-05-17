package fr.modcraftmc.pluginloader.mixins;

import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfoReturnable<Boolean> cir) {
        LOGGER.info("loading PluginClassLoader");


    }

}
