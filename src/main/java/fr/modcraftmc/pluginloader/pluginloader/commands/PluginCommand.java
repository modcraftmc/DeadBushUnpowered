package fr.modcraftmc.pluginloader.pluginloader.commands;

import com.mojang.brigadier.CommandDispatcher;
import fr.modcraftmc.pluginloader.pluginloader.loader.JavaPluginLoader;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginBase;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginCommand {

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("plugins").executes((cmd)-> execute(cmd.getSource().asPlayer())));

    }

    public static int execute(Entity player) {
        List<PluginBase> plugins = JavaPluginLoader.pluginLoaded;
        StringTextComponent header = new StringTextComponent(String.format("Liste des plugins chargés (%s) :", plugins.size()));
        List<ITextComponent> extras = new ArrayList<>();
        for (PluginBase plugin : plugins) {

            ITextComponent text = new StringTextComponent("§e" + plugin.getPluginInformations().getName());
            text.applyTextStyle((test)-> {
                test.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("§aID: " + plugin.getPluginInformations().getId()
                + "\n" + "Nom: " + plugin.getPluginInformations().getName()
                + "\n" + "Créé par: " + Arrays.toString(plugin.getPluginInformations().getAuthors().toArray()))));
            });
            extras.add(text);
        }




        player.sendMessage(header);
        extras.forEach(player::sendMessage);
        return 1;
    }
}
