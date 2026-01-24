package aster.songweaver.system.spell.definition;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.registry.physical.ritual.KhipuHookBlockEntity;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface Ritual {
    void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data);
}
