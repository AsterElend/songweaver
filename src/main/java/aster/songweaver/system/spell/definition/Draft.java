package aster.songweaver.system.spell.definition;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface Draft {
    void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data);
}
