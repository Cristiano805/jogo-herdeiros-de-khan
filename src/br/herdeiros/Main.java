package br.herdeiros;

import br.herdeiros.controller.JogoController;
import br.herdeiros.view.JanelaPrincipal;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        // 1. Janela inicial para escolher a quantidade de jogadores
        String inputQtd = JOptionPane.showInputDialog(null, 
                "Quantos jogadores vão participar? (2 a 4)", 
                "Herdeiros de Khan - Novo Jogo", 
                JOptionPane.QUESTION_MESSAGE);
        
        // Se o utilizador clicar em cancelar ou fechar a janela
        if (inputQtd == null) {
            System.exit(0);
        }
        
        try {
            int qtdJogadores = Integer.parseInt(inputQtd);
            
            if (qtdJogadores < 2 || qtdJogadores > 6) {
                JOptionPane.showMessageDialog(null, "Por favor, escolhe um número entre 2 e 6 jogadores.");
                main(args); // Reinicia a pergunta se o valor for inválido
                return;
            }
            
            // Mensagem de confirmação antes de abrir o tabuleiro
            JOptionPane.showMessageDialog(null, "Jogo iniciado com " + qtdJogadores + " jogadores! Bom jogo!");

            // 2. Inicializa a janela principal e o controlador do jogo
            JanelaPrincipal janela = new JanelaPrincipal();
            JogoController controller = new JogoController(janela);
            
            // Exibe a janela do tabuleiro
            janela.exibir();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Valor inválido! Por favor, insere um número inteiro.");
            main(args);
        }
    }
}