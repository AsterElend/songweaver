package aster.songweaver.api.weaving.rituals;

import aster.songweaver.api.cast.SongweaverPackets;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Ritual;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.registry.world.ArbitraryGeodeFeatureConfig;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class GeodeRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, @Nullable ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
        if (data == null){
            if (caster != null){
                SongweaverPackets.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            }

            return;
        }

        Identifier innerBlock = new Identifier(data.get("innerBlock").getAsString());
        Identifier buddingBlock = new Identifier(data.get("buddingBlock").getAsString());
        Identifier outerShellBlock = new Identifier(data.get("outerShellBlock").getAsString());
        Identifier innerShellBlock = new Identifier(data.get("innerShellBlock").getAsString());
        BlockState innerState = Registries.BLOCK.get(innerBlock).getDefaultState();
        BlockState buddingState = Registries.BLOCK.get(buddingBlock).getDefaultState();
        BlockState outerShellState = Registries.BLOCK.get(outerShellBlock).getDefaultState();
        BlockState innerShellState = Registries.BLOCK.get(innerShellBlock).getDefaultState();

        if (innerState.isAir() || buddingState.isAir()){
            SongweaverPackets.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        ConfiguredFeature<ArbitraryGeodeFeatureConfig, ?> configurement =
                new ConfiguredFeature<>(
                        LoomMiscRegistry.ARBITRARY_GEODE,
                        new ArbitraryGeodeFeatureConfig(
                                innerState,
                                buddingState,
                                outerShellState,
                                innerShellState
                        )
                );
        BlockPos target = loom.getPos().up(16);
        configurement.generate(world, world.getChunkManager().getChunkGenerator(), world.getRandom(), target);
        System.out.println("[GeodeRitual] Generating at " + target);
    }


}
