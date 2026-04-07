package aster.songweaver.system.spell.loaders;

import aster.songweaver.Songweaver;
import aster.songweaver.api.weaving.*;
import aster.songweaver.util.JsonParserUtil;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aster.songweaver.util.JsonParserUtil.*;

public class RitualReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID =
            new Identifier("songweaver", "rituals");

    private static final Map<PatternKey, LoadedRitual> RITUALS = new HashMap<>();

    public static Map<PatternKey, LoadedRitual> getRituals() {
        return Map.copyOf(RITUALS);
    }


    @Override
    public Identifier getFabricId() {
        return ID;
    }


    @Override
    public void reload(ResourceManager manager){
        RITUALS.clear();

        Gson gson = new Gson();

        for (Identifier id : manager.findResources(
                "rituals",
                path -> path.getPath().endsWith(".json")
        ).keySet()) {

            try {
                Resource resource = manager.getResource(id).orElseThrow();
                Reader reader = new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                );
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                if (!shouldLoad(json)) {
                    Songweaver.LOGGER.debug(
                            "Skipping ritual {} because required mod is not loaded",
                            id
                    );
                    continue;
                }

                PatternKey pattern = JsonParserUtil.parsePattern(json);
                Identifier ritualId = parseRitual(json);
                List<ItemStack> ingredients = parseItems(json, "ingredients");
                List<Requirement> requirements = parseRequirements(json);
                List<Drawback> drawbacks = parseDrawbacks(json);
                JsonObject data = JsonParserUtil.getData(json);
                int duration = parseTicks(json);
                int tickInterval = parseInterval(json);


                RitualDefinition ritual = new RitualDefinition(
                        pattern, ritualId, ingredients, requirements, drawbacks, data, duration, tickInterval
                );

                if (RITUALS.containsKey(pattern)) {
                    Songweaver.LOGGER.warn(
                            "Duplicate ritual pattern detected for {} (overwriting)",
                            ritualId
                    );
                }

                RITUALS.put(pattern, new LoadedRitual(id, ritual));


            } catch (Exception e){
                Songweaver.LOGGER.error(
                        "Failed to load ritual {}",
                        id,
                        e
                );
            }


        }

        Songweaver.LOGGER.info(
                "Loaded {} Songweaver rituals",
                RITUALS.size()
        );
    }

    public static RitualDefinition matchForRitual(List<Note> notes) {
        LoadedRitual loaded = RITUALS.get(PatternKey.of(notes));
        return loaded != null ? loaded.ritual() : null;
    }



    private static int parseTicks(JsonObject json){
        if (!json.has("duration")) return 1;
        return json.get("duration").getAsInt();
    }

      private static int parseInterval(JsonObject json){
        if (!json.has("interval")) return 1;
        return json.get("interval").getAsInt();
    }

    public static Identifier parseRitual(JsonObject json) {
        return new Identifier(json.get("ritual").getAsString());
    }

}
