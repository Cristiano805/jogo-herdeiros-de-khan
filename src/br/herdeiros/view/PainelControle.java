package br.herdeiros.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PainelControle extends JPanel {
    
    private JButton btnJogarDados;
    private JComboBox<Integer> comboDado1;
    private JComboBox<Integer> comboDado2;
    private PainelDados painelDados;
    
    public PainelControle() {
        // Define a largura do painel lateral (300px) e um fundo suave
        setPreferredSize(new Dimension(300, 1050));
        setBackground(new Color(240, 240, 245));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));
        
        // Título principal do painel lateral
        JLabel lblTitulo = new JLabel("HERDEIROS DE KHAN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(40, 40, 40));
        add(lblTitulo);
        
        // 1. Painel de Dados com Borda Estilizada e Título
        painelDados = new PainelDados();
        painelDados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), 
            "Estado / Dados da Jogada", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 12), 
            new Color(80, 80, 80)
        ));
        add(painelDados);
        
        // 2. Botão de rolar os dados com aspeto moderno
        btnJogarDados = new JButton("Jogar Dados");
        btnJogarDados.setPreferredSize(new Dimension(260, 45));
        btnJogarDados.setFont(new Font("Arial", Font.BOLD, 14));
        btnJogarDados.setBackground(new Color(41, 128, 185)); // Azul elegante
        btnJogarDados.setForeground(Color.WHITE);
        btnJogarDados.setFocusPainted(false);
        add(btnJogarDados);
        
        // 3. Secção do Modo Testador agrupada num painel dedicado
        JPanel painelTestador = new JPanel();
        painelTestador.setPreferredSize(new Dimension(260, 90));
        painelTestador.setBackground(Color.WHITE);
        painelTestador.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Modo Testador (Forçar Dados)", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 11), 
            new Color(100, 100, 100)
        ));
        
        Integer[] valores = {1, 2, 3, 4, 5, 6};
        comboDado1 = new JComboBox<>(valores);
        comboDado2 = new JComboBox<>(valores);
        
        painelTestador.add(new JLabel("D1:"));
        painelTestador.add(comboDado1);
        painelTestador.add(new JLabel("D2:"));
        painelTestador.add(comboDado2);
        
        add(painelTestador);
    }

    // Métodos Getters para o Controller aceder aos componentes
    public JButton getBtnJogarDados() { return btnJogarDados; }
    public JComboBox<Integer> getComboDado1() { return comboDado1; }
    public JComboBox<Integer> getComboDado2() { return comboDado2; }
    public PainelDados getPainelDados() { return painelDados; }
}