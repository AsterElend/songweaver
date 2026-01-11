package aster.songweaver.system.definition;

import java.util.List;

public record PatternKey(List<Note> notes) {
    public static PatternKey of(List<Note> notes) {
        // Defensive copy + immutability
        return new PatternKey(List.copyOf(notes));
    }
}
