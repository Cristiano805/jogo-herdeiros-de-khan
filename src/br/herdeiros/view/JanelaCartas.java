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
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Janela própria para exibição das cartas secretas em posse do jogador da vez (Figura 5).
 * Desenho OBRIGATORIAMENTE via Java2D (Graphics2D.drawImage).
 * Sem lambdas em listeners.
 */
public class JanelaCartas extends JFrame {

    private Partida partida;
    private JLabel lblJogador;
    private PainelExibicaoCartas painelExibicao;
    private static final Map<String, Image> CACHE_CARTAS = new HashMap<String, Image>();

    public JanelaCartas(Partida partida) {
        this.partida = partida;

        setTitle("Herdeiros de Khan - Minhas Cartas (Conselheiros)");
        setSize(860, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Topo com nome do jogador da vez e botão fechar
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);

        lblJogador = new JLabel("Cartas do Jogador da Vez", JLabel.LEFT);
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
                JanelaCartas.this.dispose();
            }
        });

        painelTopo.add(lblJogador, BorderLayout.WEST);
        painelTopo.add(btnFechar, BorderLayout.EAST);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        painelExibicao = new PainelExibicaoCartas();
        painelPrincipal.add(painelExibicao, BorderLayout.CENTER);

        add(painelPrincipal);
        sincronizarComJogadorDaVez();
    }

    public void sincronizarComJogadorDaVez() {
        if (partida == null) return;
        Cor corVez = partida.getCorJogadorAtual();
        String nome = partida.getNomeJogadorAtual();
        lblJogador.setText("Cartas Secretas de: " + nome + " (" + corVez + ")");
        painelExibicao.atualizar(partida.getCartasJogadorAtual());
    }

    private Image carregarImagemCarta(String idCarta) {
        if (idCarta == null) return null;
        if (CACHE_CARTAS.containsKey(idCarta)) {
            return CACHE_CARTAS.get(idCarta);
        }

        // Tenta resolver caminho baseado no id, ex: cartas3-1 para img/cartas/cartas3/cartas3-1.jpg
        String pasta = idCarta.contains("-") ? idCarta.substring(0, idCarta.indexOf("-")) : "cartas1";
        String caminho = "img/cartas/" + pasta + "/" + idCarta + ".jpg";

        File f = new File(caminho);
        if (!f.exists()) {
            caminho = "img/cartas/" + idCarta + ".jpg";
            f = new File(caminho);
        }

        if (f.exists()) {
            try {
                Image img = ImageIO.read(f);
                CACHE_CARTAS.put(idCarta, img);
                return img;
            } catch (IOException e) {
                System.err.println("Erro ao carregar imagem da carta " + idCarta + ": " + e.getMessage());
            }
        }
        return null;
    }

    private class PainelExibicaoCartas extends JPanel {
        private List<String> cartasAtuais;

        public PainelExibicaoCartas() {
            setBackground(new Color(235, 235, 240));
        }

        public void atualizar(List<String> cartas) {
            this.cartasAtuais = cartas;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (cartasAtuais == null || cartasAtuais.isEmpty()) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 15));
                g2d.drawString("O jogador não possui cartas no momento.", 40, 60);
                return;
            }

            int painelW = getWidth();
            int painelH = getHeight();
            int totalCartas = cartasAtuais.size();

            int espacamento = 24;
            int margemSuperior = 20;
            int larguraDisponivel = painelW - ((totalCartas + 1) * espacamento);
            int larguraCarta = Math.min(260, larguraDisponivel / totalCartas);
            int alturaCarta = (int) (larguraCarta * 1.45);

            if (alturaCarta > painelH - 80) {
                alturaCarta = painelH - 80;
                larguraCarta = (int) (alturaCarta / 1.45);
            }

            int larguraTotalBloco = (totalCartas * larguraCarta) + ((totalCartas - 1) * espacamento);
            int inicioX = (painelW - larguraTotalBloco) / 2;
            int posY = margemSuperior + (painelH - margemSuperior - alturaCarta) / 2;

            for (int i = 0; i < totalCartas; i++) {
                String idCarta = cartasAtuais.get(i);
                Image img = carregarImagemCarta(idCarta);
                int posX = inicioX + (i * (larguraCarta + espacamento));

                // Borda e sombra de fundo da carta
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(posX + 4, posY + 4, larguraCarta, alturaCarta, 16, 16);

                if (img != null) {
                    g2d.drawImage(img, posX, posY, larguraCarta, alturaCarta, this);
                } else {
                    g2d.setColor(new Color(220, 220, 225));
                    g2d.fillRoundRect(posX, posY, larguraCarta, alturaCarta, 16, 16);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 13));
                    g2d.drawString("Carta: " + idCarta, posX + 20, posY + alturaCarta / 2);
                }

                g2d.setColor(new Color(60, 60, 70));
                g2d.drawRoundRect(posX, posY, larguraCarta, alturaCarta, 16, 16);
            }
        }
    }
}
