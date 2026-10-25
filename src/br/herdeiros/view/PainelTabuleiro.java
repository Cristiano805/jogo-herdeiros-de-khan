package br.herdeiros.view;

import br.herdeiros.model.Cor;
import br.herdeiros.model.NomeCidade;
import br.herdeiros.model.Partida;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Painel responsável por renderizar o tabuleiro, cidades, tesouros e peões
 * OBRIGATORIAMENTE via Java2D (Graphics2D.drawImage), sem componentes JLabel/JButton.
 */
public class PainelTabuleiro extends JPanel {

    private Partida partida;
    private Image imagemTabuleiro;
    private Map<Cor, Image> imagensPeoes;
    private OuvinteCliqueTabuleiro ouvinteClique;

    public interface OuvinteCliqueTabuleiro {
        void aoClicarNoTabuleiro(int x, int y);
    }

    public PainelTabuleiro(Partida partida) {
        this.partida = partida;
        this.imagensPeoes = new HashMap<Cor, Image>();

        carregarImagens();

        // Tratamento de eventos de clique do mouse SEM expressao lambda (requisito formal)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ouvinteClique != null) {
                    ouvinteClique.aoClicarNoTabuleiro(e.getX(), e.getY());
                }
            }
        });
    }

    private void carregarImagens() {
        try {
            imagemTabuleiro = ImageIO.read(new File("img/tabuleiro.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do tabuleiro: " + e.getMessage());
        }

        carregarPeao(Cor.AMARELO, "img/peoes/peao_amarelo.png");
        carregarPeao(Cor.AZUL, "img/peoes/peao_azul.png");
        carregarPeao(Cor.BRANCO, "img/peoes/peao_branco.png");
        carregarPeao(Cor.LARANJA, "img/peoes/peao_laranja.png");
        carregarPeao(Cor.PRETO, "img/peoes/peao_preto.png");
        carregarPeao(Cor.VERDE, "img/peoes/peao_verde.png");
        carregarPeao(Cor.VERMELHO, "img/peoes/peao_vermelho.png");
    }

    private void carregarPeao(Cor cor, String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                imagensPeoes.put(cor, ImageIO.read(f));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem do peão " + cor + ": " + e.getMessage());
        }
    }

    public void setOuvinteClique(OuvinteCliqueTabuleiro ouvinte) {
        this.ouvinteClique = ouvinte;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int largura = getWidth();
        int altura = getHeight();

        // 1. Desenha o tabuleiro com Java2D
        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, largura, altura, this);
        }

        if (partida == null) {
            return;
        }

        // 2. Destaca cidades reveladas/disponíveis no tabuleiro com tesouros
        desenharCidadesDisponiveis(g2d, largura, altura);

        // 3. Desenha os peões dos jogadores usando Graphics2D.drawImage
        desenharPeoes(g2d, largura, altura);
    }

    private void desenharCidadesDisponiveis(Graphics2D g2d, int largura, int altura) {
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        for (NomeCidade nc : NomeCidade.values()) {
            if (partida.isCidadeDisponivel(nc)) {
                Point p = MapaCoordenadas.getCoordenadaTela(nc.getNomeFormatado(), largura, altura);
                if (p != null) {
                    int tesouros = partida.getTesourosCidade(nc);
                    g2d.setColor(new Color(255, 215, 0, 220));
                    g2d.fillRoundRect(p.x - 30, p.y + 20, 60, 20, 8, 8);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(p.x - 30, p.y + 20, 60, 20, 8, 8);
                    g2d.drawString("Tes: " + tesouros, p.x - 22, p.y + 35);
                }
            }
        }
    }

    private void desenharPeoes(Graphics2D g2d, int largura, int altura) {
        int tamanhoPeao = 36;
        int raioOffset = 18;

        int indice = 0;
        for (Cor cor : partida.getCoresJogadores()) {
            String loc = partida.getLocalizacaoJogador(cor);
            if (loc != null) {
                Point p = MapaCoordenadas.getCoordenadaTela(loc, largura, altura);
                if (p != null) {
                    // Distribui os peões ligeiramente para não ficarem sobrepostos na mesma casa
                    int offsetX = (int) (raioOffset * Math.cos(indice * (2 * Math.PI / 4)));
                    int offsetY = (int) (raioOffset * Math.sin(indice * (2 * Math.PI / 4)));

                    Image imgPeao = imagensPeoes.get(cor);
                    int xPeao = p.x + offsetX - (tamanhoPeao / 2);
                    int yPeao = p.y + offsetY - (tamanhoPeao / 2);

                    if (imgPeao != null) {
                        g2d.drawImage(imgPeao, xPeao, yPeao, tamanhoPeao, tamanhoPeao, this);
                    } else {
                        // Fallback em caso de imagem ausente
                        g2d.setColor(obterCorAwt(cor));
                        g2d.fillOval(xPeao, yPeao, tamanhoPeao, tamanhoPeao);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(xPeao, yPeao, tamanhoPeao, tamanhoPeao);
                    }
                }
            }
            indice++;
        }
    }

    private Color obterCorAwt(Cor cor) {
        if (cor == null) return Color.GRAY;
        switch (cor) {
            case AMARELO: return Color.YELLOW;
            case AZUL: return new Color(30, 144, 255);
            case BRANCO: return Color.WHITE;
            case LARANJA: return Color.ORANGE;
            case PRETO: return Color.BLACK;
            case VERDE: return new Color(34, 139, 34);
            case VERMELHO: return new Color(220, 20, 60);
            default: return Color.GRAY;
        }
    }
}