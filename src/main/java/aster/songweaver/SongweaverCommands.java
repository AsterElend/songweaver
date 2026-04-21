package aster.songweaver;

import aster.songweaver.api.weaving.LoadedDraft;
import aster.songweaver.api.weaving.LoadedRitual;
import aster.songweaver.api.weaving.Note;
import aster.songweaver.api.weaving.PatternKey;
import aster.songweaver.api.weaving.loaders.DraftReloadListener;
import aster.songweaver.api.weaving.loaders.RitualReloadListener;
import aster.songweaver.cca.SongweaverComponents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
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
        dispatcher.register(
                literal("songweaver")
                        .requires((ServerCommandSource sec) -> sec.hasPermissionLevel(1))
                        .then(literal("silence")
                                .then(argument("target", EntityArgumentType.entity())
                                        .then(literal("set").then(argument("ticks", IntegerArgumentType.integer()).executes(ctx -> {
                                           Entity target = EntityArgumentType.getEntity(ctx, "target");
                                           int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                                            SongweaverComponents.SILENCE.get(target).setSilence(ticks);
                                            return 1;
                                        })).then(literal("get").executes(ctx -> {
                                                    Entity target = EntityArgumentType.getEntity(ctx, "target");

                                                    ctx.getSource().sendFeedback(() -> Text.literal("Silence Ticks: " + SongweaverComponents.SILENCE.get(target).getSilenceDuration()), false);
                                                    return 1;
                                                }))

                        )
        )));

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
            Identifier jsonId = loaded.id();
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
            Identifier jsonId = loaded.id();
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
