package aster.songweaver.system.spell.definition;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface Requirement {
    CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual);
}
