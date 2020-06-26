package movida.marromerli;

import movida.commons.Movie;

public class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {
    private String string;

    public CaseInsensitiveString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public int compareTo(CaseInsensitiveString caseInsensitiveString) {
        return this.string.compareToIgnoreCase(caseInsensitiveString.string);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CaseInsensitiveString)) {
            return false;
        }
        CaseInsensitiveString other = (CaseInsensitiveString) obj;
        return this.string.equalsIgnoreCase(other.string);
    }

    @Override
    public int hashCode() {
        return this.string.toLowerCase().hashCode();
    }

    public static boolean contains(CaseInsensitiveString source, CaseInsensitiveString contained) {
        return source.string.toLowerCase().contains(contained.string.toLowerCase());
    }
}
