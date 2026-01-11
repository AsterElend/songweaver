package aster.songweaver.system.definition;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Requirement {
    CastFailure check(ServerPlayerEntity caster);
}
