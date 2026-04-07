package aster.songweaver.registry.physical.block;

import aster.songweaver.Songweaver;
import aster.songweaver.cca.SongweaverComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class LetheanWaterBlock extends FluidBlock {
    public LetheanWaterBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            if (player.isSubmergedIn(TagKey.of(RegistryKeys.FLUID, Songweaver.locate("lethean")))) {
                if (player.age % 100 == 0) {
                    SongweaverComponents.FORGOTTEN.get(player).forgetRandom(player);
                }
            }
        }
    }
}
