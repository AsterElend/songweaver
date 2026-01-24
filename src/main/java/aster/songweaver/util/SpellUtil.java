package aster.songweaver.util;

import aster.songweaver.client.InputBuffer;
import aster.songweaver.registry.LoomTags;
import aster.songweaver.registry.NoteHolderItem;
import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.registry.physical.ritual.KhipuHookBlockEntity;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Note;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellUtil {

    private static boolean hasValidKhipu(GrandLoomBlockEntity loom){
        KhipuHookBlockEntity hook = loom.getKhipuHook(loom.getPos());
        //terrible joke
        BlockPos possible = hook.getStoredPos();

        return possible != null;

    }
@Nullable
   public static BlockPos getKhipuPosOrLoomPosIfAbsent(GrandLoomBlockEntity loom) {
        if (!hasValidKhipu(loom)){
            return loom.getPos();
        }
        KhipuHookBlockEntity hook = loom.getKhipuHook(loom.getPos());



        if (hook.getStoredPos() == null) {
            return loom.getPos();

        }
        return hook.getStoredPos();

    }

    public static void breakBlockLikePlayer(
            ServerWorld world,
            ServerPlayerEntity player,
            BlockPos pos,
            ItemStack tool
    ) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (state.isAir()) return;
        if (state.getHardness(world, pos) < 0.0F) return;
        if (!player.canModifyAt(world, pos)) return;

        // Break particles + sound
        world.syncWorldEvent(
                WorldEvents.BLOCK_BROKEN,
                pos,
                Block.getRawIdFromState(state)
        );

        // BEFORE break hook
        block.onBreak(world, pos, state, player);

        // Drop loot + XP
        block.afterBreak(
                world,
                player,
                pos,
                state,
                world.getBlockEntity(pos),
                tool
        );

        // Remove block
        world.removeBlock(pos, false);
        world.playSound(
                null,
                pos,
                state.getSoundGroup().getBreakSound(),
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
        );

        // Stats & exhaustion
        player.incrementStat(Stats.MINED.getOrCreateStat(block));
        player.addExhaustion(0.005F);
    }

    @Nullable
    public static LivingEntity resolveTarget(ServerPlayerEntity caster, double range, boolean allowCaster) {
        if (caster.isSneaking() && allowCaster) {
            return caster;
        }

        EntityHitResult hit = ProjectileUtil.getEntityCollision(
                caster.getWorld(),
                caster,
                caster.getEyePos(),
                caster.getEyePos().add(caster.getRotationVec(1.0F).multiply(range)),
                caster.getBoundingBox().stretch(caster.getRotationVec(1.0F).multiply(range)).expand(1.0),
                entity -> entity instanceof LivingEntity && entity != caster
        );

        return hit != null ? (LivingEntity) hit.getEntity() : null;
    }

    @Nullable
    public static List<LivingEntity> radiusGetTargets(ServerWorld world, BlockPos center, double radius){
        Box box = new Box(center).expand(radius);

        return world.getEntitiesByClass(
                LivingEntity.class,
                box,
                entity -> entity.squaredDistanceTo(
                        center.getX() + 0.5,
                        center.getY() + 0.5,
                        center.getZ() + 0.5
                ) <= radius * radius
        );
    }

    public static void clientCast(PlayerEntity user, Hand hand, boolean spindle) {
        List<Note> notes;

        if (spindle){
            notes = NoteHolderItem.NotestoreUtil.readNotes(user.getStackInHand(hand));
        } else{
            notes = InputBuffer.snapshot();
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(notes.size());
        for (Note note : notes) {
            buf.writeEnumConstant(note);
        }

        ClientPlayNetworking.send(SongServerCasting.CAST_DRAFT_PACKET, buf);
        if (!spindle) {InputBuffer.clear();}
    }

    public static boolean tryClientCast(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!stack.isIn(LoomTags.CASTING_ITEMS)) {
            return false;
        }

        boolean spindle = stack.getItem() instanceof NoteHolderItem;

        // No notes? Don't steal the interaction.
        if (spindle) {
            if (NoteHolderItem.NotestoreUtil.notHasNotes(stack)) {
                return false;
            }
        } else {
            if (InputBuffer.isEmpty()) {
                return false;
            }
        }

        clientCast(player, hand, spindle);
        return true;
    }

    public static Set<EntityType<?>> getLegalTargets(JsonObject json){

        JsonArray entityArray = json.getAsJsonArray("entities");

        Set<EntityType<?>> allowedTargets = new HashSet<>();
        for (JsonElement element : entityArray) {
            Identifier id = new Identifier(element.getAsString());
            allowedTargets.add(Registries.ENTITY_TYPE.get(id));
        }

        return allowedTargets;

    }

}
