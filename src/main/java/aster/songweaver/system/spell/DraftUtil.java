package aster.songweaver.system.spell;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class DraftUtil {

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
    public static LivingEntity resolveTarget(ServerPlayerEntity caster, double range) {
        if (caster.isSneaking()) {
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


}
