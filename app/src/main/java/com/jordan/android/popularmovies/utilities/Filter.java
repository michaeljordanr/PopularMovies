package com.jordan.android.popularmovies.utilities;

/**
 * Created by Michael on 27/02/2018.
 */

public enum Filter {
    POPULAR(1), TOP_RATED(2), FAVORITES(3), NONE(99);

    private final int value;

    Filter(int value) {
        this.value = value;
    }

    public static Filter fromValue(int value) {
        for (Filter my: Filter.values()) {
            if (my.value == value) {
                return my;
            }
        }

        return null;
    }

    public int getValue() {
        return value;
    }
}
