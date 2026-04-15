package aster.songweaver.api.weaving;

import net.minecraft.util.Identifier;

public record LoadedDraft(
        Identifier id,   // e.g. songweaver:fireball
        DraftDefinition draft
) {



}