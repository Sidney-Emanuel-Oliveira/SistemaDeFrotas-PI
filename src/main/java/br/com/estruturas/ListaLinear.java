package br.com.estruturas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Implementação de uma Lista Encadeada Simples genérica com suporte a iteração (foreach)
public class ListaLinear<T> implements Iterable<T> {
    private No<T> primeiro;
    private No<T> ultimo;
    private int tamanho;

    // Estrutura interna (Nó) que encapsula o valor e guarda a referência para o próximo elemento
    private static class No<T> {
        T valor;
        No<T> proximo;

        No(T valor) {
            this.valor = valor;
        }
    }

    // Adiciona um novo elemento ao final da lista (complexidade O(1) devido ao ponteiro 'ultimo')
    public void adicionar(T valor) {
        No<T> novo = new No<>(valor);
        if (primeiro == null) {
            primeiro = novo;
            ultimo = novo;
        } else {
            ultimo.proximo = novo; // Conecta o nó antigo ao novo
            ultimo = novo;         // Atualiza o ponteiro do último elemento
        }
        tamanho++;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    // Converte a estrutura encadeada atual em uma sublista padrão da Collections API (ArrayList)
    public List<T> paraList() {
        List<T> resultado = new ArrayList<>();
        for (T valor : this) {
            resultado.add(valor);
        }
        return resultado;
    }

    // Implementação do Iterador para permitir o percurso da lista usando laços foreach externos
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private No<T> atual = primeiro; // Inicia a varredura a partir do topo da lista

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                T valor = atual.valor;
                atual = atual.proximo; // Desloca o ponteiro para o próximo nó da corrente
                return valor;
            }
        };
    }
}