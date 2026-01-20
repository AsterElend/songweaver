package aster.songweaver.system.spell.definition;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Drawback {
    void apply(ServerPlayerEntity caster);
}
