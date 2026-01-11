package aster.songweaver.client;

import aster.songweaver.system.definition.Note;

import java.util.ArrayList;
import java.util.List;

public final class InputBuffer {
    private static final List<Note> NOTES = new ArrayList<>();

    public static void add(Note note){
        if (note != null){
            NOTES.add(note);
        }
    }


    public static List<Note> snapshot(){return List.copyOf(NOTES);}

    public static void clear(){NOTES.clear();}

    public static boolean isEmpty(){return NOTES.isEmpty();}

    public static void pop(){
        if (!NOTES.isEmpty()){
            NOTES.remove(NOTES.size() -1);
        }
    }


}
