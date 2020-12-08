package fr.modcraftmc.deadbushloader.utils;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class PlayerUtils {

    public static CommandSource getCommandSource(@Nullable ServerPlayerEntity playerIn) {
        String s = playerIn == null ? "Sign" : playerIn.getName().getString();

        BlockPos pos = playerIn.getPosition();
        ITextComponent itextcomponent = playerIn == null ? new StringTextComponent("Modcraft") : playerIn.getDisplayName();
        return new CommandSource(ICommandSource.field_213139_a_, new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D), Vec2f.ZERO, (ServerWorld)playerIn.world, 2, s, itextcomponent, playerIn.world.getServer(), playerIn);
    }
}
