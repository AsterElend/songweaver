package aster.songweaver.api.weaving;

import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface Requirement {
    CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual);
}
