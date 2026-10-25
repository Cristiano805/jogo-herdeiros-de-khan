package br.herdeiros.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JanelaMenuInicial extends JFrame {

    private OuvinteMenuInicial ouvinte;

    public interface OuvinteMenuInicial {
        void aoIniciarNovoJogo();
        void aoContinuarJogoSalvo();
    }

    public JanelaMenuInicial(OuvinteMenuInicial ouvinte) {
        this.ouvinte = ouvinte;

        setTitle("Herdeiros de Khan - Menu Principal");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout()) {
            private java.awt.Image imgFundo;
            {
                try {
                    java.io.File f = new java.io.File("img/plano_de_fundo.jpg");
                    if (f.exists()) {
                        imgFundo = javax.imageio.ImageIO.read(f);
                    }
                } catch (Exception e) {}
            }
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (imgFundo != null) {
                    g.drawImage(imgFundo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Título e Subtítulo
        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setOpaque(false);

        JLabel lblTitulo = new JLabel("HERDEIROS DE KHAN", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(40, 40, 50));

        JLabel lblSubtitulo = new JLabel("Disputa pelo Conselho Kurultai no Império Mongol", JLabel.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(new Color(110, 110, 120));

        painelCabecalho.add(lblTitulo, BorderLayout.NORTH);
        painelCabecalho.add(lblSubtitulo, BorderLayout.SOUTH);
        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);

        // Botões Centrais de Opção
        JPanel painelBotoes = new JPanel(new GridBagLayout());
        painelBotoes.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton btnNovoJogo = criarBotaoMenu("Iniciar Novo Jogo", new Color(41, 128, 185));
        JButton btnContinuar = criarBotaoMenu("Continuar Jogo Salvo", new Color(52, 73, 94));
        JButton btnSair = criarBotaoMenu("Sair do Jogo", new Color(192, 57, 43));

        // Ouvintes de evento SEM usar expressao lambda
        btnNovoJogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JanelaMenuInicial.this.ouvinte != null) {
                    JanelaMenuInicial.this.ouvinte.aoIniciarNovoJogo();
                }
            }
        });

        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JanelaMenuInicial.this.ouvinte != null) {
                    JanelaMenuInicial.this.ouvinte.aoContinuarJogoSalvo();
                } else {
                    JOptionPane.showMessageDialog(JanelaMenuInicial.this,
                            "A funcionalidade de carregar jogo salvo estará disponível na 3ª iteração.",
                            "Carregar Jogo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        gbc.gridy = 0;
        painelBotoes.add(btnNovoJogo, gbc);
        gbc.gridy = 1;
        painelBotoes.add(btnContinuar, gbc);
        gbc.gridy = 2;
        painelBotoes.add(btnSair, gbc);

        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);

        // Rodapé de Créditos
        JLabel lblRodape = new JLabel("INF1636 - Programação Orientada a Objetos", JLabel.CENTER);
        lblRodape.setFont(new Font("Arial", Font.PLAIN, 11));
        lblRodape.setForeground(Color.GRAY);
        painelPrincipal.add(lblRodape, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JButton criarBotaoMenu(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(280, 48));
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public void exibir() {
        setVisible(true);
    }
}
