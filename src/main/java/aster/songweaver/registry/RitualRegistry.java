package aster.songweaver.registry;

import aster.songweaver.system.definition.Ritual;
import aster.songweaver.system.spell.rituals.MoonPhaseRitual;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RitualRegistry {
    private static final Map<Identifier, Ritual> RITUALS = new HashMap<>();

    public static void init() {
        register(new Identifier("songweaver", "advance_moon"), new MoonPhaseRitual());



    }

    public static Ritual get(Identifier id) {
        return RITUALS.get(id);
    }

    private static void register(Identifier id, Ritual ritual) {
        RITUALS.put(id, ritual);
    }
}
