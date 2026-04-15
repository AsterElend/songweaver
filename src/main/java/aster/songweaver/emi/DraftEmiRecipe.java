package aster.songweaver.emi;

import aster.songweaver.api.SongweaverTranslations;
import aster.songweaver.api.weaving.DraftDefinition;
import aster.songweaver.api.weaving.Note;
import com.google.gson.JsonObject;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;public class DraftEmiRecipe implements EmiRecipe {

    private final Identifier fileId;
    private final DraftDefinition draft;
    private final List<Note> notes;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    @Nullable private final Item infusionInput;
    @Nullable private final Item infusionOutput;

    private static final int PADDING = 4;
    private static final int INGREDIENT_ROW_H = 20;

    public DraftEmiRecipe(Identifier fileId, DraftDefinition draft) {
        this.fileId = fileId;
        this.draft = draft;
        this.notes = draft.pattern().notes(); // adjust if needed

        this.inputs = draft.components().stream()
                .map(stack -> (EmiIngredient) EmiStack.of(stack))
                .toList();

        Item parsedInfusionInput = null;
        Item parsedInfusionOutput = null;
        List<EmiStack> parsedOutputs = new ArrayList<>();

        JsonObject data = draft.data();
        if (data != null) {
            if (data.has("item")) {
                Identifier itemId = new Identifier(data.get("item").getAsString());
                Item item = Registries.ITEM.get(itemId);
                if (item != Items.AIR) {
                    int count = data.has("count") ? data.get("count").getAsInt() : 1;
                    parsedOutputs.add(EmiStack.of(item, count));
                }
            } else if (data.has("input") && data.has("output")) {
                Block inputBlock = Registries.BLOCK.get(
                        new Identifier(data.get("input").getAsString()));
                Block outputBlock = Registries.BLOCK.get(
                        new Identifier(data.get("output").getAsString()));
                parsedInfusionInput = inputBlock.asItem();
                parsedInfusionOutput = outputBlock.asItem();
                if (parsedInfusionOutput != Items.AIR) {
                    parsedOutputs.add(EmiStack.of(parsedInfusionOutput));
                }
            }
        }

        this.outputs = List.copyOf(parsedOutputs);
        this.infusionInput = parsedInfusionInput;
        this.infusionOutput = parsedInfusionOutput;
    }

    @Override public EmiRecipeCategory getCategory() { return SongweaverEmiPlugin.DRAFT_CATEGORY; }
    @Override public Identifier getId() { return new Identifier(fileId.getNamespace(), "/draft/" + fileId.getPath()); }

    @Override
    public List<EmiIngredient> getInputs() {
        if (infusionInput != null && infusionInput != Items.AIR) {
            List<EmiIngredient> all = new ArrayList<>(inputs);
            all.add(EmiStack.of(infusionInput));
            return List.copyOf(all);
        }
        return inputs;
    }

    @Override public List<EmiStack> getOutputs() { return outputs; }
    @Override public int getDisplayWidth() { return 160; }

    @Override
    public int getDisplayHeight() {
        // Infusion needs an extra row for the block swap
        int extraRows = (infusionInput != null) ? 1 : 0;
        return SongweaverEmiUtil.noteStripHeight(notes.size())
                + PADDING
                + 10               // name
                + PADDING
                + INGREDIENT_ROW_H // components + output
                + (extraRows * (PADDING + INGREDIENT_ROW_H)) // infusion block row
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
                Text.translatable(SongweaverTranslations.draftKey(fileId)),
                0, y, 0x404040, false
        );
        y += 10 + PADDING;

        int startX = 0;
        if (draft.displayItem() != null) {
            widgets.addSlot(EmiStack.of(draft.displayItem()), startX, y);
            startX += 22;
        }

        if (infusionInput != null && infusionInput != Items.AIR) {
            // Components row
            for (int i = 0; i < inputs.size(); i++) {
                widgets.addSlot(inputs.get(i), startX + (i * 20), y);
            }
            y += INGREDIENT_ROW_H + PADDING;

            // Infusion block swap row
            widgets.addText(
                    Text.translatable("songweaver.emi.infuses").formatted(Formatting.GRAY),
                    startX, y, 0x555555, false
            );
            y += 10;
            widgets.addSlot(EmiStack.of(infusionInput), startX, y);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, startX + 22, y + 1);
            if (!outputs.isEmpty()) {
                widgets.addSlot(outputs.get(0), startX + 48, y).recipeContext(this);
            }

        } else {
            // Standard components + output row
            for (int i = 0; i < inputs.size(); i++) {
                widgets.addSlot(inputs.get(i), startX + (i * 20), y);
            }
            int arrowX = startX + Math.max(inputs.size() * 20, 4) + 4;
            widgets.addTexture(EmiTexture.EMPTY_ARROW, arrowX, y + 1);
            int outputX = arrowX + 24;

            if (!outputs.isEmpty()) {
                widgets.addSlot(outputs.get(0), outputX, y).recipeContext(this);
            } else {
                widgets.addText(
                        Text.translatable(SongweaverTranslations.draftKey(fileId))
                                .formatted(Formatting.GRAY),
                        outputX, y + 5, 0x888888, false
                );
            }
        }
    }
}