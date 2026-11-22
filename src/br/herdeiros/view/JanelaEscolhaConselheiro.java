package br.herdeiros.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Diálogo modal para escolha visual entre as 2 cartas iniciais:
 * o jogador escolhe qual será o seu CONSELHEIRO e a outra se torna a CARTA SECRETA.
 * Exibe a imagem real das cartas carregadas da pasta img/cartas/.
 * SEM uso de expressões lambda em listeners (regra da disciplina).
 */
public class JanelaEscolhaConselheiro extends JDialog {

    private int escolhaSelecionada = 0;

    public JanelaEscolhaConselheiro(Frame parent, String nomeJogador, String corJogador, final List<String> cartas) {
        super(parent, "Herdeiros de Khan - Escolha do Conselheiro e Carta Secreta", true);
        setSize(780, 580);
        setResizable(false);
        setLocationRelativeTo(parent);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Cabeçalho
        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.setOpaque(false);

        Color corAwt = CoresView.obterCorAwt(corJogador);
        JLabel lblTitulo = new JLabel("Vez de: " + nomeJogador + " (" + corJogador + ")", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(corAwt);

        JLabel lblSubtitulo = new JLabel(
                "<html><center>Você recebeu 2 cartas de conselheiro secreto.<br>"
                + "Clique no botão abaixo da carta que você deseja manter como seu <b>CONSELHEIRO</b>.<br>"
                + "A outra carta permanecerá guardada como sua <b>CARTA SECRETA</b> para a pontuação final.</center></html>",
                JLabel.CENTER
        );
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(60, 60, 70));

        painelTopo.add(lblTitulo, BorderLayout.NORTH);
        painelTopo.add(lblSubtitulo, BorderLayout.SOUTH);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        // Painel Central com as 2 cartas lado a lado
        JPanel painelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        painelCartas.setOpaque(false);

        for (int i = 0; i < Math.min(2, cartas.size()); i++) {
            final int indiceCarta = i;
            String idCarta = cartas.get(i);
            Image imgCarta = carregarImagemCarta(idCarta);

            JPanel cardPainel = new JPanel(new BorderLayout(10, 10));
            cardPainel.setBackground(Color.WHITE);
            cardPainel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 220), 1),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            PainelCartaVisual visual = new PainelCartaVisual(imgCarta, idCarta);
            visual.setPreferredSize(new Dimension(240, 320));

            JButton btnEscolher = new JButton("Manter como Conselheiro");
            btnEscolher.setFont(new Font("Arial", Font.BOLD, 13));
            btnEscolher.setBackground(new Color(39, 174, 96));
            btnEscolher.setForeground(Color.WHITE);
            btnEscolher.setFocusPainted(false);
            btnEscolher.setPreferredSize(new Dimension(240, 38));

            btnEscolher.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    escolhaSelecionada = indiceCarta;
                    dispose();
                }
            });

            cardPainel.add(visual, BorderLayout.CENTER);
            cardPainel.add(btnEscolher, BorderLayout.SOUTH);
            painelCartas.add(cardPainel);
        }

        painelPrincipal.add(painelCartas, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    public static int escolher(Frame parent, String nomeJogador, String corJogador, List<String> cartas) {
        JanelaEscolhaConselheiro dialog = new JanelaEscolhaConselheiro(parent, nomeJogador, corJogador, cartas);
        dialog.setVisible(true);
        return dialog.escolhaSelecionada;
    }

    private static Image carregarImagemCarta(String idCarta) {
        if (idCarta == null || idCarta.isEmpty()) {
            return null;
        }
        try {
            String semPrefixo = idCarta.substring("cartas".length());
            String[] partes = semPrefixo.split("-");
            String pasta = "cartas" + partes[0];
            String caminho = "img/cartas/" + pasta + "/" + idCarta + ".jpg";
            File f = new File(caminho);
            if (f.exists()) {
                return ImageIO.read(f);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar carta " + idCarta + ": " + e.getMessage());
        }
        return null;
    }

    private static class PainelCartaVisual extends JPanel {
        private Image imagem;
        private String idCarta;

        public PainelCartaVisual(Image imagem, String idCarta) {
            this.imagem = imagem;
            this.idCarta = idCarta;
            setBackground(new Color(235, 235, 240));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (imagem != null) {
                int pw = getWidth();
                int ph = getHeight();
                int iw = imagem.getWidth(this);
                int ih = imagem.getHeight(this);
                double scale = Math.min((double) pw / iw, (double) ph / ih);
                int tw = (int) (iw * scale);
                int th = (int) (ih * scale);
                int x = (pw - tw) / 2;
                int y = (ph - th) / 2;
                g2d.drawImage(imagem, x, y, tw, th, this);
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("Carta: " + idCarta, 20, getHeight() / 2);
            }
        }
    }
}
