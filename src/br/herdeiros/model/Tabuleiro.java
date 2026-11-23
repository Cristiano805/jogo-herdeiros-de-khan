package br.herdeiros.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Tabuleiro implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Peao peaoDoKhan;
    private Map<NomeCidade, Cidade> cidadesDisponiveis; 
    private Map<String, Set<String>> rotas;
    private Map<String, Cor> yurts;

    public Tabuleiro(List<Cidade> cidadesIniciaisSorteadas) {
        // O peão branco representa o Khan e começa ao lado do tabuleiro
        this.peaoDoKhan = new Peao(Cor.BRANCO, "Fora do Tabuleiro");
        
        this.cidadesDisponiveis = new HashMap<>();
        if (cidadesIniciaisSorteadas != null) {
            for (Cidade cidade : cidadesIniciaisSorteadas) {
                this.cidadesDisponiveis.put(cidade.getNomeCidade(), cidade);
            }
        }

        this.rotas = new HashMap<>();
        this.yurts = new HashMap<>();
        inicializarGrafoRotas();
    }
    
    private void inicializarGrafoRotas() {
        // Capital central
        String karakorum = "Karakorum";
        
        // Rússia
        String moscou = NomeCidade.MOSCOU.getNomeFormatado();
        String kazan = NomeCidade.KAZAN.getNomeFormatado();
        String novgorod = NomeCidade.NOVGOROD.getNomeFormatado();

        // China
        String pequim = NomeCidade.PEQUIM.getNomeFormatado();
        String cantao = NomeCidade.CANTAO.getNomeFormatado();
        String xian = NomeCidade.XIAN.getNomeFormatado();

        // Pérsia
        String samarcanda = NomeCidade.SAMARCANDA.getNomeFormatado();
        String bagda = NomeCidade.BAGDA.getNomeFormatado();
        String tabriz = NomeCidade.TABRIZ.getNomeFormatado();

        // Conexões da Capital Karakorum com os principais polos de rota das regiões
        conectar(karakorum, kazan);
        conectar(karakorum, xian);
        conectar(karakorum, samarcanda);
        conectar(karakorum, moscou);
        conectar(karakorum, pequim);
        conectar(karakorum, tabriz);

        // Rotas internas da Rússia
        conectar(moscou, kazan);
        conectar(moscou, novgorod);
        conectar(kazan, novgorod);

        // Rotas internas da China
        conectar(pequim, xian);
        conectar(pequim, cantao);
        conectar(xian, cantao);

        // Rotas internas da Pérsia
        conectar(samarcanda, bagda);
        conectar(samarcanda, tabriz);
        conectar(bagda, tabriz);

        // Rotas fronteiriças entre regiões
        conectar(kazan, samarcanda);
        conectar(xian, samarcanda);
        conectar(cantao, tabriz);

        // --- 46 PARADAS / BOLINHAS DO TABULEIRO ---
        conectar(karakorum, "CENTRO_53");
        conectar(karakorum, "RUSSIA_18");
        conectar(karakorum, "RUSSIA_16");
        conectar(karakorum, "PERSIA_27");
        conectar(karakorum, "CHINA_38");
        conectar(karakorum, "CHINA_52");
        conectar("CENTRO_53", "RUSSIA_18");
        conectar("CENTRO_53", "RUSSIA_16");
        conectar("CENTRO_53", "PERSIA_27");
        conectar("CENTRO_53", "CHINA_38");
        conectar("CENTRO_53", "CHINA_52");

        // Malha da Rússia
        conectar("RUSSIA_16", "RUSSIA_2");
        conectar("RUSSIA_2", "RUSSIA_1");
        conectar("RUSSIA_1", "CONSELHEIRO_59");
        conectar("CONSELHEIRO_59", "RUSSIA_17");
        conectar("RUSSIA_17", "RUSSIA_16");
        conectar("RUSSIA_17", "RUSSIA_18");
        conectar("RUSSIA_18", "RUSSIA_19");
        conectar("RUSSIA_19", "RUSSIA_17");
        conectar("RUSSIA_2", "RUSSIA_3");
        conectar("RUSSIA_3", "RUSSIA_15");
        conectar("RUSSIA_15", "CONSELHEIRO_58");
        conectar("CONSELHEIRO_58", "CHINA_48");
        conectar("RUSSIA_20", "RUSSIA_21");
        conectar("RUSSIA_21", "RUSSIA_22");
        conectar("RUSSIA_22", "RUSSIA_24");
        conectar("RUSSIA_24", "RUSSIA_23");
        conectar("RUSSIA_24", "RUSSIA_19");
        conectar("RUSSIA_20", "RUSSIA_17");
        conectar("RUSSIA_24", "PERSIA_25");

        // Malha da Pérsia
        conectar("PERSIA_27", "PERSIA_34");
        conectar("PERSIA_34", "PERSIA_26");
        conectar("PERSIA_26", "PERSIA_25");
        conectar("PERSIA_25", "CONSELHEIRO_57");
        conectar("PERSIA_27", "PERSIA_28");
        conectar("PERSIA_28", "PERSIA_29");
        conectar("PERSIA_29", "PERSIA_30");
        conectar("PERSIA_30", "CONSELHEIRO_56");
        conectar("CONSELHEIRO_56", "CHINA_40");
        conectar("PERSIA_29", "PERSIA_31");
        conectar("PERSIA_30", "PERSIA_31");
        conectar("PERSIA_28", "PERSIA_32");
        conectar("PERSIA_32", "PERSIA_33");
        conectar("PERSIA_33", "CONSELHEIRO_60");
        conectar("PERSIA_32", "CONSELHEIRO_60");
        conectar("PERSIA_34", "PERSIA_35");
        conectar("PERSIA_35", "PERSIA_36");
        conectar("PERSIA_36", "PERSIA_37");
        conectar("PERSIA_37", "PERSIA_33");
        conectar("PERSIA_37", "PERSIA_34");

        // Malha da China
        conectar("CHINA_38", "CHINA_39");
        conectar("CHINA_39", "CHINA_52");
        conectar("CHINA_39", "CHINA_45");
        conectar("CHINA_45", "CHINA_46");
        conectar("CHINA_46", "CONSELHEIRO_55");
        conectar("CHINA_45", "CONSELHEIRO_55");
        conectar("CHINA_46", "CHINA_47");
        conectar("CHINA_47", "CHINA_48");
        conectar("CHINA_47", "CHINA_49");
        conectar("CHINA_48", "CHINA_49");
        conectar("CHINA_39", "CHINA_44");
        conectar("CHINA_44", "CHINA_42");
        conectar("CHINA_42", "CHINA_41");
        conectar("CHINA_41", "CHINA_40");
        conectar("CHINA_44", "CHINA_43");
        conectar("CHINA_43", "CHINA_42");

        // Conectar paradas às cidades adjacentes
        conectar(moscou, "RUSSIA_17");
        conectar(moscou, "RUSSIA_19");
        conectar(moscou, "RUSSIA_16");
        conectar(kazan, "RUSSIA_2");
        conectar(kazan, "RUSSIA_16");
        conectar(kazan, "RUSSIA_1");
        conectar(novgorod, "RUSSIA_22");
        conectar(novgorod, "RUSSIA_23");
        conectar(novgorod, "RUSSIA_24");

        conectar(pequim, "CHINA_39");
        conectar(pequim, "CHINA_45");
        conectar(pequim, "CHINA_46");
        conectar(pequim, "CHINA_52");
        conectar(cantao, "CHINA_43");
        conectar(cantao, "CHINA_44");
        conectar(cantao, "CHINA_42");
        conectar(xian, "CHINA_47");
        conectar(xian, "CHINA_48");
        conectar(xian, "CHINA_49");

        conectar(samarcanda, "PERSIA_27");
        conectar(samarcanda, "PERSIA_28");
        conectar(samarcanda, "PERSIA_34");
        conectar(bagda, "PERSIA_35");
        conectar(bagda, "PERSIA_36");
        conectar(bagda, "PERSIA_37");
        conectar(tabriz, "PERSIA_30");
        conectar(tabriz, "PERSIA_31");
        conectar(tabriz, "PERSIA_32");
    }

    private void conectar(String loc1, String loc2) {
        Set<String> s1 = this.rotas.get(loc1);
        if (s1 == null) {
            s1 = new HashSet<>();
            this.rotas.put(loc1, s1);
        }
        s1.add(loc2);

        Set<String> s2 = this.rotas.get(loc2);
        if (s2 == null) {
            s2 = new HashSet<>();
            this.rotas.put(loc2, s2);
        }
        s2.add(loc1);
    }

    /**
     * Calcula o menor número de passos (distância no grafo) entre duas localidades via BFS.
     * Retorna -1 se não houver caminho ou se alguma localidade for inválida.
     */
    public int calcularDistancia(String origem, String destino) {
        if (origem == null || destino == null) {
            return -1;
        }
        if (origem.equals(destino)) {
            return 0;
        }
        java.util.Queue<String> fila = new java.util.LinkedList<>();
        Map<String, Integer> distancias = new HashMap<>();
        fila.add(origem);
        distancias.put(origem, 0);

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            int d = distancias.get(atual);

            Set<String> vizinhas = this.rotas.get(atual);
            if (vizinhas != null) {
                for (String v : vizinhas) {
                    if (!distancias.containsKey(v)) {
                        distancias.put(v, d + 1);
                        if (v.equals(destino)) {
                            return d + 1;
                        }
                        fila.add(v);
                    }
                }
            }
        }
        return -1;
    }

    public boolean saoVizinhas(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        Set<String> vizinhas = this.rotas.get(loc1);
        return vizinhas != null && vizinhas.contains(loc2);
    }

    public Set<String> getVizinhos(String localizacao) {
        Set<String> vizinhos = this.rotas.get(localizacao);
        if (vizinhos == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(vizinhos);
    }

    public boolean construirYurt(String localizacao, Cor corJogador) {
        if (localizacao == null || corJogador == null || this.yurts.containsKey(localizacao)) {
            return false;
        }
        this.yurts.put(localizacao, corJogador);
        return true;
    }

    public boolean temYurt(String localizacao) {
        return this.yurts.containsKey(localizacao);
    }

    public Cor getDonoYurt(String localizacao) {
        return this.yurts.get(localizacao);
    }
    
    public void substituirCidadeConquistada(Cidade cidadeAntiga, Cidade novaCidade) {
        if (cidadeAntiga != null) {
            this.cidadesDisponiveis.remove(cidadeAntiga.getNomeCidade());
        }
        
        if (novaCidade != null) {
            this.cidadesDisponiveis.put(novaCidade.getNomeCidade(), novaCidade);
        }
    }

    public Map<String, Cor> getYurts() {
        return Collections.unmodifiableMap(yurts);
    }

    public void limparYurts() {
        this.yurts.clear();
    }

    public void recriarCidadesDisponiveis(Map<NomeCidade, Cidade> novas) {
        this.cidadesDisponiveis.clear();
        if (novas != null) {
            this.cidadesDisponiveis.putAll(novas);
        }
    }

    public Cidade getCidade(NomeCidade nome) {
        return this.cidadesDisponiveis.get(nome);
    }

    public Peao getPeaoDoKhan() {
        return peaoDoKhan;
    }

    public Map<NomeCidade, Cidade> getCidadesDisponiveis() {
        return Collections.unmodifiableMap(cidadesDisponiveis);
    }
}
