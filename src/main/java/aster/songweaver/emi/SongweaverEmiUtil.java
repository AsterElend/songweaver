package aster.songweaver.emi;

import aster.songweaver.api.weaving.Note;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;

import java.util.List;

public class SongweaverEmiUtil {

    public static final int NOTE_W = 4;
    public static final int NOTE_H = 22;
    public static final int NOTE_SCALE = 3;
    public static final int NOTE_RENDERED_W = NOTE_W * NOTE_SCALE;   // 12
    public static final int NOTE_RENDERED_H = NOTE_H * NOTE_SCALE;   // 66
    public static final int NOTES_PER_ROW = 12;

    /**
     * Adds note texture widgets for a pattern, wrapping every 12 notes.
     * @param widgets  the EMI WidgetHolder
     * @param notes    the ordered list of notes from the PatternKey
     * @param startX   left edge
     * @param startY   top edge
     * @return the Y position immediately below the last note row
     */
    public static int addNoteWidgets(WidgetHolder widgets, List<Note> notes, int startX, int startY) {
        int x = startX;
        int y = startY;

        for (int i = 0; i < notes.size(); i++) {
            if (i > 0 && i % NOTES_PER_ROW == 0) {
                // Wrap to next row
                x = startX;
                y += NOTE_RENDERED_H;
            }

            Identifier texture = Note.noteTexture(notes.get(i));

            // addTexture(id, x, y, u, v, regionWidth, regionHeight, textureWidth, textureHeight)
            // Since each file is a single 4x22 image, u/v are 0,0 and region = full texture
            widgets.addTexture(texture, x, y, NOTE_RENDERED_W, NOTE_RENDERED_H, 0, 0, NOTE_W, NOTE_H, NOTE_W, NOTE_H);

            x += NOTE_RENDERED_W;
        }

        // Return Y below the last row
        return y + NOTE_RENDERED_H;
    }

    /**
     * How many rows will a given note count need?
     */
    public static int noteRowCount(int noteCount) {
        return (int) Math.ceil((double) noteCount / NOTES_PER_ROW);
    }

    /**
     * Total pixel height consumed by the note strip.
     */
    public static int noteStripHeight(int noteCount) {
        return noteRowCount(noteCount) * NOTE_RENDERED_H;
    }
}