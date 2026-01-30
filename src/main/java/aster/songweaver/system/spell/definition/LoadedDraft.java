package aster.songweaver.system.spell.definition;

import net.minecraft.util.Identifier;

public record LoadedDraft(
        Identifier sourceId,   // e.g. songweaver:fireball
        DraftDefinition draft
) {}