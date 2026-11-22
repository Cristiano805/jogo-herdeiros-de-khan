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
        COORDENADAS_BASE.put("Karakorum", new Point(940, 842));
        COORDENADAS_BASE.put("CENTRO_53", new Point(940, 842));

        // Região da Rússia (Cidades)
        COORDENADAS_BASE.put("Moscou", new Point(715, 430));
        COORDENADAS_BASE.put("Kazan", new Point(1160, 310));
        COORDENADAS_BASE.put("Novgorod", new Point(220, 275));

        // Região da China (Cidades)
        COORDENADAS_BASE.put("Pequim", new Point(1415, 985));
        COORDENADAS_BASE.put("Cantão", new Point(1645, 1515));
        COORDENADAS_BASE.put("Xi'an", new Point(1715, 575));

        // Região da Pérsia (Cidades)
        COORDENADAS_BASE.put("Samarcanda", new Point(535, 930));
        COORDENADAS_BASE.put("Bagdá", new Point(210, 1125));
        COORDENADAS_BASE.put("Tabriz", new Point(845, 1375));

        // --- 46 PARADAS / BOLINHAS MAPEADAS DO TABULEIRO ---
        COORDENADAS_BASE.put("RUSSIA_1", new Point(1162, 138));
        COORDENADAS_BASE.put("RUSSIA_2", new Point(1249, 412));
        COORDENADAS_BASE.put("RUSSIA_3", new Point(1462, 303));
        COORDENADAS_BASE.put("RUSSIA_15", new Point(1520, 325));
        COORDENADAS_BASE.put("RUSSIA_16", new Point(1022, 448));
        COORDENADAS_BASE.put("RUSSIA_17", new Point(832, 308));
        COORDENADAS_BASE.put("RUSSIA_18", new Point(706, 553));
        COORDENADAS_BASE.put("RUSSIA_19", new Point(578, 379));
        COORDENADAS_BASE.put("RUSSIA_20", new Point(619, 167));
        COORDENADAS_BASE.put("RUSSIA_21", new Point(571, 111));
        COORDENADAS_BASE.put("RUSSIA_22", new Point(325, 169));
        COORDENADAS_BASE.put("RUSSIA_23", new Point(85, 352));
        COORDENADAS_BASE.put("RUSSIA_24", new Point(330, 352));

        COORDENADAS_BASE.put("PERSIA_25", new Point(316, 548));
        COORDENADAS_BASE.put("PERSIA_26", new Point(289, 628));
        COORDENADAS_BASE.put("PERSIA_27", new Point(557, 787));
        COORDENADAS_BASE.put("PERSIA_28", new Point(633, 1023));
        COORDENADAS_BASE.put("PERSIA_29", new Point(848, 1203));
        COORDENADAS_BASE.put("PERSIA_30", new Point(979, 1342));
        COORDENADAS_BASE.put("PERSIA_31", new Point(805, 1480));
        COORDENADAS_BASE.put("PERSIA_32", new Point(523, 1257));
        COORDENADAS_BASE.put("PERSIA_33", new Point(429, 1246));
        COORDENADAS_BASE.put("PERSIA_34", new Point(380, 914));
        COORDENADAS_BASE.put("PERSIA_35", new Point(138, 972));
        COORDENADAS_BASE.put("PERSIA_36", new Point(114, 1241));
        COORDENADAS_BASE.put("PERSIA_37", new Point(307, 1096));

        COORDENADAS_BASE.put("CHINA_38", new Point(1252, 1068));
        COORDENADAS_BASE.put("CHINA_39", new Point(1464, 1094));
        COORDENADAS_BASE.put("CHINA_40", new Point(1176, 1471));
        COORDENADAS_BASE.put("CHINA_41", new Point(1235, 1484));
        COORDENADAS_BASE.put("CHINA_42", new Point(1480, 1473));
        COORDENADAS_BASE.put("CHINA_43", new Point(1715, 1585));
        COORDENADAS_BASE.put("CHINA_44", new Point(1669, 1322));
        COORDENADAS_BASE.put("CHINA_45", new Point(1685, 1065));
        COORDENADAS_BASE.put("CHINA_46", new Point(1698, 961));
        COORDENADAS_BASE.put("CHINA_47", new Point(1664, 691));
        COORDENADAS_BASE.put("CHINA_48", new Point(1636, 421));
        COORDENADAS_BASE.put("CHINA_49", new Point(1834, 573));
        COORDENADAS_BASE.put("CHINA_52", new Point(1423, 811));

        COORDENADAS_BASE.put("CONSELHEIRO_55", new Point(1841, 1119));
        COORDENADAS_BASE.put("CONSELHEIRO_56", new Point(1118, 1576));
        COORDENADAS_BASE.put("CONSELHEIRO_57", new Point(147, 678));
        COORDENADAS_BASE.put("CONSELHEIRO_58", new Point(1591, 192));
        COORDENADAS_BASE.put("CONSELHEIRO_59", new Point(919, 100));
        COORDENADAS_BASE.put("CONSELHEIRO_60", new Point(474, 1391));
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

        String maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Map.Entry<String, Point> entry : COORDENADAS_BASE.entrySet()) {
            Point pTela = getCoordenadaTela(entry.getKey(), larguraPainel, alturaPainel);
            if (pTela != null) {
                double distancia = pTela.distance(xClique, yClique);
                if (distancia <= raioEscalado && distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProximo = entry.getKey();
                }
            }
        }
        return maisProximo;
    }
}
