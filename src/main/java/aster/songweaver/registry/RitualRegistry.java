package aster.songweaver.registry;

import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.system.spell.rituals.EffectRitual;
import aster.songweaver.system.spell.rituals.MoonPhaseRitual;
import aster.songweaver.system.spell.rituals.SummoningRitual;
import aster.songweaver.system.spell.rituals.TerraformRitual;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RitualRegistry {
    private static final Map<Identifier, Ritual> RITUALS = new HashMap<>();

    public static void init() {
        register(new Identifier("songweaver", "advance_moon"), new MoonPhaseRitual());
        register(new Identifier("songweaver", "summoning"), new SummoningRitual());
        register(new Identifier("songweaver", "effect_ritual"), new EffectRitual());
        register(new Identifier("songweaver", "terraform"), new TerraformRitual());



    }

    public static Ritual get(Identifier id) {
        return RITUALS.get(id);
    }

    private static void register(Identifier id, Ritual ritual) {
        RITUALS.put(id, ritual);
    }
}
