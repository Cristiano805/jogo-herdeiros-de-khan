package br.herdeiros.view;

import br.herdeiros.model.Cor;
import br.herdeiros.model.Partida;
import br.herdeiros.model.TipoTributo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Painel informativo desenhado estritamente com Java2D indicando a cor do jogador da vez,
 * o herdeiro atual, a sua localização e o saldo de movimentos restantes no turno.
 */
public class PainelStatusTurno extends JPanel {

    private Partida partida;
    private int movimentosRestantes = 0;

    public PainelStatusTurno(Partida partida) {
        this.partida = partida;
        setPreferredSize(new Dimension(270, 160));
        setBackground(Color.WHITE);
    }

    public void atualizarStatus(int movimentosRestantes) {
        this.movimentosRestantes = movimentosRestantes;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Cor corAtual = (partida != null) ? partida.getCorJogadorAtual() : null;
        Color awtCor = obterCorAwt(corAtual);

        // Faixa de cabeçalho colorida com a cor do jogador da vez
        g2d.setColor(awtCor);
        g2d.fillRoundRect(0, 0, w, 40, 10, 10);

        g2d.setColor(Color.WHITE);
        if (corAtual == Cor.BRANCO || corAtual == Cor.AMARELO) {
            g2d.setColor(Color.BLACK);
        }
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String nomeJogador = (partida != null && partida.getNomeJogadorAtual() != null)
                ? partida.getNomeJogadorAtual() : "Aguardando";
        g2d.drawString("Vez de: " + nomeJogador, 15, 25);

        // Corpo informativo do status
        g2d.setColor(new Color(245, 245, 250));
        g2d.fillRoundRect(0, 45, w, h - 45, 10, 10);
        g2d.setColor(new Color(200, 200, 210));
        g2d.drawRoundRect(0, 45, w - 1, h - 46, 10, 10);

        g2d.setColor(new Color(40, 40, 40));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        String loc = (partida != null && partida.getLocalizacaoJogadorAtual() != null)
                ? partida.getLocalizacaoJogadorAtual() : "-";
        g2d.drawString("Posição atual: " + loc, 15, 70);

        int votos = (partida != null) ? partida.getVotosJogadorAtual() : 0;
        g2d.drawString("Votos no Kurultai: " + votos, 15, 92);

        // Movimentos / Cavalos disponíveis para gastar no turno
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        if (movimentosRestantes > 0) {
            g2d.setColor(new Color(0, 130, 60));
            g2d.drawString("Movimentos restantes: " + movimentosRestantes, 15, 125);
        } else {
            g2d.setColor(new Color(180, 50, 50));
            g2d.drawString("Sem movimentos definidos", 15, 125);
        }

        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.setColor(Color.GRAY);
        g2d.drawString("(Cavalos da coluna ou Modo Testador)", 15, 145);
    }

    private Color obterCorAwt(Cor cor) {
        if (cor == null) return new Color(100, 100, 100);
        switch (cor) {
            case AMARELO: return new Color(230, 190, 0);
            case AZUL: return new Color(30, 144, 255);
            case BRANCO: return new Color(240, 240, 240);
            case LARANJA: return new Color(255, 140, 0);
            case PRETO: return new Color(30, 30, 30);
            case VERDE: return new Color(34, 139, 34);
            case VERMELHO: return new Color(220, 20, 60);
            default: return Color.GRAY;
        }
    }
}
