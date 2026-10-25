package br.herdeiros;

import br.herdeiros.controller.JogoController;
import br.herdeiros.model.Partida;
import br.herdeiros.view.JanelaEscolhaPersonagens;
import br.herdeiros.view.JanelaMenuInicial;
import br.herdeiros.view.JanelaPrincipal;

import java.util.List;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                iniciarFluxoDoJogo();
            }
        });
    }

    private static void iniciarFluxoDoJogo() {
        // 1ª Janela: Menu Inicial com opções "Novo Jogo", "Continuar" e "Sair"
        final JanelaMenuInicial[] refMenu = new JanelaMenuInicial[1];
        refMenu[0] = new JanelaMenuInicial(new JanelaMenuInicial.OuvinteMenuInicial() {
            @Override
            public void aoIniciarNovoJogo() {
                refMenu[0].dispose();
                abrirJanelaEscolhaPersonagens();
            }

            @Override
            public void aoContinuarJogoSalvo() {
                // Funcionalidade que será conectada ao sistema de persistência da 3ª iteração
            }
        });

        refMenu[0].exibir();
    }

    private static void abrirJanelaEscolhaPersonagens() {
        // 2ª Janela: Escolha de personagens, quantidade de jogadores (2 a 5), cores e descendentes
        final JanelaEscolhaPersonagens[] refEscolha = new JanelaEscolhaPersonagens[1];
        refEscolha[0] = new JanelaEscolhaPersonagens(
                new JanelaEscolhaPersonagens.OuvinteConfiguracaoJogo() {
                    @Override
                    public void aoConfirmarConfiguracao(List<JanelaEscolhaPersonagens.DadosJogadorConfig> jogadores) {
                        refEscolha[0].dispose();
                        iniciarPartidaComJogadores(jogadores);
                    }

                    @Override
                    public void aoVoltarMenu() {
                        refEscolha[0].dispose();
                        iniciarFluxoDoJogo();
                    }
                }
        );

        refEscolha[0].exibir();
    }

    private static void iniciarPartidaComJogadores(List<JanelaEscolhaPersonagens.DadosJogadorConfig> listaJogadores) {
        // Criação e configuração do Model (Partida)
        Partida partida = new Partida();

        for (JanelaEscolhaPersonagens.DadosJogadorConfig cfg : listaJogadores) {
            partida.adicionarJogador(cfg.nome, cfg.cor, cfg.descendentes);
        }

        // Regra oficial de Herdeiros de Khan: o jogador com o maior número de descendentes começa
        partida.definirPrimeiroJogadorPorDescendentes();

        // 3ª Janela: Exibição do Tabuleiro e do Painel de Controle via MVC
        JanelaPrincipal janelaTabuleiro = new JanelaPrincipal(partida);
        new JogoController(partida, janelaTabuleiro);

        janelaTabuleiro.exibir();
    }
}