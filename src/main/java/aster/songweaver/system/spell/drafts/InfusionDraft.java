package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class InfusionDraft implements Draft {

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        if (data == null) {
            caster.sendMessage(
                    Text.literal("Infusion spell requires data."),
                    true
            );
            return;
        }


        HitResult hit = caster.raycast(5.0D, 0.0F, false);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = world.getBlockState(pos);

        Identifier inputId = new Identifier(data.get("input").getAsString());
        Block input = Registries.BLOCK.get(inputId);

        if (state.getBlock() != input){
            SongServerCasting.sendFailure(caster, CastFailure.NO_COMPONENTS);
            return;
        }

        Identifier outputId = new Identifier(data.get("output").getAsString());
        Block output = Registries.BLOCK.get(outputId);


        world.setBlockState(pos, output.getDefaultState());



    }
}
