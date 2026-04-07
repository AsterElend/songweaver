package aster.songweaver.api.weaving;

import net.minecraft.util.Identifier;

public record LoadedDraft(
        Identifier sourceId,   // e.g. songweaver:fireball
        DraftDefinition draft
) {}