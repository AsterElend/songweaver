package aster.songweaver.emi;

import aster.songweaver.api.SongweaverTranslations;
import aster.songweaver.api.weaving.Note;
import aster.songweaver.api.weaving.RitualDefinition;
import com.google.gson.JsonObject;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class RitualEmiRecipe implements EmiRecipe {

    private final Identifier fileId;
    private final RitualDefinition ritual;
    private final List<Note> notes;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    private static final int PADDING = 4;
    private static final int INGREDIENT_ROW_H = 20;

    public RitualEmiRecipe(Identifier fileId, RitualDefinition ritual) {
        this.fileId = fileId;
        this.ritual = ritual;
        this.notes = ritual.pattern().notes(); // adjust if PatternKey exposes them differently
        this.inputs = ritual.ingredients().stream()
                .map(stack -> (EmiIngredient) EmiStack.of(stack))
                .toList();
        this.outputs = parseOutputs(ritual);
    }

    private static List<EmiStack> parseOutputs(RitualDefinition ritual) {
        JsonObject data = ritual.data();
        if (data == null) return List.of();
        if (data.has("item")) {
            Identifier itemId = new Identifier(data.get("item").getAsString());
            Item item = Registries.ITEM.get(itemId);
            if (item != Items.AIR) {
                int count = data.has("count") ? data.get("count").getAsInt() : 1;
                return List.of(EmiStack.of(item, count));
            }
        }
        return List.of();
    }

    @Override public EmiRecipeCategory getCategory() { return SongweaverEmiPlugin.RITUAL_CATEGORY; }
    @Override public Identifier getId() { return new Identifier(fileId.getNamespace(), "/ritual/" + fileId.getPath()); }
    @Override public List<EmiIngredient> getInputs() { return inputs; }
    @Override public List<EmiStack> getOutputs() { return outputs; }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return SongweaverEmiUtil.noteStripHeight(notes.size())
                + PADDING          // gap between notes and name
                + 10               // name text height
                + PADDING
                + INGREDIENT_ROW_H // ingredient + output row
                + PADDING;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int y = 0;

        // Note strip
        y = SongweaverEmiUtil.addNoteWidgets(widgets, notes, 0, y);
        y += PADDING;

        // Name
        widgets.addText(
                Text.translatable(SongweaverTranslations.ritualKey(fileId)),
                0, y, 0x404040, false
        );
        y += 10 + PADDING;

        // Display item icon
        int startX = 0;
        if (ritual.displayItem() != null) {
            widgets.addSlot(EmiStack.of(ritual.displayItem()), startX, y);
            startX += 22;
        }

        // Ingredient slots
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), startX + (i * 20), y);
        }

        // Arrow + output
        int arrowX = startX + Math.max(inputs.size() * 20, 4) + 4;
        widgets.addTexture(EmiTexture.EMPTY_ARROW, arrowX, y + 1);
        int outputX = arrowX + 24;

        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.get(0), outputX, y).recipeContext(this);
        } else {
            widgets.addText(
                    Text.translatable(SongweaverTranslations.ritualKey(fileId))
                            .formatted(Formatting.GRAY),
                    outputX, y + 5, 0x888888, false
            );
        }
    }
}