package aster.songweaver.registry.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

// Record stays as-is:
public record ArbitraryGeodeFeatureConfig(
        BlockState innerBlock,
        BlockState buddingBlock,
        BlockState outerShellBlock,
        BlockState innerShellBlock
) implements FeatureConfig {

    public static final Codec<ArbitraryGeodeFeatureConfig> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BlockState.CODEC.fieldOf("inner_block")
                            .forGetter(ArbitraryGeodeFeatureConfig::innerBlock),
                    BlockState.CODEC.fieldOf("budding_block")
                            .forGetter(ArbitraryGeodeFeatureConfig::buddingBlock),
                    BlockState.CODEC.fieldOf("outer_shell")
                            .forGetter(ArbitraryGeodeFeatureConfig::outerShellBlock),
                    BlockState.CODEC.fieldOf("inner_shell")
                            .forGetter(ArbitraryGeodeFeatureConfig::innerShellBlock)
            ).apply(instance, ArbitraryGeodeFeatureConfig::new));
}