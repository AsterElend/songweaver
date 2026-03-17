package aster.songweaver.api;

import aster.songweaver.registry.physical.LoomBlockStuff;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GenericSpaceSapling extends SaplingBlock {
    public GenericSpaceSapling(SaplingGenerator generator, Settings settings){
        super (generator, settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos){
        return floor.isOf(LoomBlockStuff.VOIDSTONE);
    }
}
