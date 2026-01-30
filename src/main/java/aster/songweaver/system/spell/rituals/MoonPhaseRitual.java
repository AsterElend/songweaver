package aster.songweaver.system.spell.rituals;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static aster.songweaver.system.cast.SongServerCasting.sendFeedback;

public class MoonPhaseRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject ritualData) {
        //ignore the khipu; irrelevant
        if (!world.getDimension().hasSkyLight() || world.getDimension().ultrawarm()) {
            sendFeedback(caster, CastFeedback.WRONG_DIMENSION);

        }  if (ritualData != null && ritualData.has("phases")) {
            if (Objects.equals(ritualData.get("phases").getAsString(), "onyx")) {
                advanceToNewMoon(world, caster);
            } else {

                advanceMoonPhase(world, ritualData.get("phases").getAsInt());
            }

        } else {advanceMoonPhase(world, 1);}

    }

    public static void advanceMoonPhase(ServerWorld world, int steps) {
        long currentTime = world.getTimeOfDay();
        int currentPhase = world.getMoonPhase();   // 0-7
        int nextPhase = (currentPhase + steps) % 8;

        // Compute the current day at midnight
        long currentDay = currentTime / 24000L;

        // Calculate how many days we need to reach the desired phase
        int deltaDays = nextPhase - currentPhase;
        if (deltaDays < 0) deltaDays += 8;  // wrap-around

        long newTime = currentTime + deltaDays * 24000L;

        world.setTimeOfDay(newTime);
    }


    public static void advanceToNewMoon(ServerWorld world, ServerPlayerEntity caster) {
        long currentTime = world.getTimeOfDay();


        int stepsNeeded = stepsToNextNewMoon(world);
        long currentDay = currentTime / 24000L;
        long newMoonDay = currentDay + stepsNeeded;
        long newMoonMidnight = newMoonDay * 24000L;

        // Preflight: would ritual execution pass midnight?
        if (currentTime + (stepsNeeded * 24000L) > newMoonMidnight) {
            SongServerCasting.sendFeedback(caster, CastFeedback.SHOOT_THE_MOON);
        }

        // Safe to advance
        world.setTimeOfDay(currentTime + stepsNeeded * 24000L);
    }

    private static int stepsToNextNewMoon(ServerWorld world) {
        int phase = world.getMoonPhase();
        int diff = 4 - phase;
        return diff <= 0 ? diff + 8 : diff;
    }



}
