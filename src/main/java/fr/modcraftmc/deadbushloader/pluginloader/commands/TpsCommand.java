package fr.modcraftmc.deadbushloader.pluginloader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber
public class TpsCommand {

    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");
    private static final long[] UNLOADED = new long[] {0};

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("tps").executes((cmd)-> getTps(cmd)));


    }

    private static int getTps(CommandContext<CommandSource> ctx) {

        double meanTickTime = mean(ctx.getSource().getServer().tickTimeArray) * 1.0E-6D;
        double meanTPS = Math.min(1000.0/meanTickTime, 20);
        ctx.getSource().sendFeedback(new StringTextComponent("SERVER MS: " + TIME_FORMATTER.format(meanTickTime) + " SERVER TPS: " + TIME_FORMATTER.format(meanTPS)), false);
        ITextComponent component = new StringTextComponent("Click here for more informations");
        component.applyTextStyle((c) -> c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spark tps")));
        ctx.getSource().sendFeedback(component, false);

        return 1;
    }



    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
