package aster.songweaver.api.weaving;

import net.minecraft.util.Identifier;

public record LoadedRitual(Identifier sourceId,
                           RitualDefinition ritual) {

}
