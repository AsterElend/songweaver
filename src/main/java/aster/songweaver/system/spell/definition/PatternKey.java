package aster.songweaver.system.spell.definition;

import java.util.List;

public record PatternKey(List<Note> notes) {
    public static PatternKey of(List<Note> notes) {
        // Defensive copy + immutability
        return new PatternKey(List.copyOf(notes));
    }

    // in PatternKey
    public List<Note> notes() {
        return notes;
    }

}
