package aster.songweaver;

import aster.songweaver.system.spell.definition.*;
import aster.songweaver.system.spell.loaders.DraftReloadListener;

import aster.songweaver.system.spell.loaders.RitualReloadListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;


public class SongweaverCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("songweaver")
                        .requires((ServerCommandSource src) -> src.hasPermissionLevel(0))
                        .then(literal("dump")
                                .then(literal("drafts")
                                        .executes(ctx -> dumpDrafts(ctx.getSource()))
                                )
                                .then(literal("rituals")
                                        .executes(ctx -> dumpRituals(ctx.getSource()))
                                )
                        )
        );
    }


    private static int dumpDrafts(ServerCommandSource source) {
        Map<PatternKey, LoadedDraft> drafts = DraftReloadListener.getDrafts();

        if (drafts.isEmpty()) {
            source.sendFeedback(
                    () -> Text.literal("§7No Songweaver drafts are currently loaded."),
                    false
            );
            return 0;
        }

        source.sendFeedback(
                () -> Text.literal("§6Loaded Songweaver Drafts (" + drafts.size() + "):"),
                false
        );

        drafts.forEach((pattern, loaded) -> {
            Identifier jsonId = loaded.sourceId();
            String patternText = formatPattern(pattern);
            String trimJson = jsonId.getPath().replace("drafts/", "").replace(".json", "");


            source.sendFeedback(
                    () -> Text.literal(" §e• §b" + trimJson)
                            .append(Text.literal(" §7→ §a[" + patternText + "]")),
                    false
            );
        });

        return drafts.size();
    }

    private static int dumpRituals(ServerCommandSource source) {
        Map<PatternKey, LoadedRitual> rituals = RitualReloadListener.getRituals();

        if (rituals.isEmpty()) {
            source.sendFeedback(
                    () -> Text.literal("§7No Songweaver rituals are currently loaded."),
                    false
            );
            return 0;
        }

        source.sendFeedback(
                () -> Text.literal("§6Loaded Songweaver Rituals (" + rituals.size() + "):"),
                false
        );

        rituals.forEach((pattern, loaded) -> {
            Identifier jsonId = loaded.sourceId();
            String patternText = formatPattern(pattern);
            String trimJson = jsonId.getPath().replace("rituals/", "").replace(".json", "");

            source.sendFeedback(
                    () -> Text.literal(" §e• §b" + trimJson)
                            .append(Text.literal(" §7→ §a[" + patternText + "]")),
                    false
            );
        });

        return rituals.size();
    }



    private static String formatPattern(PatternKey pattern) {
        return pattern.notes().stream()
                .map(Note::display)
                .reduce((a, b) -> a + " " + b)
                .orElse("(empty)");
    }
}
