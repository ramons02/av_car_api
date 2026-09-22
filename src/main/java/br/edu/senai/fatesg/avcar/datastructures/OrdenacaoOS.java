package br.edu.senai.fatesg.avcar.datastructures;

import java.util.Comparator;
import java.util.List;

/**
 * <b>Estrutura de Dados: Algoritmos de Ordenação</b>
 * <p>
 * Implementação manual de algoritmos de ordenação baseados em "Dividir e Conquistar",
 * sem uso de coleções nativas de sort do Java, conforme exigência do projeto.
 * </p>
 */
public class OrdenacaoOS {

    private OrdenacaoOS() {}

    /**
     * <b>Merge Sort</b>
     * <p>
     * Divide a lista repetidamente na metade até ter elementos individuais e 
     * depois as combina de volta na ordem correta.
     * </p>
     * <p><b>Complexidade:</b> O(n log n) garantido em todos os casos.</p>
     * <p><b>Estabilidade:</b> É um algoritmo estável (mantém a ordem relativa de elementos de mesmo valor), excelente para relatórios.</p>
     * 
     * @param lista A lista genérica a ser ordenada
     * @param comparator O comparador para definir a regra de ordenação
     */
    public static <T> void mergeSort(List<T> lista, Comparator<T> comparator) {
        if (lista == null || lista.size() <= 1) return;
        mergeSortRecursivo(lista, 0, lista.size() - 1, comparator);
    }

    private static <T> void mergeSortRecursivo(List<T> lista, int left, int right, Comparator<T> c) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortRecursivo(lista, left, mid, c);
        mergeSortRecursivo(lista, mid + 1, right, c);
        merge(lista, left, mid, right, c);
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(List<T> lista, int left, int mid, int right, Comparator<T> c) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Object[] leftArr = new Object[n1];
        Object[] rightArr = new Object[n2];

        for (int i = 0; i < n1; i++) leftArr[i] = lista.get(left + i);
        for (int j = 0; j < n2; j++) rightArr[j] = lista.get(mid + 1 + j);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            T a = (T) leftArr[i];
            T b = (T) rightArr[j];
            if (c.compare(a, b) <= 0) {
                lista.set(k++, a);
                i++;
            } else {
                lista.set(k++, b);
                j++;
            }
        }
        while (i < n1) lista.set(k++, (T) leftArr[i++]);
        while (j < n2) lista.set(k++, (T) rightArr[j++]);
    }

    /**
     * <b>Quick Sort</b>
     * <p>
     * Escolhe um elemento 'pivô' e particiona a lista de modo que elementos menores
     * fiquem à esquerda e os maiores à direita, ordenando recursivamente as partições.
     * </p>
     * <p><b>Complexidade:</b> O(n log n) médio. Pior caso: O(n²) se a lista já estiver ordenada.</p>
     * <p><b>Estabilidade:</b> Instável.</p>
     * 
     * @param lista A lista genérica a ser ordenada
     * @param comparator O comparador para definir a regra de ordenação
     */
    public static <T> void quickSort(List<T> lista, Comparator<T> comparator) {
        if (lista == null || lista.size() <= 1) return;
        quickSortRecursivo(lista, 0, lista.size() - 1, comparator);
    }

    private static <T> void quickSortRecursivo(List<T> lista, int low, int high, Comparator<T> c) {
        if (low >= high) return;
        int pi = partition(lista, low, high, c);
        quickSortRecursivo(lista, low, pi - 1, c);
        quickSortRecursivo(lista, pi + 1, high, c);
    }

    private static <T> int partition(List<T> lista, int low, int high, Comparator<T> c) {
        int mid = low + (high - low) / 2;
        T pivot = lista.get(mid);

        T temp = lista.get(mid);
        lista.set(mid, lista.get(high));
        lista.set(high, temp);

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (c.compare(lista.get(j), pivot) <= 0) {
                i++;
                T troca = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, troca);
            }
        }
        T troca = lista.get(i + 1);
        lista.set(i + 1, lista.get(high));
        lista.set(high, troca);
        return i + 1;
    }
}
