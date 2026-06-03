package br.com.estruturas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementação de lista encadeada genérica
 * Permite adicionar elementos e iterar sobre eles
 */
public class ListaLinear<T> implements Iterable<T> {
    // Referência ao primeiro nó da lista
    private No<T> primeiro;
    private No<T> ultimo;
    private int tamanho;

    
    private static class No<T> {
        T valor;
        No<T> proximo;

        No(T valor) {
            this.valor = valor;
        }
    }

    
    public void adicionar(T valor) {
        No<T> novo = new No<>(valor);
        if (primeiro == null) {
            primeiro = novo;
            ultimo = novo;
        } else {
            ultimo.proximo = novo; 
            ultimo = novo;         
        }
        tamanho++;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    
    public List<T> paraList() {
        List<T> resultado = new ArrayList<>();
        for (T valor : this) {
            resultado.add(valor);
        }
        return resultado;
    }

    // Implementa iterador para percorrer a lista encadeada
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private No<T> atual = primeiro; 

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                T valor = atual.valor;
                atual = atual.proximo; 
                return valor;
            }
        };
    }
}