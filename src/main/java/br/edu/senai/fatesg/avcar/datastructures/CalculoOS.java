package br.edu.senai.fatesg.avcar.datastructures;

import java.util.List;

public class CalculoOS {

    private CalculoOS() {}

    /**
     * <b>Somador Recursivo Genérico</b>
     * <p>
     * Percorre a lista do índice `i` até o final, acumulando valores de forma recursiva.
     * </p>
     * <ul>
     * <li><b>Caso base:</b> {@code i >= lista.size()} retorna 0</li>
     * <li><b>Passo recursivo:</b> {@code lista.get(i) + somar(lista, i + 1)}</li>
     * </ul>
     * 
     * @param valores Lista de valores
     * @param i Índice atual
     * @return A soma acumulada a partir do índice {@code i}
     */
    public static double somarValores(List<Double> valores, int i) {
        if (i >= valores.size()) return 0;
        return valores.get(i) + somarValores(valores, i + 1);
    }

    /**
     * <b>Cálculo do Valor Total da OS</b>
     * <p>
     * Consolida o total geral somando itens (peças, serviços, externos)
     * via recursão e subtraindo os descontos permitidos.
     * </p>
     */
    public static double calcularValorTotal(
            List<Double> servicos,
            List<Double> pecas,
            List<Double> externos,
            double valorDesconto) {
        double totalServicos = somarValores(servicos, 0);
        double totalPecas = somarValores(pecas, 0);
        double totalExternos = somarValores(externos, 0);
        double total = totalServicos + totalPecas + totalExternos;
        return total - Math.min(total, valorDesconto);
    }

    /**
     * <b>Função Fatorial Recursiva (Didática)</b>
     * <p>
     * Utilizada para demonstrar o conceito de recursão simples.
     * Útil em cenários de cálculo de permutações e combinações de agendamento.
     * </p>
     * <ul>
     * <li><b>Caso base:</b> {@code n <= 1} retorna 1</li>
     * <li><b>Passo recursivo:</b> {@code n * fatorial(n - 1)}</li>
     * </ul>
     * 
     * @param n O número a ter o fatorial calculado
     * @return O fatorial de n
     */
    public static long fatorial(int n) {
        if (n <= 1) return 1;
        return n * fatorial(n - 1);
    }
}
