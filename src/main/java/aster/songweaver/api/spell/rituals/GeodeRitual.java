package aster.songweaver.api.spell.rituals;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.registry.world.ArbitraryGeodeFeatureConfig;
import aster.songweaver.api.SongweaverPackets;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class GeodeRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
        Identifier innerBlock = new Identifier(data.get("inner").getAsString());
        Identifier buddingBlock = new Identifier(data.get("budding").getAsString());
        BlockState innerState = Registries.BLOCK.get(innerBlock).getDefaultState();
        BlockState buddingState = Registries.BLOCK.get(buddingBlock).getDefaultState();

        if (innerState.isAir() || buddingState.isAir()){
            SongweaverPackets.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        ConfiguredFeature<ArbitraryGeodeFeatureConfig, ?> configurement =
                new ConfiguredFeature<>(
                        LoomMiscRegistry.ARBITRARY_GEODE,
                        new ArbitraryGeodeFeatureConfig(
                                innerState,
                                buddingState
                        )
                );

        configurement.generate(world, world.getChunkManager().getChunkGenerator(), world.getRandom(), loom.getPos().up(3));

    }


}
