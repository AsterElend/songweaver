package aster.songweaver.api.weaving;

import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface Ritual {
    void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data);
}
