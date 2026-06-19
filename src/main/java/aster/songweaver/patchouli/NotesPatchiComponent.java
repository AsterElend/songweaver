package aster.songweaver.patchouli;

import aster.songweaver.api.weaving.Note;
import aster.songweaver.emi.SongweaverEmiUtil;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class NotesPatchiComponent implements ICustomComponent {

    @SerializedName("notes")
    public String notesRaw; // e.g. "C,G,A,C_HIGH,REST"


    @SerializedName("x")
    public int x;


    @SerializedName("y")
    public int y;

    private List<Note> resolvedNotes;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        x = componentX;
        y = componentY;
        resolvedNotes = new ArrayList<>();
        if (notesRaw != null && !notesRaw.isBlank()) {
            for (String raw : notesRaw.split(",")) {
                try {
                    resolvedNotes.add(Note.valueOf(raw.trim()));
                } catch (IllegalArgumentException ignored) {
                    // skip unrecognized note names
                }
            }
        }
    }


    @Override
    public void render(DrawContext draw, IComponentRenderContext iComponentRenderContext, float v, int i, int i1) {
        int curX = x;
        int curY = y;

        for (int iterator = 0; iterator < resolvedNotes.size(); iterator++) {
            if (iterator > 0 && iterator % SongweaverEmiUtil.NOTES_PER_ROW == 0) {
                curX = x;
                curY += SongweaverEmiUtil.NOTE_RENDERED_H;
            }

            Identifier texture = Note.noteTexture(resolvedNotes.get(iterator));

            draw.getMatrices().push();
            draw.getMatrices().translate(curX, curY, 0);
            draw.getMatrices().scale(SongweaverEmiUtil.NOTE_SCALE, SongweaverEmiUtil.NOTE_SCALE, 1);
            RenderSystem.setShaderTexture(0, texture);
            draw.drawTexture(texture, 0, 0, 0, 0, SongweaverEmiUtil.NOTE_W, SongweaverEmiUtil.NOTE_H,
                    SongweaverEmiUtil.NOTE_W, SongweaverEmiUtil.NOTE_H);
            draw.getMatrices().pop();

            curX += SongweaverEmiUtil.NOTE_RENDERED_W;
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> unaryOperator) {
        notesRaw = unaryOperator.apply(IVariable.wrap(notesRaw)).asString();
    }


}
