package br.herdeiros.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PainelTabuleiro extends JPanel {
    
    private Image imagemTabuleiro;
    // Referência opcional para avisar o controller quando houver um clique
    private OuvinteCliqueTabuleiro ouvinteClique;

    public PainelTabuleiro() {
        try {
            imagemTabuleiro = ImageIO.read(new File("img/tabuleiro.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do tabuleiro: " + e.getMessage());
        }

        // Adiciona o detetor de cliques do rato SEM usar lambdas
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                System.out.println("Clique detetado nas coordenadas X: " + x + ", Y: " + y);
                
                if (ouvinteClique != null) {
                    ouvinteClique.aoClicarNoTabuleiro(x, y);
                }
            }
        });
    }

    // Interface interna para passar o clique para o Controller de forma limpa
    public interface OuvinteCliqueTabuleiro {
        void aoClicarNoTabuleiro(int x, int y);
    }

    public void setOuvinteClique(OuvinteCliqueTabuleiro ouvinte) {
        this.ouvinteClique = ouvinte;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        }
    }
}