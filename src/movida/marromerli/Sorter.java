package movida.marromerli;

import java.util.Comparator;
import java.util.List;

/**
 * Interfaccia per ordinare una lista di elementi.
 */
public interface Sorter {
    /**
     * Ordina una lista in-loco.
     *
     * @param keys       Gli elementi da ordinare.
     * @param comparator Comparatore per stabilire l'ordinamento.
     * @param <T>        Tipo generico degli elementi.
     */
    <T> void sort(List<T> keys, Comparator<T> comparator);
}