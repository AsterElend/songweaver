package aster.songweaver.api;

import aster.songweaver.api.cast.SongweaverPackets;
import aster.songweaver.registry.physical.item.WardingPrismItem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class WardedBlocksState extends PersistentState {

    // Store: position -> block type
    private final Long2ObjectOpenHashMap<Block> warded = new Long2ObjectOpenHashMap<>();

    // ----------------------------
    // Basic Queries
    // ----------------------------

    public boolean isWarded(BlockPos pos, BlockState currentState) {
        Block stored = warded.get(pos.asLong());

        if (stored == null) return false;

        if (stored != currentState.getBlock()) {
            warded.remove(pos.asLong()); // auto-clean
            markDirty();
            return false;
        }

        return true;
    }

    public boolean isWardedRaw(BlockPos pos) {
        return warded.containsKey(pos.asLong());
    }

    // ----------------------------
    // Mutations
    // ----------------------------

    public void setWarded(BlockPos pos, BlockState state, boolean value) {
        long key = pos.asLong();

        if (value) {
            warded.put(key, state.getBlock());
        } else {
            warded.remove(key);
        }

        markDirty();
    }

    public void remove(BlockPos pos) {
        warded.remove(pos.asLong());
        markDirty();
    }

    // ----------------------------
    // Access for Sync / Iteration
    // ----------------------------

    public Set<Long> getAllKeys() {
        return warded.keySet();
    }

    public Collection<BlockPos> getAllPositions() {
        List<BlockPos> list = new ArrayList<>(warded.size());
        for (long l : warded.keySet()) {
            list.add(BlockPos.fromLong(l));
        }
        return list;
    }

    public Block getStoredBlock(long posLong) {
        return warded.get(posLong);
    }

    // ----------------------------
    // Cleanup (VERY IMPORTANT)
    // ----------------------------

    public void validate(ServerWorld world) {

        Iterator<Long2ObjectMap.Entry<Block>> it = warded.long2ObjectEntrySet().iterator();

        List<BlockPos> removed = new ArrayList<>();

        while (it.hasNext()) {
            var entry = it.next();

            long posLong = entry.getLongKey();
            Block stored = entry.getValue();

            BlockPos pos = BlockPos.fromLong(posLong);
            Block current = world.getBlockState(pos).getBlock();

            if (current != stored || current.getDefaultState().isAir()) {
                it.remove();
                removed.add(pos);
                markDirty();
            }
        }

        // 🔥 Sync removals
        if (!removed.isEmpty()) {
            WardingPrismItem.syncBatch(world, removed, false);
        }
    }
    // ----------------------------
    // NBT Saving
    // ----------------------------

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtList list = new NbtList();

        for (Long2ObjectMap.Entry<Block> entry : warded.long2ObjectEntrySet()) {

            NbtCompound tag = new NbtCompound();

            tag.putLong("pos", entry.getLongKey());

            Identifier id = Registries.BLOCK.getId(entry.getValue());
            tag.putString("block", id.toString());

            list.add(tag);
        }

        nbt.put("warded", list);

        return nbt;
    }

    // ----------------------------
    // NBT Loading
    // ----------------------------

    public static WardedBlocksState createFromNbt(NbtCompound nbt) {

        WardedBlocksState state = new WardedBlocksState();

        NbtList list = nbt.getList("warded", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < list.size(); i++) {

            NbtCompound tag = list.getCompound(i);

            long pos = tag.getLong("pos");

            Identifier id = new Identifier(tag.getString("block"));
            Block block = Registries.BLOCK.get(id);

            if (block != null) {
                state.warded.put(pos, block);
            }
        }

        return state;
    }

    // ----------------------------
    // Retrieval from World
    // ----------------------------

    public static WardedBlocksState get(World world) {

        if (world.isClient()) {
            throw new IllegalStateException("Tried to access server state on client");
        }

        ServerWorld serverWorld = (ServerWorld) world;

        return serverWorld.getPersistentStateManager().getOrCreate(
                WardedBlocksState::createFromNbt,
                WardedBlocksState::new,
                "warded_blocks"
        );
    }

    // ----------------------------
    // Networking Helpers
    // ----------------------------

    public static void syncToTracking(ServerWorld world, BlockPos pos, boolean warded) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeLong(pos.asLong());
        buf.writeBoolean(warded);

        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, SongweaverPackets.SYNC_WARDS, new PacketByteBuf(buf.copy()));
        }
    }
}