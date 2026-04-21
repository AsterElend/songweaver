package aster.songweaver.registry.physical.item;

import aster.songweaver.api.WardedBlocksState;
import aster.songweaver.api.cast.SongweaverPackets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class WardingPrismItem extends Item {
    public WardingPrismItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;


        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        WardedBlocksState state = WardedBlocksState.get(world);

        boolean warded = state.isWarded(pos, world.getBlockState(pos));

        if (player.isSneaking()) {
            toggleConnected(world, pos, state, !warded);
        } else {

            boolean newState = !warded;

            BlockState current = world.getBlockState(pos);
            state.setWarded(pos, current, newState);
            WardedBlocksState.syncToTracking((ServerWorld) world, pos, newState);
        }

        return ActionResult.SUCCESS;
    }

    private void toggleConnected(World world, BlockPos start, WardedBlocksState state, boolean ward) {

        BlockState startState = world.getBlockState(start);
        if (startState.isAir()) return;

        Block target = startState.getBlock();

        Queue<BlockPos> queue = new ArrayDeque<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        List<BlockPos> affected = new ArrayList<>();

        int limit = 256;

        queue.add(start);

        while (!queue.isEmpty() && visited.size() < limit) {

            BlockPos pos = queue.poll();
            long key = pos.asLong();

            // ✅ skip if already visited
            if (!visited.add(key)) continue;

            BlockState current = world.getBlockState(pos);

            // ✅ only operate on matching blocks
            if (current.getBlock() != target) continue;

            // ✅ skip air (extra safety)
            if (current.isAir()) continue;

            // ✅ apply ward
            state.setWarded(pos, current, ward);
            affected.add(pos);

            // ✅ only expand from valid blocks
            for (Direction d : Direction.values()) {
                BlockPos next = pos.offset(d);
                if (!visited.contains(next.asLong())) {
                    queue.add(next);
                }
            }
        }

        // ✅ ONLY sync actual changed blocks
        if (!affected.isEmpty()) {
            syncBatch((ServerWorld) world, affected, ward);
        }
    }

    public static void syncBatch(ServerWorld world, Collection<BlockPos> positions, boolean warded) {

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(positions.size());
        buf.writeBoolean(warded);

        for (BlockPos pos : positions) {
            buf.writeLong(pos.asLong());
        }

        for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
            PacketByteBuf copy = new PacketByteBuf(buf.copy());
            ServerPlayNetworking.send(player, SongweaverPackets.SYNC_WARDS, copy);
        }
    }
}
