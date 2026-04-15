package aster.songweaver.api.weaving;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record RitualDefinition(
        PatternKey pattern,
        Identifier ritualId,
        List<ItemStack> ingredients,
        List<Requirement> requirements,
        List<Drawback> drawbacks,
        JsonObject data,
        int duration,
        int tickInterval,
        @Nullable ItemStack displayItem
) {}
