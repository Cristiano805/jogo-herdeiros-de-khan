package br.herdeiros.view;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapeia as posições relativas e coordenadas das localidades presentes no Model (Karakorum e as 9 Cidades).
 * Suporta normalização com base na largura e altura reais do componente na tela.
 */
public class MapaCoordenadas {

    private static final Map<String, Point> COORDENADAS_BASE = new HashMap<String, Point>();
    // Dimensão de referência da imagem base (1923 x 1930)
    private static final double LARGURA_BASE = 1923.0;
    private static final double ALTURA_BASE = 1930.0;
    private static final int RAIO_CLIQUE = 70;

    static {
        // Coordenadas aproximadas dos centros no mapa original
        COORDENADAS_BASE.put("Karakorum", new Point(955, 875));

        // Região da Rússia
        COORDENADAS_BASE.put("Moscou", new Point(715, 430));
        COORDENADAS_BASE.put("Kazan", new Point(1160, 310));
        COORDENADAS_BASE.put("Novgorod", new Point(220, 275));

        // Região da China
        COORDENADAS_BASE.put("Pequim", new Point(1415, 985));
        COORDENADAS_BASE.put("Cantão", new Point(1645, 1515));
        COORDENADAS_BASE.put("Xi'an", new Point(1715, 575));

        // Região da Pérsia
        COORDENADAS_BASE.put("Samarcanda", new Point(535, 930));
        COORDENADAS_BASE.put("Bagdá", new Point(210, 1125));
        COORDENADAS_BASE.put("Tabriz", new Point(845, 1375));
    }

    public static Point getCoordenadaTela(String localizacao, int larguraPainel, int alturaPainel) {
        Point pBase = COORDENADAS_BASE.get(localizacao);
        if (pBase == null) {
            return null;
        }
        int x = (int) Math.round((pBase.x / LARGURA_BASE) * larguraPainel);
        int y = (int) Math.round((pBase.y / ALTURA_BASE) * alturaPainel);
        return new Point(x, y);
    }

    public static String identificarLocalizacaoClicada(int xClique, int yClique, int larguraPainel, int alturaPainel) {
        if (larguraPainel <= 0 || alturaPainel <= 0) {
            return null;
        }
        double escalaX = larguraPainel / LARGURA_BASE;
        double escalaY = alturaPainel / ALTURA_BASE;
        double raioEscalado = RAIO_CLIQUE * Math.min(escalaX, escalaY);

        for (Map.Entry<String, Point> entry : COORDENADAS_BASE.entrySet()) {
            Point pTela = getCoordenadaTela(entry.getKey(), larguraPainel, alturaPainel);
            if (pTela != null) {
                double distancia = pTela.distance(xClique, yClique);
                if (distancia <= raioEscalado) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
