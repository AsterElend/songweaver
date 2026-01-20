package aster.songweaver.system;

import aster.songweaver.system.spell.definition.*;
import aster.songweaver.system.spell.drawback.ConsumeItemDrawback;
import aster.songweaver.system.spell.drawback.DamageDrawback;
import aster.songweaver.system.spell.drawback.EffectDrawback;
import aster.songweaver.system.spell.requirement.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.List;

public class JsonParserUtil {

    public static PatternKey parsePattern(JsonObject json) {
        JsonArray arr = json.getAsJsonArray("pattern");

        List<Note> notes = new ArrayList<>();
        for (JsonElement el : arr) {
            notes.add(Note.valueOf(el.getAsString()));
        }

        return PatternKey.of(notes);
    }



    public static List<Requirement> parseRequirements(JsonObject json) {
        if (!json.has("requirements")) return List.of();

        List<Requirement> list = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray("requirements");

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String type = obj.get("type").getAsString();

            switch (type) {
                case "advancement" -> list.add(
                        new AdvancementRequirement(
                                new Identifier(obj.get("id").getAsString())
                        )
                );
                case "distaff_tier" -> list.add(
                        new TierRequirement(
                                Tier.valueOf(obj.get("tier").getAsString())
                        )
                );
                case "item" -> list.add(
                        new ItemRequirement(
                                new Identifier(obj.get("item").getAsString()),
                                obj.get("count").getAsInt()
                        )
                );
                case "dimension" -> list.add(
                        new DimensionRequirement(
                                (obj.get("dimension").getAsString())

                        )
                );
                case "structure" -> list.add(
                        new StructureRequirement(
                                PatchouliAPI.get().getMultiblock(new Identifier(obj.get("structure").getAsString()))
                        )
                );

                default -> throw new IllegalArgumentException(
                        "Unknown requirement type: " + type
                );
            }
        }

        return list;
    }
    public static List<Drawback> parseDrawbacks(JsonObject json) {
        if (!json.has("drawbacks")) return List.of();

        List<Drawback> list = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray("drawbacks");

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String type = obj.get("type").getAsString();

            switch (type) {
                case "damage" -> list.add(
                        new DamageDrawback(
                                obj.get("percent").getAsFloat()
                        )
                );
                case "consume_item" -> list.add(
                        new ConsumeItemDrawback(
                                new Identifier(obj.get("item").getAsString()),
                                obj.get("count").getAsInt()
                        )
                );
                case "effect" -> list.add(
                        new EffectDrawback(
                                new Identifier(obj.get("effect").getAsString()),
                                obj.get("ticks").getAsInt(),
                                obj.has("amplifier") ? obj.get("amplifier").getAsInt() : 0
                        )
                );
                default -> throw new IllegalArgumentException(
                        "Unknown drawback type: " + type
                );
            }
        }

        return list;
    }


    @Nullable
    public static JsonObject getData(JsonObject json){
        if (!json.has("data")) return null;

        return json.getAsJsonObject("data");

    }
}
