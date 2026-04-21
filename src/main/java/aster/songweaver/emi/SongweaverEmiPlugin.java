package aster.songweaver.emi;

import aster.songweaver.Songweaver;
import aster.songweaver.api.weaving.LoadedDraft;
import aster.songweaver.api.weaving.LoadedRitual;
import aster.songweaver.api.weaving.PatternKey;
import aster.songweaver.api.weaving.loaders.DraftReloadListener;
import aster.songweaver.api.weaving.loaders.RitualReloadListener;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.util.Identifier;

import java.util.Map;

@EmiEntrypoint
public class SongweaverEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory RITUAL_CATEGORY = new EmiRecipeCategory(
            new Identifier("songweaver", "emi_ritual"),
            EmiStack.of(LoomBlockStuff.GRAND_LOOM.asItem())
    );

    public static final EmiRecipeCategory DRAFT_CATEGORY = new EmiRecipeCategory(
            new Identifier("songweaver", "emi_draft"),
            EmiStack.of(LoomItems.DISTAFF_BASIC)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(RITUAL_CATEGORY);
        registry.addCategory(DRAFT_CATEGORY);

        registry.addWorkstation(RITUAL_CATEGORY, EmiStack.of(LoomBlockStuff.GRAND_LOOM.asItem()));
        registry.addWorkstation(DRAFT_CATEGORY, EmiStack.of(LoomItems.DISTAFF_BASIC));

        for (Map.Entry<PatternKey, LoadedRitual> entry :
                RitualReloadListener.getRituals().entrySet()) {
            try {
                registry.addRecipe(new RitualEmiRecipe(
                        entry.getValue().id(),
                        entry.getValue().ritual()
                ));
            } catch (Exception e) {
                Songweaver.LOGGER.warn("Failed to register EMI ritual recipe for {}",
                        entry.getValue().id(), e);
            }
        }

        for (Map.Entry<PatternKey, LoadedDraft> entry :
                DraftReloadListener.getDrafts().entrySet()) {
            try {
                registry.addRecipe(new DraftEmiRecipe(
                        entry.getValue().id(),
                        entry.getValue().draft()
                ));
            } catch (Exception e) {
                Songweaver.LOGGER.warn("Failed to register EMI draft recipe for {}",
                        entry.getValue().id(), e);
            }
        }
    }
}
