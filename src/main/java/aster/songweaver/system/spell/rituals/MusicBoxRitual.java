package aster.songweaver.system.spell.rituals;

import aster.songweaver.api.weaving.Ritual;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MusicBoxRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {

        RegistryKey<Biome> biome;

        if (data == null || !data.has("biome") ){
            Optional<RegistryKey<Biome>> probablyBiome = world.getBiome(loom.getPos()).getKey();
            biome = probablyBiome.orElse(BiomeKeys.THE_VOID);
        } else {
            biome = RegistryKey.of(RegistryKeys.BIOME, new Identifier(data.get("biome").getAsString()));
        }




        BlockPos khipuPos = SpellUtil.getKhipuPosOrLoomPosIfAbsent(loom);

        summonMusicBox(world, khipuPos, biome);


    }

    public void summonMusicBox(ServerWorld world, BlockPos pos, RegistryKey<Biome> key) {
        ItemStack stack = new ItemStack(LoomItems.MUSIC_BOX);

        // Write biome data into the item's NBT, not the entity's
        NbtCompound itemNbt = new NbtCompound();
        itemNbt.putString("biome", key.getValue().toString());
        stack.setNbt(itemNbt);

        ItemEntity output = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, stack);

        world.spawnEntity(output);
        world.spawnParticles(
                ParticleTypes.END_ROD,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                6,
                0.4, 0.2, 0.4,
                0.01
        );
    }
}
