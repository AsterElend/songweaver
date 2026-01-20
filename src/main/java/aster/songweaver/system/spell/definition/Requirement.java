package aster.songweaver.system.spell.definition;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface Requirement {
    CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller);
}
