package fr.modcraftmc.deadbushloader.mixins;

import com.mojang.authlib.GameProfile;
import fr.modcraftmc.deadbushloader.pluginloader.events.PluginEventFactory;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Shadow @Nullable public abstract CompoundNBT readPlayerDataFromFile(ServerPlayerEntity playerIn);

    @Shadow @Final private MinecraftServer server;

    @Shadow @Final private static Logger LOGGER;

    @Shadow protected abstract void setPlayerGameTypeBasedOnOther(ServerPlayerEntity target, ServerPlayerEntity source, IWorld worldIn);

    @Shadow public abstract MinecraftServer getServer();

    @Shadow public abstract void updatePermissionLevel(ServerPlayerEntity player);

    @Shadow protected abstract void sendScoreboard(ServerScoreboard scoreboardIn, ServerPlayerEntity playerIn);

    @Shadow public abstract void sendMessage(ITextComponent component);

    @Shadow public abstract boolean addPlayer(ServerPlayerEntity player);

    @Shadow @Final private Map<UUID, ServerPlayerEntity> uuidToPlayerMap;

    @Shadow public abstract void sendPacketToAllPlayers(IPacket<?> packetIn);

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow public abstract void func_72354_b(ServerPlayerEntity playerIn, ServerWorld worldIn);

    @Shadow private int viewDistance;

    @Shadow public abstract int getMaxPlayers();



    /**
     * @author mojang
     */
    @Overwrite
    public void initializeConnectionToPlayer(NetworkManager netManager, ServerPlayerEntity playerIn) {

        GameProfile gameprofile = playerIn.getGameProfile();
        PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
        GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
        playerprofilecache.addEntry(gameprofile);
        CompoundNBT compoundnbt = this.readPlayerDataFromFile(playerIn);


        PluginEventFactory.onPlayerPreloginEvent(netManager, playerIn);

        //Forge: Make sure the dimension hasn't been deleted, if so stick them in the overworld.
        ServerWorld serverworld = playerIn.dimension != null ? this.server.func_71218_a(playerIn.dimension) : null ;
        if (serverworld == null) {
            playerIn.dimension = DimensionType.OVERWORLD;
            serverworld = this.server.func_71218_a(playerIn.dimension);
            playerIn.setPosition(serverworld.getWorldInfo().getSpawnX(), serverworld.getWorldInfo().getSpawnY(), serverworld.getWorldInfo().getSpawnZ());
        }

        playerIn.setWorld(serverworld);
        playerIn.interactionManager.func_73080_a((ServerWorld)playerIn.world);
        String s1 = "local";
        if (netManager.getRemoteAddress() != null) {
            s1 = netManager.getRemoteAddress().toString();
        }

        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName().getString(), s1, playerIn.getEntityId(), playerIn.func_226277_ct_(), playerIn.func_226278_cu_(), playerIn.func_226281_cx_());
        WorldInfo worldinfo = serverworld.getWorldInfo();
        this.setPlayerGameTypeBasedOnOther(playerIn, null, serverworld);
        ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, netManager, playerIn);
        net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(netManager, "PLAY_TO_CLIENT");
        net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(netManager, playerIn);
        GameRules gamerules = serverworld.getGameRules();
        boolean flag = gamerules.getBoolean(GameRules.field_226683_z_);
        boolean flag1 = gamerules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        serverplaynethandler.sendPacket(new SJoinGamePacket(playerIn.getEntityId(), playerIn.interactionManager.getGameType(), WorldInfo.func_227498_c_(worldinfo.getSeed()), worldinfo.isHardcore(), serverworld.dimension.getType(), this.getMaxPlayers(), worldinfo.getGenerator(), this.viewDistance, flag1, !flag));
        serverplaynethandler.sendPacket(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServer().getServerModName())));
        serverplaynethandler.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
        serverplaynethandler.sendPacket(new SPlayerAbilitiesPacket(playerIn.abilities));
        serverplaynethandler.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
        serverplaynethandler.sendPacket(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        serverplaynethandler.sendPacket(new STagsListPacket(this.server.getNetworkTagManager()));
        this.updatePermissionLevel(playerIn);
        playerIn.getStats().markAllDirty();
        playerIn.getRecipeBook().init(playerIn);
        this.sendScoreboard(serverworld.func_96441_U(), playerIn);
        this.server.refreshStatusNextTick();
        serverplaynethandler.setPlayerLocation(playerIn.func_226277_ct_(), playerIn.func_226278_cu_(), playerIn.func_226281_cx_(), playerIn.rotationYaw, playerIn.rotationPitch);
        this.addPlayer(playerIn);
        this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
        this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, playerIn));

        for(int i = 0; i < this.players.size(); ++i) {
            playerIn.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
        }

        serverworld.addNewPlayer(playerIn);
        this.server.getCustomBossEvents().onPlayerLogin(playerIn);
        this.func_72354_b(playerIn, serverworld);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }

        for(EffectInstance effectinstance : playerIn.getActivePotionEffects()) {
            serverplaynethandler.sendPacket(new SPlayEntityEffectPacket(playerIn.getEntityId(), effectinstance));
        }

        if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
            final ServerWorld worldf = serverworld;
            Entity entity1 = EntityType.func_220335_a(compoundnbt1.getCompound("Entity"), serverworld, (p_217885_1_) -> {
                return !worldf.summonEntity(p_217885_1_) ? null : p_217885_1_;
            });
            if (entity1 != null) {
                UUID uuid = compoundnbt1.getUniqueId("Attach");
                if (entity1.getUniqueID().equals(uuid)) {
                    playerIn.startRiding(entity1, true);
                } else {
                    for(Entity entity : entity1.getRecursivePassengers()) {
                        if (entity.getUniqueID().equals(uuid)) {
                            playerIn.startRiding(entity, true);
                            break;
                        }
                    }
                }

                if (!playerIn.isPassenger()) {
                    LOGGER.warn("Couldn't reattach entity to player");
                    serverworld.removeEntity(entity1);

                    for(Entity entity2 : entity1.getRecursivePassengers()) {
                        serverworld.removeEntity(entity2);
                    }
                }
            }
        }

        ITextComponent itextcomponent = new StringTextComponent("§8[§a+§8]§f " + playerIn.getDisplayName().getFormattedText());

        this.sendMessage(itextcomponent);

        playerIn.addSelfToInternalCraftingInventory();
        net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedIn( playerIn );

    }

}
