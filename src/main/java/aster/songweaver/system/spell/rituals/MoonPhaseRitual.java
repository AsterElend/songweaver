package aster.songweaver.system.spell.rituals;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import static aster.songweaver.system.cast.SongServerCasting.sendFeedback;

public class MoonPhaseRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
//ignore the khipu; irrelevant
        if (!world.getDimension().hasSkyLight() || world.getDimension().ultrawarm()) {
            sendFeedback(caster, CastFeedback.WRONG_DIMENSION);

        } else {
            advanceMoonPhase(world, 1);
        }

    }
    public static void advanceMoonPhase(ServerWorld world, int steps) {
        long worldTime = world.getTimeOfDay();
        long day = worldTime / 24000L;
        int currentPhase = (int)(day % 8);
        int nextPhase = (currentPhase + steps) % 8;
        long newDay = day - currentPhase + nextPhase;
        long newTime = newDay * 24000L + (worldTime % 24000L);
        world.setTimeOfDay(newTime);
    }
}
