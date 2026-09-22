package br.edu.senai.fatesg.avcar.datastructures;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * <b>Estrutura de Dados: Algoritmos de Busca</b>
 * <p>
 * Contém implementações de busca para coleções genéricas no sistema.
 * </p>
 */
public class BuscaOS {

    private BuscaOS() {}

    /**
     * <b>Busca Linear (Sequencial)</b>
     * <p>Percorre a lista elemento por elemento. Ideal para textos parciais (contains).</p>
     * <p><b>Complexidade:</b> O(n) onde n é o tamanho da lista.</p>
     * 
     * @param lista A lista genérica onde será feita a busca
     * @param extrator Função lambda para extrair o valor String a ser comparado do objeto T
     * @param alvo O termo a ser buscado
     * @return O índice do elemento encontrado, ou -1 se não encontrar
     */
    public static <T> int buscaLinear(List<T> lista, Function<T, String> extrator, String alvo) {
        if (lista == null || alvo == null) return -1;
        String target = alvo.toLowerCase();
        for (int i = 0; i < lista.size(); i++) {
            String valor = extrator.apply(lista.get(i));
            if (valor != null && valor.toLowerCase().contains(target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * <b>Busca Binária</b>
     * <p>
     * Busca rápida baseada em divisão e conquista.
     * <b>ATENÇÃO:</b> A lista DEVE estar previamente ordenada.
     * </p>
     * <p><b>Complexidade:</b> O(log n).</p>
     * 
     * @param lista A lista ordenada genérica
     * @param alvo O elemento procurado
     * @param comparator Comparador para determinar a direção da busca
     * @return O índice do elemento encontrado, ou -1 se não encontrar
     */
    public static <T> int buscaBinaria(List<T> lista, T alvo, Comparator<T> comparator) {
        if (lista == null || lista.isEmpty()) return -1;
        int esq = 0, dir = lista.size() - 1;
        while (esq <= dir) {
            int meio = esq + (dir - esq) / 2;
            int cmp = comparator.compare(lista.get(meio), alvo);
            if (cmp == 0) return meio;
            if (cmp < 0) esq = meio + 1;
            else dir = meio - 1;
        }
        return -1;
    }
}
