package aster.songweaver.registry;

import aster.songweaver.system.spell.ambi.EffectMagic;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.system.spell.drafts.*;
import aster.songweaver.system.spell.rituals.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class MagicRegistry {
    private static final Map<Identifier, Ritual> RITUALS = new HashMap<>();
    private static final Map<Identifier, Draft> DRAFTS = new HashMap<>();

    public static void init() {
        registerRitual(new Identifier("songweaver", "advance_moon"), new MoonPhaseRitual());
        registerRitual(new Identifier("songweaver", "summoning"), new SummoningRitual());
        registerRitual(new Identifier("songweaver", "crafting"), new CraftingRitual());
        registerRitual(new Identifier("songweaver", "terraform"), new TerraformRitual());



        registerDraft(new Identifier("songweaver", "break_block"), new BreakBlockDraft());
        registerDraft(new Identifier("songweaver", "enchant_break"), new EnchantedBreakDraft());
        registerDraft (new Identifier("songweaver", "lodestone_teleport"), new LodestoneTPDraft());
        registerDraft (new Identifier("songweaver", "distill_effects"), new DistillDraft());
        registerDraft (new Identifier("songweaver", "give"), new GiveItemDraft());
        registerDraft (new Identifier("songweaver", "infusion"), new InfusionDraft());

        registerBoth (new Identifier("songweaver", "pseudo_harvest"), new PseudoHarvestMagic());


        registerBoth(new Identifier("songweaver", "effect"), new EffectMagic());
        registerBoth(new Identifier("songweaver", "firework"), new FireworkMagic());
        registerBoth(new Identifier("songweaver", "flag"), new FlagMagic());



    }

    public static Ritual getRitual(Identifier id) {
        return RITUALS.get(id);
    }
    public static Draft getDraft(Identifier id) {
        return DRAFTS.get(id);
    }

    private static void registerRitual(Identifier id, Ritual ritual) {
        RITUALS.put(id, ritual);
    }
    private static void registerDraft(Identifier id, Draft draft) {
        DRAFTS.put(id, draft);
    }
    private static <T extends Ritual & Draft> void registerBoth(Identifier id, T value) {
        RITUALS.put(id, value);
        DRAFTS.put(id, value);
    }
}
