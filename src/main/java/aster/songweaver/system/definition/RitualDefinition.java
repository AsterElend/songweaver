package aster.songweaver.system.definition;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public record RitualDefinition(
        PatternKey pattern,
        Identifier ritualId,
        //these can be null
        List<ItemStack> ingredients,
        List<Requirement> requirements,
        List<Drawback> drawbacks,
        JsonObject data,

        int duration,
        int tickInterval

) {
}
