package aster.songweaver.api.weaving;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Drawback {
    void apply(ServerPlayerEntity caster);
}
