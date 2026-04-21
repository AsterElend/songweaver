package aster.songweaver.util;

import aster.songweaver.api.weaving.*;
import aster.songweaver.api.weaving.drawback.DamageDrawback;
import aster.songweaver.api.weaving.drawback.EffectDrawback;
import aster.songweaver.api.weaving.drawback.SilenceDrawback;
import aster.songweaver.api.weaving.requirement.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
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
                                obj.has("count") ? obj.get("count").getAsInt(): 1
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
                case "effect" -> list.add(
                        new EffectDrawback(
                                new Identifier(obj.get("effect").getAsString()),
                                obj.get("ticks").getAsInt(),
                                obj.has("amplifier") ? obj.get("amplifier").getAsInt() : 0
                        )
                );

                case "silence" -> list.add(
                        new SilenceDrawback(
                                obj.get("ticks").getAsInt()
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean shouldLoad(JsonObject json) {
        if (!json.has("modLoaded")) {
            return true;
        }

        var loader = FabricLoader.getInstance();
        var element = json.get("modLoaded");

        if (element.isJsonPrimitive()) {
            return loader.isModLoaded(element.getAsString());
        }

        if (element.isJsonArray()) {
            for (var e : element.getAsJsonArray()) {
                if (!loader.isModLoaded(e.getAsString())) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }

    public static List<ItemStack> parseItems(JsonObject json, String field) {
        if (!json.has(field)) return List.of();

        List<ItemStack> list = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray(field);

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            Identifier itemId = new Identifier(obj.get("item").getAsString());
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

            Item item = Registries.ITEM.get(itemId);

            if (item == Items.AIR) {
                throw new JsonParseException("Unknown item in spell ingredients: " + itemId);
            }


            list.add(new ItemStack(item, count));


        }

        return List.copyOf(list);
    }

    @Nullable
    public static ItemStack parseDisplayItem(JsonObject json) {
        if (!json.has("displayItem")) return null;

        Identifier itemId = new Identifier(json.get("displayItem").getAsString());
        Item item = Registries.ITEM.get(itemId);

        if (item == Items.AIR) {
            throw new JsonParseException("Unknown displayItem: " + itemId);
        }

        return new ItemStack(item);
    }
}
