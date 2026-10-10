package br.herdeiros.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Tabuleiro {
    private Peao peaoDoKhan;
    private Map<NomeCidade, Cidade> cidadesDisponiveis; 

    public Tabuleiro(List<Cidade> cidadesIniciaisSorteadas) {
        // O peão branco representa o Khan e começa ao lado do tabuleiro
        this.peaoDoKhan = new Peao(Cor.BRANCO, "Fora do Tabuleiro");
        
        this.cidadesDisponiveis = new HashMap<>();
        if (cidadesIniciaisSorteadas != null) {
            for (Cidade cidade : cidadesIniciaisSorteadas) {
                this.cidadesDisponiveis.put(cidade.getNomeCidade(), cidade);
            }
        }
    }
    
    public void substituirCidadeConquistada(Cidade cidadeAntiga, Cidade novaCidade) {
        if (cidadeAntiga != null) {
            this.cidadesDisponiveis.remove(cidadeAntiga.getNomeCidade());
        }
        
        if (novaCidade != null) {
            this.cidadesDisponiveis.put(novaCidade.getNomeCidade(), novaCidade);
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
