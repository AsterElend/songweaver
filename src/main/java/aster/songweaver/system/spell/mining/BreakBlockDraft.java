package aster.songweaver.system.spell.mining;

import aster.songweaver.system.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BreakBlockDraft implements Draft {

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {




        HitResult hit = caster.raycast(5.0D, 0.0F, false);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = world.getBlockState(pos);



        // Air or nothing
        if (state.isAir()) return;

        // Unbreakable blocks (bedrock, end portal frames, etc.)
        if (state.getHardness(world, pos) < 0.0F) return;

        // Permission check (claims, spawn protection, etc.)
        if (!caster.canModifyAt(world, pos)) return;


        // Break as player (respects tool, drops, permissions)
        world.breakBlock(
                blockHit.getBlockPos(),
                true,
                caster
        );
    }
}
