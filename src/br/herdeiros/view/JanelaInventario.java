package br.herdeiros.view;

import br.herdeiros.model.Cor;
import br.herdeiros.model.Partida;
import br.herdeiros.model.TipoTributo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Janela separada para visualização do inventário de tributos e votos de cada jogador.
 * Pode ser aberta e fechada por meio de botão na tela principal.
 */
public class JanelaInventario extends JFrame {

    private Partida partida;
    private JPanel painelCartoes;

    public JanelaInventario(Partida partida) {
        this.partida = partida;

        setTitle("Herdeiros de Khan - Inventário dos Jogadores");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Inventário de Recursos & Tributos", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(40, 40, 50));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        painelCartoes = new JPanel();
        painelCartoes.setLayout(new GridLayout(0, 1, 10, 10));
        painelCartoes.setOpaque(false);
        painelPrincipal.add(painelCartoes, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(120, 36));
        btnFechar.setFont(new Font("Arial", Font.BOLD, 13));
        btnFechar.setBackground(new Color(127, 140, 141));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JanelaInventario.this.dispose();
            }
        });

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setOpaque(false);
        painelBotao.add(btnFechar);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);

        add(painelPrincipal);
        atualizarInventario();
    }

    public void atualizarInventario() {
        if (partida == null) return;

        painelCartoes.removeAll();

        for (Cor cor : partida.getCoresJogadores()) {
            String nome = partida.getNomeJogador(cor);
            int votos = partida.getVotosJogador(cor);
            String loc = partida.getLocalizacaoJogador(cor);
            int espadas = partida.getTributosJogador(cor, TipoTributo.ESPADA);
            int yurts = partida.getTributosJogador(cor, TipoTributo.YURT);
            int moedas = partida.getTributosJogador(cor, TipoTributo.MOEDA);

            JPanel card = new JPanel(new GridLayout(1, 4, 15, 5));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(obterCorAwt(cor), 2),
                    nome + " (" + cor + ")",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 13),
                    obterCorAwt(cor)
            ));

            JLabel lblPos = new JLabel("Posição: " + loc);
            lblPos.setFont(new Font("Arial", Font.PLAIN, 12));

            JLabel lblVotos = new JLabel("Votos Kurultai: " + votos);
            lblVotos.setFont(new Font("Arial", Font.BOLD, 12));

            JLabel lblTrib1 = new JLabel("⚔ Espadas: " + espadas + " | ⛺ Yurts: " + yurts);
            lblTrib1.setFont(new Font("Arial", Font.PLAIN, 12));

            JLabel lblTrib2 = new JLabel("💰 Moedas: " + moedas);
            lblTrib2.setFont(new Font("Arial", Font.PLAIN, 12));

            card.add(lblPos);
            card.add(lblVotos);
            card.add(lblTrib1);
            card.add(lblTrib2);

            painelCartoes.add(card);
        }

        painelCartoes.revalidate();
        painelCartoes.repaint();
    }

    private Color obterCorAwt(Cor cor) {
        if (cor == null) return Color.GRAY;
        switch (cor) {
            case AMARELO: return new Color(200, 160, 0);
            case AZUL: return new Color(30, 144, 255);
            case BRANCO: return new Color(180, 180, 180);
            case LARANJA: return new Color(255, 140, 0);
            case PRETO: return Color.BLACK;
            case VERDE: return new Color(34, 139, 34);
            case VERMELHO: return new Color(220, 20, 60);
            default: return Color.GRAY;
        }
    }
}
