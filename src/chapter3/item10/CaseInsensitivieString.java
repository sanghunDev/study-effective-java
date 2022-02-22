package chapter3.item10;

import java.util.Objects;

public final class CaseInsensitivieString {
    private final String s;

    public CaseInsensitivieString(String s) {
            this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitivieString)
            return s.equalsIgnoreCase(((CaseInsensitivieString) o).s);
        if (o instanceof String)
            return s.equalsIgnoreCase((String) o);
        return false;
    }

}