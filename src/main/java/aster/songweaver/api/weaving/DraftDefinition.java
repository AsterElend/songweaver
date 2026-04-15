package aster.songweaver.api.weaving;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public record DraftDefinition(
        PatternKey pattern,
        Identifier draftId,
        List<ItemStack> components,
        List<Requirement> requirements,
        List<Drawback> drawbacks,
        JsonObject data,
        @Nullable ItemStack displayItem
) {}
