package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class HerdeirosTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Cidade cidadeTeste;
    private Jogador jogadorTeste;
    private BaralhoDeCidades baralhoTeste;
    private Tabuleiro tabuleiroTeste;

    @Before
    public void setUp() {
        cidadeTeste = new Cidade(NomeCidade.BAGDA, Regiao.PERSIA);
        jogadorTeste = new Jogador("Genghis", Cor.VERMELHO, 3);
        baralhoTeste = new BaralhoDeCidades();
        List<Cidade> cidadesIniciais = baralhoTeste.sacarCidadesIniciais();
        tabuleiroTeste = new Tabuleiro(cidadesIniciais);
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeComTesourosDecrementaQuantidadeERetornaVerdadeiro() {
        boolean resultado = cidadeTeste.saquearTesouro();
        assertTrue("O saque deve ser bem-sucedido quando a cidade possui tesouros", resultado);
        assertEquals("A quantidade de tesouros deve diminuir de 4 para 3", 3, cidadeTeste.getQuantidadeTesouros());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeAteEgotarTesourosMarcaComoConquistada() {
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro(); // Esgota os 4 tesouros
        
        assertTrue("A cidade deve estar marcada como conquistada após esgotar os tesouros", cidadeTeste.isConquistada());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeSemTesourosRetornaFalsoENaoAlteraEstado() {
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro(); // Esgota
        boolean saqueExtra = cidadeTeste.saquearTesouro();

        assertFalse("O saque em cidade já esgotada deve retornar falso", saqueExtra);
        assertTrue("A cidade continua conquistada", cidadeTeste.isConquistada());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorInicializaComAtributosCorretos() {
        assertEquals("O nome do jogador deve corresponder ao informado", "Genghis", jogadorTeste.getNome());
        assertEquals("A cor do jogador deve ser vermelha", Cor.VERMELHO, jogadorTeste.getCor());
        assertEquals("A quantidade inicial de descendentes deve ser 3", 3, jogadorTeste.getQuantidadeDescendentes());
        assertEquals("Os votos iniciais no Kurultai devem ser zero", 0, jogadorTeste.getVotosKurultai());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testGanharVotosIncrementaKurultaiCorretamente() {
        jogadorTeste.ganharVotos(2);
        assertEquals("O jogador deve possuir 2 votos no Kurultai após o ganho", 2, jogadorTeste.getVotosKurultai());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testBaralhoDeCidadesGeraCidadesIniciaisComSucesso() {
        List<Cidade> iniciais = baralhoTeste.sacarCidadesIniciais();
        assertNotNull("A lista de cidades iniciais não deve ser nula", iniciais);
        assertEquals("Devem ser sacadas exatamente 3 cidades iniciais", 3, iniciais.size());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testTabuleiroInicializaComCidadesDisponiveisNoMapa() {
        Map<NomeCidade, Cidade> disponiveis = tabuleiroTeste.getCidadesDisponiveis();
        assertNotNull("O mapa de cidades disponíveis no tabuleiro não deve ser nulo", disponiveis);
        assertEquals("O tabuleiro deve conter 3 cidades disponíveis inicialmente", 3, disponiveis.size());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testPeaoDoJogadorENaoNulo() {
        assertNotNull("O jogador deve possuir um peão associado ao ser instanciado", jogadorTeste.getMeuPeao());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testSubstituirCidadeConquistadaNoTabuleiroAtualizaMapa() {
        Cidade cidadeParaRemover = tabuleiroTeste.getCidadesDisponiveis().values().iterator().next();
        NomeCidade nomeRemovida = cidadeParaRemover.getNomeCidade();
        
        Cidade novaCidade = new Cidade(NomeCidade.PEQUIM, Regiao.CHINA);
        tabuleiroTeste.substituirCidadeConquistada(cidadeParaRemover, novaCidade);

        assertNull("A cidade antiga não deve mais estar no mapa", tabuleiroTeste.getCidade(nomeRemovida));
        assertEquals("A nova cidade deve estar acessível pela chave no mapa", novaCidade, tabuleiroTeste.getCidade(NomeCidade.PEQUIM));
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testPartidaAdicionaEBuscaJogadorPorCorComHashMap() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 2);
        partida.adicionarJogador("Jochi", Cor.VERDE, 4);

        assertEquals("A partida deve conter 2 jogadores mapeados", 2, partida.getJogadores().size());
        assertNotNull("Deve encontrar o jogador azul via busca direta por chave", partida.getJogador(Cor.AZUL));
        assertEquals("O nome do jogador azul deve bater", "Batu", partida.getJogador(Cor.AZUL).getNome());
        assertNotNull("Deve encontrar o jogador verde via busca direta por chave", partida.getJogador(Cor.VERDE));
        assertEquals("O primeiro jogador adicionado deve ser o atual", partida.getJogador(Cor.AZUL), partida.getJogadorAtual());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorGerenciaTributosComHashMap() {
        assertEquals("Jogador deve iniciar com 0 espadas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve iniciar com 0 yurts", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.YURT));
        assertEquals("Jogador deve iniciar com 0 moedas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));

        jogadorTeste.adicionarTributo(TipoTributo.ESPADA, 3);
        jogadorTeste.adicionarTributo(TipoTributo.MOEDA, 5);

        assertEquals("Jogador deve ter 3 espadas após adição", 3, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve ter 5 moedas após adição", 5, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));
        assertEquals("Yurts continuam 0", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.YURT));
        assertEquals("Mapa de tributos deve conter 3 tipos mapeados", 3, jogadorTeste.getTributos().size());
    }

    @Test(timeout = TIMEOUT_PADRAO)
    public void testEqualsEHashCodeCidadeEJogadorParaTabelasHash() {
        Cidade cidade1 = new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA);
        Cidade cidade2 = new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA);
        assertEquals("Cidades com mesmo nome devem ser iguais", cidade1, cidade2);
        assertEquals("Cidades iguais devem ter o mesmo hashCode", cidade1.hashCode(), cidade2.hashCode());

        Jogador jog1 = new Jogador("Subutai", Cor.AMARELO, 1);
        Jogador jog2 = new Jogador("OutroNome", Cor.AMARELO, 5);
        assertEquals("Jogadores com a mesma cor devem ser iguais", jog1, jog2);
        assertEquals("Jogadores iguais devem ter o mesmo hashCode", jog1.hashCode(), jog2.hashCode());
    }
}
