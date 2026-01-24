package aster.songweaver.system.spell.loaders;

import aster.songweaver.Songweaver;
import aster.songweaver.system.spell.definition.*;
import aster.songweaver.util.JsonParserUtil;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aster.songweaver.util.JsonParserUtil.*;

public class RitualReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID =
            new Identifier("songweaver", "rituals");

    private static final Map<PatternKey, RitualDefinition> RITUALS = new HashMap<>();

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

                PatternKey pattern = JsonParserUtil.parsePattern(json);
                Identifier ritualId = parseRitual(json);
                List<ItemStack> ingredients = parseIngredients(json);
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

                RITUALS.put(pattern, ritual);


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
        return RITUALS.get(PatternKey.of(notes));
    }

    private static List<ItemStack> parseIngredients(JsonObject json) {
        if (!json.has("ingredients")) return List.of();

        List<ItemStack> list = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray("ingredients");

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            Identifier itemId = new Identifier(obj.get("item").getAsString());
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

            Item item = Registries.ITEM.get(itemId);

            if (item == Items.AIR) {
                throw new JsonParseException("Unknown item in ritual ingredients: " + itemId);
            }


            list.add(new ItemStack(item, count));


        }

        return List.copyOf(list);
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
