package aster.songweaver.registry.physical.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SplittableRandom;

public class MusicBoxItem extends Item {



    public MusicBoxItem(Settings settings) {
        super(settings);
    }
    @Override
    public int getMaxUseTime(ItemStack stack){
        return 72000;
    }
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW; // or BOW if you want animation
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!stack.hasNbt() || !stack.getNbt().contains("biome")) {
            return TypedActionResult.fail(stack);
        }

        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(user instanceof ServerPlayerEntity player)) return;

        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;


        int radius = Math.min(usedTicks / 10, 64);

        Identifier id = new Identifier(stack.getNbt().getString("biome"));
        RegistryKey<Biome> biomeKey = RegistryKey.of(RegistryKeys.BIOME, id);

        setBiomeRegion((ServerWorld) world, player.getBlockPos(), radius, biomeKey);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient) {
            stack.decrement(1);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {

        if (!stack.hasNbt()) return;
        if (!stack.getNbt().contains("biome")) return;

        String biomeIdentifier = stack.getNbt().getString("biome");
        if (biomeIdentifier.isEmpty()) return;

        Identifier id = new Identifier(biomeIdentifier);

        int seed = deterministicInt(biomeIdentifier, 0, 5);

        String affix = switch (seed) {
            case 0 -> "item.songweaver.music_box.affix.0";
            case 1 -> "item.songweaver.music_box.affix.1";
            case 2 -> "item.songweaver.music_box.affix.2";
            case 3 -> "item.songweaver.music_box.affix.3";
            case 4 -> "item.songweaver.music_box.affix.4";
            case 5 -> "item.songweaver.music_box.affix.5";
            default -> "item.songweaver.music_box.affix.error";
        };



        // Build biome translation key
        String biomeKey = "biome." + id.getNamespace() + "." + id.getPath();

        tooltip.add(
                Text.translatable(affix)
                        .append(Text.translatable("item.songweaver.music_box.linker"))
                        .append(Text.translatable(biomeKey))
        );
    }

    public static void setBiomeRegion(ServerWorld world, BlockPos center, int radius, RegistryKey<Biome> biomeKey) {
        Registry<Biome> registry = world.getRegistryManager().get(RegistryKeys.BIOME);
        Optional<RegistryEntry.Reference<Biome>> entryOpt = registry.getEntry(biomeKey);

        if (entryOpt.isEmpty()) return;
        RegistryEntry<Biome> biome = entryOpt.get();

        int minChunkX = (center.getX() - radius) >> 4;
        int maxChunkX = (center.getX() + radius) >> 4;
        int minChunkZ = (center.getZ() - radius) >> 4;
        int maxChunkZ = (center.getZ() + radius) >> 4;

        List<Chunk> chunks = new ArrayList<>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {

                // 🔥 ONLY update edge chunks (huge optimization)
                boolean isEdge =
                        cx == minChunkX || cx == maxChunkX ||
                                cz == minChunkZ || cz == maxChunkZ;

                if (!isEdge) continue;

                Chunk chunk = world.getChunk(cx, cz);
                chunks.add(chunk);
            }
        }

        for (Chunk chunk : chunks) {
            BiomeSupplier supplier = (x, y, z, sampler) -> {
                int bx = BiomeCoords.toBlock(x);
                int by = BiomeCoords.toBlock(y);
                int bz = BiomeCoords.toBlock(z);

                if (Math.abs(bx - center.getX()) <= radius &&
                        Math.abs(bz - center.getZ()) <= radius) {
                    return biome;
                }

                return chunk.getBiomeForNoiseGen(x, y, z);
            };

            chunk.populateBiomes(supplier, world.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
            chunk.setNeedsSaving(true);
        }

        world.getChunkManager().threadedAnvilChunkStorage.sendChunkBiomePackets(chunks);
    }

    public static int deterministicInt(String input, int min, int max) {
        long seed = input.hashCode() * 0x9E3779B97F4A7C15L;

        SplittableRandom random = new SplittableRandom(seed);
        return random.nextInt(min, max + 1);
    }

}
