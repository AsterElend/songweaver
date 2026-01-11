package aster.songweaver.system;

import aster.songweaver.Songweaver;
import aster.songweaver.system.definition.*;
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

import static aster.songweaver.system.JsonParserUtil.parseDrawbacks;
import static aster.songweaver.system.JsonParserUtil.parseRequirements;

public class DraftReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID =
            new Identifier("songweaver", "drafts");

    private static final Map<PatternKey, DraftDefinition> SPELLS = new HashMap<>();

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

                SPELLS.put(pattern, spell);

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
        return SPELLS.get(PatternKey.of(notes));
    }


    public static Identifier parseDraft(JsonObject json) {
        return new Identifier(json.get("draft").getAsString());
    }








}





