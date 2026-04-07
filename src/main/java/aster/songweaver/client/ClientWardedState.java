package aster.songweaver.client;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class ClientWardedState {

    private static final LongOpenHashSet WARDED = new LongOpenHashSet();

    public static void set(long pos, boolean warded) {
        if (warded) WARDED.add(pos);
        else WARDED.remove(pos);
    }

    public static LongOpenHashSet get() {
        return WARDED;
    }
}