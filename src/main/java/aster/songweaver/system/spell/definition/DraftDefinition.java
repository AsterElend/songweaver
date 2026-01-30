package aster.songweaver.system.spell.definition;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.List;

public record DraftDefinition(
        PatternKey pattern,
        Identifier draftId,

        //these are nullable:
        List<Requirement> requirements,
        List<Drawback> drawbacks,
        JsonObject data


) {


}
