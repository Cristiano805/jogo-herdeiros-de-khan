package br.herdeiros;

import br.herdeiros.controller.JogoController;
import br.herdeiros.model.Cor;
import br.herdeiros.model.Partida;
import br.herdeiros.view.JanelaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Inicializa a interface gráfica na Event Dispatch Thread do Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Cria a partida no Model
                Partida partida = new Partida();

                // 2. Configura os participantes da partida (conforme as regras: 2 a 5 jogadores)
                // Cada um recebe nome, cor e quantidade de descendentes
                partida.adicionarJogador("Batu Khan", Cor.VERMELHO, 4);
                partida.adicionarJogador("Berke Khan", Cor.AZUL, 2);
                partida.adicionarJogador("Hulagu Khan", Cor.VERDE, 5);

                // Regra oficial de Herdeiros de Khan: o jogador com mais descendentes começa
                partida.definirPrimeiroJogadorPorDescendentes();

                // 3. Inicializa a janela principal (View) e o Controlador (Controller)
                JanelaPrincipal janela = new JanelaPrincipal(partida);
                JogoController controller = new JogoController(partida, janela);

                // 4. Exibe a interface do tabuleiro
                janela.exibir();
            }
        });
    }
}