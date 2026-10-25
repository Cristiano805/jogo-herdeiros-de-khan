package br.herdeiros.controller;

import br.herdeiros.model.Partida;
import br.herdeiros.view.JanelaPrincipal;
import br.herdeiros.view.MapaCoordenadas;
import br.herdeiros.view.PainelTabuleiro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador da aplicação (Controller do MVC).
 * Intermedia as ações do jogador entre a View e o componente Model (Partida).
 * SEM uso de expressões lambda em listeners (regra obrigatória da disciplina).
 */
public class JogoController {

    private Partida partida;
    private JanelaPrincipal view;
    private int movimentosRestantes = 0;

    public JogoController(Partida partida, JanelaPrincipal view) {
        this.partida = partida;
        this.view = view;

        configurarOuvintes();
        atualizarInterface();
    }

    private void configurarOuvintes() {
        // 1. Ouvinte de cliques no tabuleiro (movimentação para parada/cidade vizinha)
        this.view.getPainelTabuleiro().setOuvinteClique(new PainelTabuleiro.OuvinteCliqueTabuleiro() {
            @Override
            public void aoClicarNoTabuleiro(int x, int y) {
                processarCliqueNoTabuleiro(x, y);
            }
        });

        // 2. Ouvinte do botão Encerrar Turno (passa a vez para o próximo jogador)
        this.view.getPainelControle().getBtnEncerrarTurno().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encerrarTurnoAtual();
            }
        });

        // 3. Ouvinte do modo testador (define os movimentos / cavalos disponíveis)
        this.view.getPainelControle().getBtnDefinirMovimentos().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                definirMovimentosTestador();
            }
        });
    }

    private void processarCliqueNoTabuleiro(int x, int y) {
        if (movimentosRestantes <= 0) {
            // Sem movimentos disponíveis: de acordo com a especificação, não se deve exibir pop-up
            return;
        }

        int largura = view.getPainelTabuleiro().getWidth();
        int altura = view.getPainelTabuleiro().getHeight();

        String destino = MapaCoordenadas.identificarLocalizacaoClicada(x, y, largura, altura);
        if (destino == null) {
            return;
        }

        // Tenta realizar o movimento através do Model
        boolean moveu = partida.moverJogadorAtual(destino);
        if (moveu) {
            movimentosRestantes--;
            atualizarInterface();
        } else {
            // Movimento inválido: a regra exige silêncio, sem mensagens de erro e sem passar o turno
        }
    }

    private void encerrarTurnoAtual() {
        partida.passarTurno();
        movimentosRestantes = 0;
        atualizarInterface();
    }

    private void definirMovimentosTestador() {
        Object item = view.getPainelControle().getComboMovimentos().getSelectedItem();
        if (item instanceof Integer) {
            movimentosRestantes = (Integer) item;
            atualizarInterface();
        }
    }

    private void atualizarInterface() {
        view.atualizarView(movimentosRestantes);
    }
}