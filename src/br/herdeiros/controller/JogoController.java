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

        // 4. Ouvinte para abrir a Janela de Cartelas dos Herdeiros
        this.view.getPainelControle().getBtnVerCartelas().addActionListener(new ActionListener() {
            private br.herdeiros.view.JanelaCartelas janelaCartelas;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (janelaCartelas == null || !janelaCartelas.isDisplayable()) {
                    janelaCartelas = new br.herdeiros.view.JanelaCartelas(partida);
                }
                janelaCartelas.sincronizarComJogadorDaVez();
                janelaCartelas.setVisible(true);
                janelaCartelas.toFront();
            }
        });

        // 4.1 Ouvinte para abrir a Janela de Cartas Secretas dos Jogadores (Figura 5 do professor)
        this.view.getPainelControle().getBtnVerCartas().addActionListener(new ActionListener() {
            private br.herdeiros.view.JanelaCartas janelaCartas;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (janelaCartas == null || !janelaCartas.isDisplayable()) {
                    janelaCartas = new br.herdeiros.view.JanelaCartas(partida);
                }
                janelaCartas.sincronizarComJogadorDaVez();
                janelaCartas.setVisible(true);
                janelaCartas.toFront();
            }
        });

        // 5. Ouvinte para abrir a Janela de Inventário de Recursos
        this.view.getPainelControle().getBtnVerInventario().addActionListener(new ActionListener() {
            private br.herdeiros.view.JanelaInventario janelaInventario;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (janelaInventario == null || !janelaInventario.isDisplayable()) {
                    janelaInventario = new br.herdeiros.view.JanelaInventario(partida);
                }
                janelaInventario.atualizarInventario();
                janelaInventario.setVisible(true);
                janelaInventario.toFront();
            }
        });
    }

    private void processarCliqueNoTabuleiro(int x, int y) {
        if (movimentosRestantes <= 0) {
            System.out.println("[DEBUG] Clique ignorado: defina movimentos no Modo Testador primeiro!");
            return;
        }

        int largura = view.getPainelTabuleiro().getWidth();
        int altura = view.getPainelTabuleiro().getHeight();

        String destino = MapaCoordenadas.identificarLocalizacaoClicada(x, y, largura, altura);
        if (destino == null) {
            System.out.println("[DEBUG] Nenhuma localidade reconhecida perto do clique (" + x + ", " + y + ").");
            return;
        }

        String origem = partida.getLocalizacaoJogador(partida.getCorJogadorAtual());
        if (origem == null || origem.equalsIgnoreCase(destino)) {
            return;
        }

        int distancia = partida.calcularDistancia(origem, destino);
        System.out.println("[DEBUG] Tentando mover de " + origem + " para " + destino + ". Distancia no grafo: " + distancia + " (Movimentos disponiveis: " + movimentosRestantes + ")");

        if (distancia > 0 && distancia <= movimentosRestantes) {
            boolean moveu = partida.moverJogadorAtual(destino);
            if (moveu) {
                movimentosRestantes -= distancia;
                atualizarInterface();
            }
        } else {
            // Movimento inválido ou passos insuficientes: a regra exige silêncio total
            System.out.println("[DEBUG] Movimento recusado: passos insuficientes ou local desconectado.");
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