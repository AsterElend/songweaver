package aster.songweaver.system.definition;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Drawback {
    void apply(ServerPlayerEntity caster);
}
