package fr.modcraftmc.pluginloader.plugin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.realmsclient.gui.ChatFormatting;
import fr.modcraftmc.pluginloader.plugin.JavaPluginLoader;
import fr.modcraftmc.pluginloader.plugin.PluginBase;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class PluginCommand {

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("plugins").executes((cmd)-> execute(cmd.getSource().asPlayer())));

    }

    public static int execute(Entity player) {

        StringTextComponent header = new StringTextComponent("Liste des plugins chargés");
        List<PluginBase> plugins = JavaPluginLoader.pluginLoaded;
        StringBuilder builder = new StringBuilder();
        for (PluginBase plugin : plugins) {
            builder.append(plugin.getPluginInformations().getName());
            builder.append(", ");
        }
        StringTextComponent pluginlist = new StringTextComponent(builder.toString());



        player.sendMessage(header);
        player.sendMessage(pluginlist);
        return 1;
    }
}
