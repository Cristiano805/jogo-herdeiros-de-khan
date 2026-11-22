package br.herdeiros.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Ferramenta visual para calibração e mapeamento das bolinhas (paradas) do mapa.
 * Para rodar: clique com botão direito no Eclipse -> Run As -> Java Application.
 */
public class CalibradorMapa extends JFrame {

    private static final double LARGURA_BASE = 1923.0;
    private static final double ALTURA_BASE = 1930.0;

    private static class PontoMapeado {
        String id;
        int xBase;
        int yBase;
        Point tela;

        PontoMapeado(String id, int xBase, int yBase, Point tela) {
            this.id = id;
            this.xBase = xBase;
            this.yBase = yBase;
            this.tela = tela;
        }
    }

    private Image imgTabuleiro;
    private List<PontoMapeado> pontos = new ArrayList<PontoMapeado>();
    private JComboBox<String> comboRegiao;
    private int contadorPontos = 1;
    private PainelMapa painelMapa;

    public CalibradorMapa() {
        setTitle("Calibrador de Bolinhas do Tabuleiro - Herdeiros de Khan");
        setSize(1100, 950);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        carregarImagem();

        // Barra Superior de Controles
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelControles.setBackground(new Color(230, 230, 235));

        JLabel lblInfo = new JLabel("Região do Ponto:");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 13));

        String[] regioes = {"RUSSIA", "CHINA", "PERSIA", "CENTRO", "CONSELHEIRO"};
        comboRegiao = new JComboBox<String>(regioes);
        comboRegiao.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnDesfazer = new JButton("Desfazer Último Clique");
        btnDesfazer.setFont(new Font("Arial", Font.BOLD, 12));
        btnDesfazer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desfazerUltimo();
            }
        });

        JButton btnCopiar = new JButton("Copiar Código para Área de Transferência");
        btnCopiar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCopiar.setBackground(new Color(39, 174, 96));
        btnCopiar.setForeground(Color.WHITE);
        btnCopiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copiarCodigo();
            }
        });

        painelControles.add(lblInfo);
        painelControles.add(comboRegiao);
        painelControles.add(btnDesfazer);
        painelControles.add(btnCopiar);
        add(painelControles, BorderLayout.NORTH);

        // Painel Central com a Imagem do Tabuleiro
        painelMapa = new PainelMapa();
        add(painelMapa, BorderLayout.CENTER);
    }

    private void carregarImagem() {
        try {
            File f = new File("img/tabuleiro.jpg");
            if (f.exists()) {
                imgTabuleiro = ImageIO.read(f);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar tabuleiro.jpg: " + e.getMessage());
        }
    }

    private void desfazerUltimo() {
        if (!pontos.isEmpty()) {
            PontoMapeado removido = pontos.remove(pontos.size() - 1);
            System.out.println("// [DESFEITO] " + removido.id);
            painelMapa.repaint();
        }
    }

    private void copiarCodigo() {
        if (pontos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum ponto clicado ainda! Clique nas bolinhas do mapa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("// --- COORDENADAS DAS BOLINHAS MAPEADAS ---\n");
        for (PontoMapeado p : pontos) {
            sb.append("COORDENADAS_BASE.put(\"").append(p.id).append("\", new Point(")
              .append(p.xBase).append(", ").append(p.yBase).append("));\n");
        }

        String codigo = sb.toString();
        // Copia para área de transferência do Windows
        StringSelection sel = new StringSelection(codigo);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);

        // Também salva em arquivo de backup
        try (FileWriter fw = new FileWriter("pontos_mapeados.txt")) {
            fw.write(codigo);
        } catch (IOException ex) {}

        JOptionPane.showMessageDialog(this,
                "Código de " + pontos.size() + " pontos copiado para a Área de Transferência!\n"
                + "Também foi salvo no arquivo 'pontos_mapeados.txt'.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private class PainelMapa extends JPanel {

        public PainelMapa() {
            setBackground(Color.DARK_GRAY);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    adicionarPonto(e.getX(), e.getY());
                }
            });
        }

        private void adicionarPonto(int mouseX, int mouseY) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            int xBase = (int) Math.round((mouseX / (double) w) * LARGURA_BASE);
            int yBase = (int) Math.round((mouseY / (double) h) * ALTURA_BASE);

            String reg = (String) comboRegiao.getSelectedItem();
            String id = reg + "_" + contadorPontos++;

            PontoMapeado p = new PontoMapeado(id, xBase, yBase, new Point(mouseX, mouseY));
            pontos.add(p);

            String linhaCodigo = "COORDENADAS_BASE.put(\"" + id + "\", new Point(" + xBase + ", " + yBase + "));";
            System.out.println(linhaCodigo);

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            if (imgTabuleiro != null) {
                g2d.drawImage(imgTabuleiro, 0, 0, w, h, this);
            }

            // Desenha os pontos já clicados
            int raio = 10;
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            for (PontoMapeado p : pontos) {
                // Converte a base de volta para a coordenada atual da tela
                int px = (int) Math.round((p.xBase / LARGURA_BASE) * w);
                int py = (int) Math.round((p.yBase / ALTURA_BASE) * h);

                // Círculo verde com borda vermelha
                g2d.setColor(new Color(46, 204, 113, 220));
                g2d.fillOval(px - raio, py - raio, raio * 2, raio * 2);
                g2d.setColor(new Color(192, 57, 43));
                g2d.drawOval(px - raio, py - raio, raio * 2, raio * 2);

                // Etiqueta com o nome do ponto
                g2d.setColor(Color.WHITE);
                g2d.fillRect(px + raio + 2, py - 8, g2d.getFontMetrics().stringWidth(p.id) + 4, 14);
                g2d.setColor(Color.BLACK);
                g2d.drawString(p.id, px + raio + 4, py + 4);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalibradorMapa().setVisible(true);
            }
        });
    }
}
