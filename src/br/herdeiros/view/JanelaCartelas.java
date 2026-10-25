package br.herdeiros.view;

import br.herdeiros.model.Cor;
import br.herdeiros.model.Partida;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Janela para exibição estrita do tabuleiro individual (cartela) do jogador da vez.
 * Cada jogador só tem acesso à visualização da sua própria cartela no seu turno.
 * Desenhada com Graphics2D.drawImage() via Java2D.
 */
public class JanelaCartelas extends JFrame {

    private Partida partida;
    private Map<String, Image> cacheCartelasPorNome;
    private Map<Cor, Image> cacheCartelasPorCor;
    private JLabel lblJogador;
    private PainelExibicaoCartela painelExibicao;

    public JanelaCartelas(Partida partida) {
        this.partida = partida;
        this.cacheCartelasPorNome = new HashMap<String, Image>();
        this.cacheCartelasPorCor = new HashMap<Cor, Image>();

        setTitle("Herdeiros de Khan - Meu Tabuleiro (Cartela)");
        setSize(950, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        carregarCartelas();

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(240, 240, 245));
        painelPrincipal.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Barra Superior: Nome do jogador da vez e botão fechar
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);

        lblJogador = new JLabel("Cartela do Jogador da Vez", JLabel.LEFT);
        lblJogador.setFont(new Font("Arial", Font.BOLD, 15));
        lblJogador.setForeground(new Color(40, 40, 50));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(new Font("Arial", Font.BOLD, 12));
        btnFechar.setBackground(new Color(127, 140, 141));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JanelaCartelas.this.dispose();
            }
        });

        painelTopo.add(lblJogador, BorderLayout.WEST);
        painelTopo.add(btnFechar, BorderLayout.EAST);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        painelExibicao = new PainelExibicaoCartela();
        painelPrincipal.add(painelExibicao, BorderLayout.CENTER);

        add(painelPrincipal);
        sincronizarComJogadorDaVez();
    }

    private void carregarCartelas() {
        carregarPorNomeECor("altani", Cor.VERMELHO, "img/cartelas/cartela_altani.jpg");
        carregarPorNomeECor("chagatai", Cor.AZUL, "img/cartelas/cartela_chagatai.jpg");
        carregarPorNomeECor("jochi", Cor.VERDE, "img/cartelas/cartela_jochi.jpg");
        carregarPorNomeECor("ogedei", Cor.AMARELO, "img/cartelas/cartela_ogedei.jpg");
        carregarPorNomeECor("tolui", Cor.LARANJA, "img/cartelas/cartela_tolui.jpg");
    }

    private void carregarPorNomeECor(String nomeHerdeiro, Cor cor, String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                Image img = ImageIO.read(f);
                cacheCartelasPorNome.put(nomeHerdeiro.toLowerCase(), img);
                if (cor != null) {
                    cacheCartelasPorCor.put(cor, img);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar cartela " + caminho + ": " + e.getMessage());
        }
    }

    public void sincronizarComJogadorDaVez() {
        if (partida == null) return;
        Cor corVez = partida.getCorJogadorAtual();
        String nome = partida.getNomeJogadorAtual();
        lblJogador.setText("Tabuleiro / Cartela de: " + nome + " (" + corVez + ")");
        painelExibicao.setJogadorAtual(nome, corVez);
    }

    private class PainelExibicaoCartela extends JPanel {
        private String nomeHerdeiro;
        private Cor corAtual;

        public PainelExibicaoCartela() {
            setBackground(new Color(230, 230, 235));
        }

        public void setJogadorAtual(String nomeHerdeiro, Cor cor) {
            this.nomeHerdeiro = nomeHerdeiro;
            this.corAtual = cor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Image img = null;
            if (nomeHerdeiro != null) {
                img = cacheCartelasPorNome.get(nomeHerdeiro.toLowerCase().trim());
            }
            if (img == null && corAtual != null) {
                img = cacheCartelasPorCor.get(corAtual);
            }

            if (img != null) {
                int painelW = getWidth();
                int painelH = getHeight();
                int imgW = img.getWidth(this);
                int imgH = img.getHeight(this);

                double ratio = Math.min((double) painelW / imgW, (double) painelH / imgH);
                int targetW = (int) (imgW * ratio);
                int targetH = (int) (imgH * ratio);
                int x = (painelW - targetW) / 2;
                int y = (painelH - targetH) / 2;

                g2d.drawImage(img, x, y, targetW, targetH, this);
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("Cartela não encontrada para o herdeiro " + nomeHerdeiro, 30, 50);
            }
        }
    }
}
