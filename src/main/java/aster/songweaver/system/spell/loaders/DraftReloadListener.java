package aster.songweaver.system.spell.loaders;

import aster.songweaver.Songweaver;
import aster.songweaver.system.spell.definition.*;
import aster.songweaver.util.JsonParserUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
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

public class DraftReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID =
            new Identifier("songweaver", "drafts");

    private static final Map<PatternKey, LoadedDraft> SPELLS = new HashMap<>();

    public static Map<PatternKey, LoadedDraft> getDrafts() {
        return Map.copyOf(SPELLS);
    }


    @Override
    public Identifier getFabricId() {
        return ID;
    }


    @Override
    public void reload(
          ResourceManager manager
    ) {
        SPELLS.clear();

        Gson gson = new Gson();

        for (Identifier id : manager.findResources(
                "drafts",
                path -> path.getPath().endsWith(".json")
        ).keySet()) {

            try {
                Resource resource = manager.getResource(id).orElseThrow();
                Reader reader = new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                );

                JsonObject json = gson.fromJson(reader, JsonObject.class);



                if (json == null) {
                    Songweaver.LOGGER.warn("Draft {} returned null JSON, skipping", id);
                    continue;
                }


                if (!shouldLoad(json)) {
                    Songweaver.LOGGER.debug(
                            "Skipping draft {} because required mod is not loaded",
                            id
                    );
                    continue;
                }

                PatternKey pattern = JsonParserUtil.parsePattern(json);
                Identifier draft = parseDraft(json);
                List<Requirement> requirements = parseRequirements(json);
                List<Drawback> drawbacks = parseDrawbacks(json);
                JsonObject data = JsonParserUtil.getData(json);


                DraftDefinition spell = new DraftDefinition(
                        pattern,
                        draft,
                        requirements,
                        drawbacks,
                        data

                );

                if (SPELLS.containsKey(pattern)) {
                    Songweaver.LOGGER.warn(
                            "Duplicate ritual pattern detected for {} (overwriting)",
                            draft
                    );
                }


                SPELLS.put(pattern, new LoadedDraft(id, spell));

            } catch (Exception e) {
                Songweaver.LOGGER.error(
                        "Failed to load spell {}",
                        id,
                        e
                );
            }
        }

        Songweaver.LOGGER.info(
                "Loaded {} Songweaver drafts",
                SPELLS.size()
        );
    }

    public static DraftDefinition matchForDraft(List<Note> notes) {
        LoadedDraft loaded = SPELLS.get(PatternKey.of(notes));
        return loaded != null ? loaded.draft() : null;
    }



    public static Identifier parseDraft(JsonObject json) {
        return new Identifier(json.get("draft").getAsString());
    }








}





