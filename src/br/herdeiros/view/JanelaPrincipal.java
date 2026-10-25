package br.herdeiros.view;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class JanelaPrincipal extends JFrame {
    
    private PainelTabuleiro painelTabuleiro;
    private PainelControle painelControle;

    public JanelaPrincipal() {
        setTitle("Herdeiros de Khan");
        setSize(1400, 1050); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());
        
        painelTabuleiro = new PainelTabuleiro();
        painelControle = new PainelControle();
        
        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelControle, BorderLayout.EAST);
    }

    public void exibir() {
        setVisible(true);
    }

    // Getters essenciais para o Controller
    public PainelControle getPainelControle() { return painelControle; }
    public PainelTabuleiro getPainelTabuleiro() { return painelTabuleiro; }
}