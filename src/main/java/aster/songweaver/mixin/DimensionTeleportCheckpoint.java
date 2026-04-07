package aster.songweaver.mixin;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.dimension.LoomDimensions;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.status.UnravelingEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class DimensionTeleportCheckpoint {

    @Shadow public abstract World getWorld();

    @Shadow @Nullable public abstract MinecraftServer getServer();

    @Shadow public abstract boolean isPlayer();

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At("HEAD"))
    private void enterExitHighWilderness(ServerWorld destination, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch,
                                         CallbackInfoReturnable<Boolean> cir) {
        World origin = this.getWorld();
        Entity maybePlayer =(Entity) (Object) this;

        if (destination == getServer().getWorld(LoomDimensions.HIGH_WILDERNESS) && this.isPlayer()){
            PlayerEntity player = (PlayerEntity) maybePlayer;
            enterHighWilderness(player, player.getWorld().getRegistryKey());
        }

        if (origin == getServer().getWorld(LoomDimensions.HIGH_WILDERNESS) && this.isPlayer()){
            PlayerEntity player = (PlayerEntity) maybePlayer;
            leaveHighWilderness(player);
        }

    }

    @Unique
    private static void enterHighWilderness(PlayerEntity player, RegistryKey<World> origin){
        ItemStack thread = new ItemStack(LoomItems.THREAD);

        NbtCompound tag = thread.getOrCreateNbt();

        tag.putInt("x", player.getBlockPos().getX());
        tag.putInt("y", player.getBlockPos().getY());
        tag.putInt("z", player.getBlockPos().getZ());
        tag.putString("dim", origin.getValue().toString()); // ✅ correct

        player.giveItemStack(thread);

        player.addStatusEffect(new StatusEffectInstance(
                LoomMiscRegistry.UNRAVELING,
                UnravelingEffect.BASE_DURATION,
                0
        ));

    }

    @Unique
    private static void leaveHighWilderness(PlayerEntity player){
        SongweaverComponents.UNRAVELING_STATE.get(player).setClearable(true);
        SongweaverComponents.UNRAVELING_STATE.sync(player);
        player.removeStatusEffect(LoomMiscRegistry.UNRAVELING);
        player.getInventory().remove(stack -> stack.isOf(LoomItems.THREAD), 2000000, player.getInventory());
        SongweaverComponents.UNRAVELING_STATE.get(player).setClearable(false);
        SongweaverComponents.UNRAVELING_STATE.sync(player);
    }
}
