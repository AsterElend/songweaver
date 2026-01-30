package aster.songweaver.system.spell.ambi;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.definition.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class FireworkMagic implements Draft, Ritual {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        spawnRandomFirework(world, twoBlocksAboveHead(caster));

    }

    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity loom, @Nullable JsonObject data){
        BlockPos pos = loom.getPos();

        Vec3d vec =  new Vec3d(
                pos.getX(),
                pos.getY() + 2,
                pos.getZ()
        );
        spawnRandomFirework(world, vec);
    }


    public static Vec3d twoBlocksAboveHead(ServerPlayerEntity player) {
        return new Vec3d(
                player.getX(),
                player.getEyeY() + 2,
                player.getZ()
        );
    }

    public static void spawnRandomFirework(ServerWorld world, Vec3d pos) {
        Random random = world.getRandom();

        // Random RGB color
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        int color = (red << 16) | (green << 8) | blue;

        // Random flight time (1–3 is vanilla-like)
        int flightTime = 1 + random.nextInt(3);
        int shape = random.nextInt(4);

        // Firework rocket item
        ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);

        // Root Fireworks NBT
        NbtCompound fireworksNbt = new NbtCompound();
        fireworksNbt.putByte("Flight", (byte) flightTime);

        // Explosion NBT
        NbtCompound explosionNbt = new NbtCompound();
        explosionNbt.putIntArray("Colors", new int[]{color});
        explosionNbt.putByte("Type", (byte) shape); // 0=small, 1=large, 2=star, 3=creeper, 4=burst

        NbtList explosions = new NbtList();
        explosions.add(explosionNbt);

        fireworksNbt.put("Explosions", explosions);

        // Attach Fireworks NBT to item
        NbtCompound rootNbt = new NbtCompound();
        rootNbt.put("Fireworks", fireworksNbt);
        firework.setNbt(rootNbt);

        // Spawn entity
        FireworkRocketEntity rocket = new FireworkRocketEntity(
                world,
                pos.x,
                pos.y,
                pos.z,
                firework
        );

        world.spawnEntity(rocket);
    }


}
