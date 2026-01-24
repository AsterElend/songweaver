package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EnchantedBreakDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        if (data == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }


        Identifier enchantId = new Identifier(data.get("enchantment").getAsString());

        Enchantment magic = Registries.ENCHANTMENT.get(enchantId);

        if (magic == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        int level = data.has("level") ? data.get("level").getAsInt() : 1;




        HitResult hit = caster.raycast(5.0D, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult bhr = (BlockHitResult) hit;
        BlockPos pos = bhr.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) return;
        if (state.getHardness(world, pos) < 0.0F) return;
        if (!caster.canModifyAt(world, pos)) return;

        // Fake silk-touch tool
        ItemStack magicTool = new ItemStack(caster.getMainHandStack().getItem());
        magicTool.addEnchantment(magic, level);

        SpellUtil.breakBlockLikePlayer(world, caster, pos, magicTool);
    }

}
