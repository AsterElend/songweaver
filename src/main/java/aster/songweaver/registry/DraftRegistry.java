package aster.songweaver.registry;

import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.drafts.*;
import aster.songweaver.system.spell.drafts.DistillDraft;
import aster.songweaver.system.spell.drafts.EffectDraft;
import aster.songweaver.system.spell.drafts.BreakBlockDraft;
import aster.songweaver.system.spell.drafts.EnchantedBreakDraft;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DraftRegistry {
    private static final Map<Identifier, Draft> DRAFTS = new HashMap<>();

    public static void init() {


        register(new Identifier("songweaver", "break_block"), new BreakBlockDraft());
        register(new Identifier("songweaver", "enchant_break"), new EnchantedBreakDraft());
        register (new Identifier("songweaver", "effect"), new EffectDraft());
        register (new Identifier("songweaver", "lodestone_teleport"), new LodestoneTPDraft());
        register (new Identifier("songweaver", "distill_effects"), new DistillDraft());
        register (new Identifier("songweaver", "give"), new GiveItemDraft());
        register (new Identifier("songweaver", "infusion"), new InfusionDraft());
        register (new Identifier("songweaver", "firework"), new FireworkDraft());
        register (new Identifier("songweaver", "pseudo_harvest"), new PseudoHarvestDraft());





    }

    public static Draft get(Identifier id) {
        return DRAFTS.get(id);
    }

    private static void register(Identifier id, Draft draft) {
        DRAFTS.put(id, draft);
    }
}
