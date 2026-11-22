package br.herdeiros.observer;

/**
 * Protocolo de índices usado em ObservadoIF.get(int).
 * Como get devolve apenas int, todo estado é codificado como inteiro:
 *  - cores: ordinal da cor (nome obtido via Controller.getNomeCor);
 *  - localidades: índice no vetor LOCALIDADES (-1 = fora do tabuleiro);
 *  - cartas: "cartasP-N" codificada como P*100 + N;
 *  - booleanos: 1 (verdadeiro) / 0 (falso).
 */
public final class IndicesEstado {

    private IndicesEstado() {
    }

    /** Tabela de localidades (o índice é o código usado no protocolo). */
    public static final String[] LOCALIDADES = {
        "Karakorum", "Moscou", "Kazan", "Novgorod",
        "Pequim", "Cantão", "Xi'an",
        "Samarcanda", "Bagdá", "Tabriz",

        // 46 paradas/bolinhas mapeadas do mapa
        "RUSSIA_1", "RUSSIA_2", "RUSSIA_3", "RUSSIA_15", "RUSSIA_16",
        "RUSSIA_17", "RUSSIA_18", "RUSSIA_19", "RUSSIA_20", "RUSSIA_21",
        "RUSSIA_22", "RUSSIA_23", "RUSSIA_24",

        "PERSIA_25", "PERSIA_26", "PERSIA_27", "PERSIA_28", "PERSIA_29",
        "PERSIA_30", "PERSIA_31", "PERSIA_32", "PERSIA_33", "PERSIA_34",
        "PERSIA_35", "PERSIA_36", "PERSIA_37",

        "CHINA_38", "CHINA_39", "CHINA_40", "CHINA_41", "CHINA_42",
        "CHINA_43", "CHINA_44", "CHINA_45", "CHINA_46", "CHINA_47",
        "CHINA_48", "CHINA_49", "CHINA_52",

        "CENTRO_53",

        "CONSELHEIRO_55", "CONSELHEIRO_56", "CONSELHEIRO_57",
        "CONSELHEIRO_58", "CONSELHEIRO_59", "CONSELHEIRO_60"
    };

    // --- Estado global da partida ---
    public static final int QTD_JOGADORES = 0;
    public static final int JOGADOR_DA_VEZ = 1;        // índice do jogador (ordem de cadastro)
    public static final int MOVIMENTOS_RESTANTES = 2;
    public static final int EXIBIR_CARTELA = 3;        // 1 = janela da cartela deve estar visível
    public static final int EXIBIR_CARTAS = 4;         // 1 = janela de cartas deve estar visível
    public static final int EXIBIR_INVENTARIO = 5;     // 1 = janela de inventário deve estar visível
    public static final int LOCALIDADE_KHAN = 6;
    public static final int PARTIDA_ENCERRADA = 7;     // 1 = partida terminou

    // --- Estado por jogador: JOGADOR_BASE + j * JOGADOR_PASSO + campo ---
    public static final int JOGADOR_BASE = 100;
    public static final int JOGADOR_PASSO = 30;
    public static final int CAMPO_COR = 0;
    public static final int CAMPO_LOCALIDADE = 1;
    public static final int CAMPO_VOTOS = 2;
    public static final int CAMPO_ESPADAS = 3;
    public static final int CAMPO_YURTS = 4;
    public static final int CAMPO_MOEDAS = 5;
    public static final int CAMPO_DESCENDENTES = 6;
    public static final int CAMPO_QTD_CARTAS = 7;
    public static final int CAMPO_CARTA_0 = 8;          // cartas k em CAMPO_CARTA_0 + k
    public static final int MAX_CARTAS = 20;
    public static final int CAMPO_CONSELHEIRO = 28;     // código da carta de conselheiro (-1 = não escolhido)
    public static final int CAMPO_SECRETA = 29;         // código da carta secreta (-1 = não escolhida)

    // --- Estado por cidade: CIDADE_BASE + ordinalCidade * CIDADE_PASSO + campo ---
    public static final int CIDADE_BASE = 1000;
    public static final int CIDADE_PASSO = 4;
    public static final int CAMPO_TESOUROS = 0;         // -1 = cidade não disponível no tabuleiro
    public static final int CAMPO_LOCALIDADE_CIDADE = 1;

    // --- Yurts: YURT_BASE + indiceLocalidade -> ordinal da cor do dono (-1 = sem yurt) ---
    public static final int YURT_BASE = 2000;

    public static int jogador(int indiceJogador, int campo) {
        return JOGADOR_BASE + indiceJogador * JOGADOR_PASSO + campo;
    }

    public static int cidade(int ordinalCidade, int campo) {
        return CIDADE_BASE + ordinalCidade * CIDADE_PASSO + campo;
    }

    public static int yurt(int indiceLocalidade) {
        return YURT_BASE + indiceLocalidade;
    }

    public static int indiceLocalidade(String nome) {
        if (nome == null) return -1;
        for (int i = 0; i < LOCALIDADES.length; i++) {
            if (LOCALIDADES[i].equalsIgnoreCase(nome)) return i;
        }
        return -1;
    }

    public static String nomeLocalidade(int indice) {
        return (indice >= 0 && indice < LOCALIDADES.length) ? LOCALIDADES[indice] : null;
    }

    public static int codificarCarta(String idCarta) {
        // formato "cartasP-N"
        try {
            String semPrefixo = idCarta.substring("cartas".length());
            String[] partes = semPrefixo.split("-");
            return Integer.parseInt(partes[0]) * 100 + Integer.parseInt(partes[1]);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static String decodificarCarta(int codigo) {
        return "cartas" + (codigo / 100) + "-" + (codigo % 100);
    }
}

