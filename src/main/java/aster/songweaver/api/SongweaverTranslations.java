package aster.songweaver.api;

import net.minecraft.util.Identifier;

public class SongweaverTranslations {

    // id is the file Identifier, e.g. "songweaver:fire_bolt"
    public static String draftKey(Identifier id) {
        return id.getNamespace() + ".draft." + sanitize(id.getPath());
    }

    public static String ritualKey(Identifier id) {
        return id.getNamespace() + ".ritual." + sanitize(id.getPath());
    }

    // Replaces slashes in case any subfolder structure sneaks in
    private static String sanitize(String path) {
        return path.replace("/", ".");
    }

}
