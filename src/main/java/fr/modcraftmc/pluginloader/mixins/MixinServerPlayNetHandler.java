package fr.modcraftmc.pluginloader.mixins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetHandler.class)
public abstract class MixinServerPlayNetHandler {

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private MinecraftServer server;

    @Shadow public ServerPlayerEntity player;

    @Shadow protected abstract boolean func_217264_d();

    /**
     * @author
     */
    @Overwrite
    public void onDisconnect(ITextComponent reason) {
        this.LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
        this.server.refreshStatusNextTick();
        this.server.getPlayerList().sendMessage((new StringTextComponent("§8[§4-§8]§f " + this.player.getDisplayName().getFormattedText())));
        this.player.disconnect();
        this.server.getPlayerList().playerLoggedOut(this.player);
        if (this.func_217264_d()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.initiateShutdown(false);
        }

    }
}
