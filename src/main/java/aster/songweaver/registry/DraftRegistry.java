package aster.songweaver.registry;

import aster.songweaver.system.definition.Draft;
import aster.songweaver.system.spell.potion.DistillDraft;
import aster.songweaver.system.spell.potion.EffectDraft;
import aster.songweaver.system.spell.LodestoneTPDraft;
import aster.songweaver.system.spell.mining.BreakBlockDraft;
import aster.songweaver.system.spell.mining.FortuneBreakDraft;
import aster.songweaver.system.spell.mining.SilkBreakDraft;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DraftRegistry {
    private static final Map<Identifier, Draft> DRAFTS = new HashMap<>();

    public static void init() {


        register(new Identifier("songweaver", "break_block"), new BreakBlockDraft());
        register(new Identifier("songweaver", "silk_break"), new SilkBreakDraft());
        register(new Identifier("songweaver", "fortune_break"), new FortuneBreakDraft());

        register (new Identifier("songweaver", "effect"), new EffectDraft());
        register (new Identifier("songweaver", "lodestone_teleport"), new LodestoneTPDraft());
        register (new Identifier("songweaver", "distill_effects"), new DistillDraft());


    }

    public static Draft get(Identifier id) {
        return DRAFTS.get(id);
    }

    private static void register(Identifier id, Draft draft) {
        DRAFTS.put(id, draft);
    }
}
