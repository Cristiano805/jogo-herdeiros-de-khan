package br.herdeiros.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PainelDados extends JPanel {
    
    private Image[] imgDados = new Image[6];
    private int valorDado1 = 1;
    private int valorDado2 = 1;
    private Color corJogador = Color.RED; // Cor do jogador da vez

    public PainelDados() {
        setPreferredSize(new Dimension(280, 150));
        try {
            // Tenta carregar as 6 imagens dos dados da pasta img/dados/
            // NOTA: Verifique se os ficheiros na sua pasta se chamam "dado1.png", "1.png", ou ".jpg" e ajuste aqui.
            for (int i = 0; i < 6; i++) {
                imgDados[i] = ImageIO.read(new File("img/dados/dado" + (i + 1) + ".png")); 
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens dos dados: " + e.getMessage());
        }
    }

    // Método que o Controller vai chamar para atualizar a tela
    public void atualizarDados(int d1, int d2, Color cor) {
        this.valorDado1 = d1;
        this.valorDado2 = d2;
        this.corJogador = cor;
        repaint(); // Força o Java a chamar o paintComponent novamente
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 1. Pinta a área de fundo com a cor do jogador para indicar a vez
        g2d.setColor(corJogador);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // 2. Desenha os dois dados lada a lado (Java 2D estrito)
        if (imgDados[valorDado1 - 1] != null && imgDados[valorDado2 - 1] != null) {
            g2d.drawImage(imgDados[valorDado1 - 1], 20, 25, 100, 100, this);
            g2d.drawImage(imgDados[valorDado2 - 1], 140, 25, 100, 100, this);
        }
    }
}