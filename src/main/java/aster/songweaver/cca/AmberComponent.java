package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AmberComponent implements AutoSyncedComponent {
    private final LivingEntity entity;

    private  BlockPos origin = null;
    private  RegistryKey<World> worldKey;

    public AmberComponent(LivingEntity entity){
        this.entity = entity;
    }

    public void setOrigin(ServerWorld world, BlockPos pos) {
        this.origin = pos;
        this.worldKey = world.getRegistryKey();
        SongweaverComponents.AMBER_STEPS.sync(entity);
    }


    public BlockPos getOrigin() {
        return origin;
    }


    public RegistryKey<World> getWorldKey() {
        if (worldKey == null) return null;
        return worldKey;

    }


    public boolean hasOrigin() {
        return origin != null && worldKey != null;
    }


    public void clear() {
        origin = null;
        worldKey = null;
        SongweaverComponents.AMBER_STEPS.sync(entity);
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        if (!tag.contains("origin")) return;

        NbtCompound posTag = tag.getCompound("origin");

        origin = new BlockPos(
                posTag.getInt("x"),
                posTag.getInt("y"),
                posTag.getInt("z")
        );

        worldKey = RegistryKey.of(
                RegistryKeys.WORLD,
                new Identifier(tag.getString("world"))
        );
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (!hasOrigin()) return; // ← prevents empty data saving

        NbtCompound posTag = new NbtCompound();
        posTag.putInt("x", origin.getX());
        posTag.putInt("y", origin.getY());
        posTag.putInt("z", origin.getZ());

        tag.put("origin", posTag);
        tag.putString("world", worldKey.getValue().toString());
    }
}
