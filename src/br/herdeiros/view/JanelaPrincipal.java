package br.herdeiros.view;

import br.herdeiros.model.Partida;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * Janela principal da aplicação (respeita o limite máximo de 1400x1050 pixels).
 */
public class JanelaPrincipal extends JFrame {

    private Partida partida;
    private PainelTabuleiro painelTabuleiro;
    private PainelControle painelControle;

    public JanelaPrincipal(Partida partida) {
        this.partida = partida;

        setTitle("Herdeiros de Khan");
        setSize(1400, 1020);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelTabuleiro = new PainelTabuleiro(partida);
        painelControle = new PainelControle(partida);

        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelControle, BorderLayout.EAST);
    }

    public void exibir() {
        setVisible(true);
    }

    public void atualizarView(int movimentosRestantes) {
        painelStatus().atualizarStatus(movimentosRestantes);
        painelTabuleiro.repaint();
    }

    public PainelControle getPainelControle() {
        return painelControle;
    }

    public PainelTabuleiro getPainelTabuleiro() {
        return painelTabuleiro;
    }

    public PainelStatusTurno painelStatus() {
        return painelControle.getPainelStatus();
    }
}