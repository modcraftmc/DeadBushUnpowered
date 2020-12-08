package fr.modcraftmc.deadbushloader.pluginloader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.modcraftmc.deadbushloader.pluginloader.loader.JavaPluginLoader;
import fr.modcraftmc.deadbushloader.pluginloader.plugin.MCPlugin;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginsCommand {

    public static JavaPluginLoader javaPluginLoader = JavaPluginLoader.getInstance();

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("plugins").executes((cmd)-> list(cmd.getSource().asPlayer())));

        commandDispatcher.register(
                Commands.literal("plugin")
                        .then(Commands.literal("unload")
                                .then(Commands.argument("pl", StringArgumentType.string())
                                        .executes((cmd)-> unload(cmd, StringArgumentType.getString(cmd, "pl")))))

                        .then(Commands.literal("load")
                                .then(Commands.argument("pl", StringArgumentType.string())
                                        .executes((cmd)-> load(cmd, StringArgumentType.getString(cmd, "pl")))))
                        .then(Commands.literal("info")
                                .then(Commands.argument("pl", StringArgumentType.string())
                                        .executes((cmd) -> info(cmd, StringArgumentType.getString(cmd, "pl"))))));
    }

    public static int load(CommandContext<CommandSource> cmd, String plugin) throws CommandSyntaxException {
        CommandSource player = cmd.getSource();

        javaPluginLoader.executor.submit(() -> {
            player.sendFeedback(new StringTextComponent("loading plugin " + plugin), true);
            javaPluginLoader.loadplugin(new File(javaPluginLoader.pluginFolder, plugin));
        });

        return 1;
    }

    public static int unload(CommandContext<CommandSource> cmd, String plugin) throws CommandSyntaxException {

        CommandSource player = cmd.getSource();

        javaPluginLoader.executor.submit(() -> {
            player.sendFeedback(new StringTextComponent("unloading plugin " + plugin), true);
            MCPlugin pluginBase = javaPluginLoader.getPlugin(plugin);
            if (pluginBase != null)
                javaPluginLoader.unloadPlugin(pluginBase);
            else
                player.sendFeedback(new StringTextComponent("no plugins with this name !"), true);
        });

        return 1;
    }


    public static int list(Entity player) {
        List<MCPlugin> plugins = javaPluginLoader.pluginLoaded;
        StringTextComponent header = new StringTextComponent(String.format("Liste des plugins chargés (%s) :", plugins.size()));
        List<ITextComponent> extras = new ArrayList<>();
        for (MCPlugin plugin : plugins) {

            ITextComponent text = new StringTextComponent("§a" + plugin.getPluginInformations().getName());
            text.applyTextStyle((component)-> {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(
                        "§aID: " + plugin.getPluginInformations().getId()
                                + "\n" + "Nom: " + plugin.getPluginInformations().getName()
                                + "\n" + "Description: " + plugin.getPluginInformations().getDescription()
                                + "\n" + "Créé par: " + Arrays.toString(plugin.getPluginInformations().getAuthors().toArray())
                                + "\n" + "Version: " + plugin.getPluginInformations().getVersion())));


                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/plugin info " + plugin.getPluginInformations().getId()));
            });


            extras.add(text);
        }

        player.sendMessage(header);
        extras.forEach(player::sendMessage);
        return 1;
    }

    public static int info(CommandContext<CommandSource> cmd, String pluginName) throws CommandSyntaxException {

        CommandSource player = cmd.getSource();

        MCPlugin plugin = javaPluginLoader.pluginLoaded.stream().filter(pl -> pl.getPluginInformations().getId().equalsIgnoreCase(pluginName)).findFirst().get();

        StringTextComponent msg = new StringTextComponent(
                "§aID: §8" + plugin.getPluginInformations().getId()
                        + "\n" + "§aNom: §8" + plugin.getPluginInformations().getName()
                        + "\n" + "§aDescription: §8" + plugin.getPluginInformations().getDescription()
                        + "\n" + "§aCréé par: §8" + Arrays.toString(plugin.getPluginInformations().getAuthors().toArray())
                        + "\n" + "§aVersion: §8" + plugin.getPluginInformations().getVersion());

        player.sendFeedback(msg, false);

        return 1;
    }
}
