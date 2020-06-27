package movida.marromerli;

/**
 * Una stringa che esegue confronti case-insensitive.
 */
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

    /**
     * Restituisce l'hash code.
     *
     * @return L'hash code
     */
    @Override
    public int hashCode() {
        return this.string.toLowerCase().hashCode();
    }

    /**
     * Variante case-insensitive di String.contains.
     *
     * @param source    La stringa che può contenere <code>contained</code>
     * @param contained La stringa che può essere contenuta in <code>source</code>
     * @return True se <code>source</code> contiene <code>contained</code>
     * (ignorando maiuscole e minuscole), false altrimenti
     */
    public static boolean contains(CaseInsensitiveString source, CaseInsensitiveString contained) {
        return source.string.toLowerCase().contains(contained.string.toLowerCase());
    }
}
