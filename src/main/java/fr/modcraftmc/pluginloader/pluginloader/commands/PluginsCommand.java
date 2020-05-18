package fr.modcraftmc.pluginloader.pluginloader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.modcraftmc.pluginloader.pluginloader.loader.JavaPluginLoader;
import fr.modcraftmc.pluginloader.pluginloader.plugin.PluginBase;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.HoverEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginsCommand {

    public static JavaPluginLoader javaPluginLoader = new JavaPluginLoader();

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("plugins").executes((cmd)-> execute(cmd.getSource().asPlayer())));


        commandDispatcher.register(
                Commands.literal("plugin")
                        .then(Commands.literal("unload")
                                .then(Commands.argument("pl", StringArgumentType.string())
                                        .executes((cmd)-> unload(cmd, StringArgumentType.getString(cmd, "pl")))))

                        .then(Commands.literal("load")
                                .then(Commands.argument("pl", StringArgumentType.string())
                                        .executes((cmd)-> load(cmd, StringArgumentType.getString(cmd, "pl"))))));
    }

    public static int load(CommandContext<CommandSource> cmd, String plugin) throws CommandSyntaxException {


        ServerPlayerEntity player = cmd.getSource().asPlayer();

        new Thread(() -> {

            player.sendMessage(new StringTextComponent("loading plugin " + plugin));
            javaPluginLoader.loadplugin(new File(javaPluginLoader.pluginFolder, plugin));

        }, "PluginLoadingWorker").start();

        return 1;

    }

    public static int unload(CommandContext<CommandSource> cmd, String plugin) throws CommandSyntaxException {


        ServerPlayerEntity player = cmd.getSource().asPlayer();

        new Thread(() -> {
            player.sendMessage(new StringTextComponent("unloading plugin " + plugin));
            PluginBase pluginBase = javaPluginLoader.getPlugin(plugin);
            if (pluginBase != null)
                javaPluginLoader.unloadPlugin(pluginBase);
            else
                player.sendMessage(new StringTextComponent("plugin == null !"));

        }, "PluginLoadingWorker").start();

        return 1;
    }


    public static int execute(Entity player) {
        List<PluginBase> plugins = JavaPluginLoader.pluginLoaded;
        StringTextComponent header = new StringTextComponent(String.format("Liste des plugins chargés (%s) :", plugins.size()));
        List<ITextComponent> extras = new ArrayList<>();
        for (PluginBase plugin : plugins) {

            ITextComponent text = new StringTextComponent("§a" + plugin.getPluginInformations().getName());
            text.applyTextStyle((component)-> component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(
                    "§aID: " + plugin.getPluginInformations().getId()
            + "\n" + "Nom: " + plugin.getPluginInformations().getName()
            + "\n" + "Créé par: " + Arrays.toString(plugin.getPluginInformations().getAuthors().toArray())
            + "\n" + "Version: " + plugin.getPluginInformations().getVersion()))));
            extras.add(text);
        }

        player.sendMessage(header);
        extras.forEach(player::sendMessage);
        return 1;
    }
}
