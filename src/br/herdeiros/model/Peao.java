package br.herdeiros.model;

import java.util.Objects;

class Peao {
    private Cor cor;
    private String localizacaoAtual; 

    public Peao(Cor cor, String localizacaoInicial) {
        this.cor = cor;
        this.localizacaoAtual = localizacaoInicial;
    }

    public void mover(String novaLocalizacao) {
        this.localizacaoAtual = novaLocalizacao;
    }

    public String getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public Cor getCor() {
        return cor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peao peao = (Peao) o;
        return cor == peao.cor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cor);
    }
}

