package aster.songweaver.system.spell.mining;

import aster.songweaver.system.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static aster.songweaver.system.spell.DraftUtil.breakBlockLikePlayer;

public class SilkBreakDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {


        HitResult hit = caster.raycast(5.0D, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult bhr = (BlockHitResult) hit;
        BlockPos pos = bhr.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) return;
        if (state.getHardness(world, pos) < 0.0F) return;
        if (!caster.canModifyAt(world, pos)) return;

        // Fake silk-touch tool
        ItemStack silkTool = new ItemStack(caster.getMainHandStack().getItem());
        silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1);

      breakBlockLikePlayer(world, caster, pos, silkTool);
    }
}
