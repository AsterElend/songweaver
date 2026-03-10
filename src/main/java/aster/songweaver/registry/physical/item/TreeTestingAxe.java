package aster.songweaver.registry.physical.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class TreeTestingAxe extends Item {

    public TreeTestingAxe(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        BlockPos start = context.getBlockPos();

        if (world.isClient) return ActionResult.SUCCESS;



        if (context.getPlayer().isSneaking()){
            forceSapling((ServerWorld) world, start);
            return ActionResult.SUCCESS;
        }

        removeTree((ServerWorld) world, start);

        return ActionResult.SUCCESS;
    }

    private void forceSapling(ServerWorld world, BlockPos start){
        BlockState state = world.getBlockState(start);
        if (state.isIn(BlockTags.SAPLINGS)){
            SaplingBlock sapling = (SaplingBlock) state.getBlock();
            sapling.generate(world, start, state, world.getRandom());
        }

    }

    private void removeTree(ServerWorld world, BlockPos start) {


        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(start);

        while (!queue.isEmpty()) {

            BlockPos pos = queue.poll();

            if (visited.contains(pos)) continue;
            visited.add(pos);

            BlockState state = world.getBlockState(pos);

            if (!state.isIn(BlockTags.LOGS) && !state.isIn(BlockTags.LEAVES)) {
                continue;
            }

            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {

                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos next = pos.add(dx, dy, dz);

                        if (!visited.contains(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
            }
        }
    }

