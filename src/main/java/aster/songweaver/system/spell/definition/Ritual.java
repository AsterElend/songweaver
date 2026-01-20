package aster.songweaver.system.spell.definition;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface Ritual {
    void ritualCast(ServerWorld world, ServerPlayerEntity caster, RitualControllerBlockEntity loom, @Nullable JsonObject data);
}
