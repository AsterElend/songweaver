package aster.songweaver.api.weaving;

import net.minecraft.util.Identifier;

public enum Note {

    C(0),
    D(2),
    E(4),
    F(5),
    G(7),
    A(9),
    B(11),
    C_HIGH(12),
    REST(-1);


    Note(Integer semitone) {
        this.semitone = semitone;
    }

    public static Note fromHotbarIndex(int index){
        return switch (index){
            case 0 -> C;       // 1
            case 1 -> D;       // 2
            case 2 -> E;       // 3
            case 3 -> F;       // 4
            case 4 -> G;       // 5
            case 5 -> A;       // 6
            case 6 -> B;       // 7 etc.
            case 7 -> C_HIGH;
            case 8 -> REST;
            default -> null;
        };
    }


    public String display(){
        if (this == REST){
            return "REST";
        }
        return this == C_HIGH ? "C'": name();

    }

    private final int semitone;

    public float pitch() {
        return (float) Math.pow(2.0, (semitone - 12) / 12.0);
    }
    public static String noteTextureId(Note note) {
        return switch (note) {
            case C -> "emic";
            case D -> "emid";
            case E -> "emie";
            case F -> "emif";
            case G -> "emig";
            case A -> "emia";
            case B -> "emib";
            case C_HIGH -> "emichigh";
            case REST -> "emirest";
        };
    }

    public static Identifier noteTexture(Note note) {
        return new Identifier("songweaver", "textures/gui/" + noteTextureId(note) + ".png");
    }

}
