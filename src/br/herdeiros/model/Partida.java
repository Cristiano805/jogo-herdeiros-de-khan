package br.herdeiros.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Partida {
    private Map<Cor, Jogador> jogadores;
    private Tabuleiro tabuleiro;
    private BaralhoDeCidades baralho;
    private Jogador jogadorAtual;

    public Partida() {
        this.jogadores = new HashMap<>();
        this.baralho = new BaralhoDeCidades();
        // Inicializa o tabuleiro com as 3 cidades iniciais sacadas do baralho
        this.tabuleiro = new Tabuleiro(baralho.sacarCidadesIniciais());
    }

    public void adicionarJogador(String nome, Cor cor, int quantidadeDescendentes) {
        Jogador novoJogador = new Jogador(nome, cor, quantidadeDescendentes);
        this.jogadores.put(cor, novoJogador);
        
        // Se for o primeiro jogador, define como o atual provisoriamente
        if (this.jogadorAtual == null) {
            this.jogadorAtual = novoJogador;
        }
    }

    public Jogador getJogador(Cor cor) {
        return this.jogadores.get(cor);
    }

    public Map<Cor, Jogador> getJogadores() {
        return Collections.unmodifiableMap(jogadores);
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public BaralhoDeCidades getBaralho() {
        return baralho;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    // Método para avançar ou definir o turno (exemplo básico para a regra de negócio)
    public void definirJogadorAtual(Jogador jogador) {
        if (jogador != null && this.jogadores.containsKey(jogador.getCor())) {
            this.jogadorAtual = jogador;
        }
    }
}
