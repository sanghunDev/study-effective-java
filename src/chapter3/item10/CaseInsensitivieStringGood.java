package chapter3.item10;

import java.util.Objects;

public final class CaseInsensitivieStringGood {
    private final String s;

    public CaseInsensitivieStringGood(String s) {
            this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitivieStringGood && ((CaseInsensitivieStringGood) o).s.equalsIgnoreCase(s);
    }

}