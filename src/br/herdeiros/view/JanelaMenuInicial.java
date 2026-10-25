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



        // Botões Centrais de Opção posicionados mais abaixo
        JPanel painelBotoes = new JPanel(new GridBagLayout());
        painelBotoes.setOpaque(false);
        painelBotoes.setBorder(new EmptyBorder(160, 0, 20, 0)); // Empurra os botões alguns pixels para baixo
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
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
        add(painelPrincipal);
    }

    private JButton criarBotaoMenu(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(300, 48));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public void exibir() {
        setVisible(true);
    }
}
