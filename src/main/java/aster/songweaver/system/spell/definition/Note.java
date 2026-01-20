package aster.songweaver.system.spell.definition;

public enum Note {

    C(0),
    D(2),
    E(4),
    F(5),
    G(7),
    A(9),
    B(11),
    C_HIGH(12);


    Note(int semitone) {
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
            case 6 -> B;       // 7
            case 7 -> C_HIGH;  // 8
            default -> null;
        };
    }


    public String display(){
        return this == C_HIGH ? "C'": name();
    }

    private final int semitone;
    public float pitch() {
        return (float) Math.pow(2.0, (semitone - 12) / 12.0);
    }


}
