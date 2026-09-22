package br.edu.senai.fatesg.avcar.datastructures;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Estrutura de Dados: Fila (Queue) - FIFO</b>
 * <p>
 * <b>Conceito:</b> Uma fila operando no princípio First In, First Out (Primeiro a Entrar, Primeiro a Sair).
 * Usada para gerenciar os veículos/OS aguardando atendimento na oficina de forma justa.
 * </p>
 * <p>
 * <b>Complexidade de Tempo:</b> O(1) para inserção (enqueue) e remoção (dequeue) 
 * graças à implementação de Fila Circular com Array.
 * </p>
 *
 * @param <T> O tipo de objeto armazenado na fila (Genérico)
 */
public class FilaEsperaOS<T> {

    private Object[] elementos;
    private int inicio;
    private int fim;
    private int tamanho;
    private static final int CAPACIDADE_INICIAL = 10;

    public FilaEsperaOS() {
        this.elementos = new Object[CAPACIDADE_INICIAL];
        this.inicio = 0;
        this.fim = -1;
        this.tamanho = 0;
    }

    /**
     * Adiciona um item no final da fila (Rear).
     * <p>Complexidade: O(1) amortizado, pois o redimensionamento só ocorre quando cheio.</p>
     * 
     * @param item o item a ser enfileirado
     */
    public void enqueue(T item) {
        if (isFull()) {
            crescer();
        }
        fim = (fim + 1) % elementos.length;
        elementos[fim] = item;
        tamanho++;
    }

    /**
     * Remove e retorna o item que está no início da fila (Front).
     * <p>Complexidade: O(1) estrito.</p>
     * 
     * @return O primeiro item da fila
     * @throws IllegalStateException se a fila estiver vazia
     */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila vazia");
        }
        T item = (T) elementos[inicio];
        elementos[inicio] = null;
        inicio = (inicio + 1) % elementos.length;
        tamanho--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila vazia");
        }
        return (T) elementos[inicio];
    }

    public boolean isEmpty() {
        return tamanho == 0;
    }

    public boolean isFull() {
        return tamanho == elementos.length;
    }

    public int size() {
        return tamanho;
    }

    @SuppressWarnings("unchecked")
    public List<T> listar() {
        List<T> lista = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            int idx = (inicio + i) % elementos.length;
            lista.add((T) elementos[idx]);
        }
        return lista;
    }

    private void crescer() {
        int novaCapacidade = elementos.length * 2;
        Object[] novoArray = new Object[novaCapacidade];
        for (int i = 0; i < tamanho; i++) {
            novoArray[i] = elementos[(inicio + i) % elementos.length];
        }
        elementos = novoArray;
        inicio = 0;
        fim = tamanho - 1;
    }
}
