package aster.songweaver.api.weaving;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public record DraftDefinition(
        PatternKey pattern,
        Identifier draftId,

        //these are nullable:
        List<ItemStack> components,
        List<Requirement> requirements,
        List<Drawback> drawbacks,
        JsonObject data


) {


}
