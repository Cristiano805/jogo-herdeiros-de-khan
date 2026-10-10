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

/**
 * Bateria de testes unitários para o componente Model do jogo Herdeiros de Khan.
 * Implementada com JUnit 4 e documentada conforme as diretrizes do Capítulo 18 (INF1636).
 */
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

    /**
     * Objetivo: Testar se o saque em uma cidade com tesouros decrementa seu estoque e retorna sucesso.
     * Cenário: Cidade recém-criada com 4 tesouros.
     * Ação: Invocação de saquearTesouro().
     * Resultado Esperado: Retorno verdadeiro e estoque reduzido para 3.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeComTesourosDecrementaQuantidadeERetornaVerdadeiro() {
        boolean resultado = cidadeTeste.saquearTesouro();
        assertTrue("O saque deve ser bem-sucedido quando a cidade possui tesouros", resultado);
        assertEquals("A quantidade de tesouros deve diminuir de 4 para 3", 3, cidadeTeste.getQuantidadeTesouros());
    }

    /**
     * Objetivo: Testar se uma cidade tem seu estado alterado para conquistada ao ter todos os 4 tesouros saqueados.
     * Cenário: Cidade inicial com 4 tesouros.
     * Ação: Invocação de saquearTesouro() 4 vezes consecutivas.
     * Resultado Esperado: isConquistada() retorna verdadeiro e estoque de tesouros é 0.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeAteEgotarTesourosMarcaComoConquistada() {
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro(); // Esgota os 4 tesouros
        
        assertTrue("A cidade deve estar marcada como conquistada após esgotar os tesouros", cidadeTeste.isConquistada());
        assertEquals("Estoque de tesouros deve ser zero", 0, cidadeTeste.getQuantidadeTesouros());
    }

    /**
     * Objetivo: Testar se tentativas de saque adicionais em cidades já esgotadas falham sem alterar estado.
     * Cenário: Cidade com todos os tesouros já saqueados.
     * Ação: Invocação de uma quinta operação de saque.
     * Resultado Esperado: Retorno falso e preservação do estado de conquistada.
     */
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

    /**
     * Objetivo: Testar a correta inicialização dos atributos de um Jogador.
     * Cenário: Instanciação de Jogador com nome "Genghis", cor VERMELHO e 3 descendentes.
     * Ação: Leitura dos getters correspondentes.
     * Resultado Esperado: Atributos correspondentes aos parâmetros passados e votos no Kurultai zerados.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorInicializaComAtributosCorretos() {
        assertEquals("O nome do jogador deve corresponder ao informado", "Genghis", jogadorTeste.getNome());
        assertEquals("A cor do jogador deve ser vermelha", Cor.VERMELHO, jogadorTeste.getCor());
        assertEquals("A quantidade inicial de descendentes deve ser 3", 3, jogadorTeste.getQuantidadeDescendentes());
        assertEquals("Os votos iniciais no Kurultai devem ser zero", 0, jogadorTeste.getVotosKurultai());
    }

    /**
     * Objetivo: Testar o incremento de pontuação (votos no Kurultai) de um Jogador.
     * Cenário: Jogador com 0 votos.
     * Ação: Chamada a ganharVotos(2).
     * Resultado Esperado: Total de votos atualizado para 2.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testGanharVotosIncrementaKurultaiCorretamente() {
        jogadorTeste.ganharVotos(2);
        assertEquals("O jogador deve possuir 2 votos no Kurultai após o ganho", 2, jogadorTeste.getVotosKurultai());
    }

    /**
     * Objetivo: Testar se o Baralho de Cidades saca exatamente as 3 cidades iniciais para o tabuleiro.
     * Cenário: Baralho de cidades recém-instanciado.
     * Ação: Chamada a sacarCidadesIniciais().
     * Resultado Esperado: Lista não-nula com exatamente 3 elementos do tipo Cidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testBaralhoDeCidadesGeraCidadesIniciaisComSucesso() {
        List<Cidade> iniciais = baralhoTeste.sacarCidadesIniciais();
        assertNotNull("A lista de cidades iniciais não deve ser nula", iniciais);
        assertEquals("Devem ser sacadas exatamente 3 cidades iniciais", 3, iniciais.size());
    }

    /**
     * Objetivo: Testar se o Tabuleiro inicializa as cidades disponíveis utilizando a estrutura de Mapa (HashMap).
     * Cenário: Tabuleiro instanciado com 3 cidades sorteadas.
     * Ação: Consulta ao mapa de cidades disponíveis.
     * Resultado Esperado: Mapa não nulo com exatamente 3 cidades indexadas por NomeCidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testTabuleiroInicializaComCidadesDisponiveisNoMapa() {
        Map<NomeCidade, Cidade> disponiveis = tabuleiroTeste.getCidadesDisponiveis();
        assertNotNull("O mapa de cidades disponíveis no tabuleiro não deve ser nulo", disponiveis);
        assertEquals("O tabuleiro deve conter 3 cidades disponíveis inicialmente", 3, disponiveis.size());
    }

    /**
     * Objetivo: Testar a correta associação e criação do peão do jogador no nó inicial "Karakorum".
     * Cenário: Instanciação do Jogador.
     * Ação: Invocação de getMeuPeao() e getLocalizacaoAtual().
     * Resultado Esperado: Peão não nulo com cor correspondente e localizado em Karakorum.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPeaoDoJogadorENaoNulo() {
        assertNotNull("O jogador deve possuir um peão associado ao ser instanciado", jogadorTeste.getMeuPeao());
        assertEquals("O peão deve iniciar na capital Karakorum", "Karakorum", jogadorTeste.getMeuPeao().getLocalizacaoAtual());
    }

    /**
     * Objetivo: Testar a substituição de cidade conquistada no mapa de cidades do Tabuleiro via HashMap.
     * Cenário: Tabuleiro com 3 cidades ativas.
     * Ação: Remoção da cidade antiga e inclusão de nova cidade sacada.
     * Resultado Esperado: Antiga cidade inacessível via chave e nova cidade indexada com sucesso.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSubstituirCidadeConquistadaNoTabuleiroAtualizaMapa() {
        Cidade cidadeParaRemover = tabuleiroTeste.getCidadesDisponiveis().values().iterator().next();
        NomeCidade nomeRemovida = cidadeParaRemover.getNomeCidade();
        
        Cidade novaCidade = new Cidade(NomeCidade.PEQUIM, Regiao.CHINA);
        tabuleiroTeste.substituirCidadeConquistada(cidadeParaRemover, novaCidade);

        assertNull("A cidade antiga não deve mais estar no mapa", tabuleiroTeste.getCidade(nomeRemovida));
        assertEquals("A nova cidade deve estar acessível pela chave no mapa", novaCidade, tabuleiroTeste.getCidade(NomeCidade.PEQUIM));
    }

    /**
     * Objetivo: Testar adição e busca direta de jogadores por Cor na Partida utilizando HashMap.
     * Cenário: Partida recém-criada.
     * Ação: Adição de jogadores de cores azul e verde.
     * Resultado Esperado: 2 jogadores mapeados e recuperação direta via chave única (Cor).
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPartidaAdicionaEBuscaJogadorPorCorComHashMap() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 2);
        partida.adicionarJogador("Jochi", Cor.VERDE, 4);

        assertEquals("A partida deve conter 2 jogadores mapeados", 2, partida.getQuantidadeJogadores());
        assertEquals("O nome do jogador azul deve bater", "Batu", partida.getNomeJogador(Cor.AZUL));
        assertEquals("O nome do jogador verde deve bater", "Jochi", partida.getNomeJogador(Cor.VERDE));
        assertEquals("O primeiro jogador adicionado deve ser o atual", Cor.AZUL, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar a gestão do inventário de tributos do Jogador modelado com HashMap.
     * Cenário: Jogador com inventário zerado.
     * Ação: Adição de 3 espadas e 5 moedas, seguida de remoção de 1 espada.
     * Resultado Esperado: Estoques atualizados corretamente e retorno booleano adequado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorGerenciaTributosComHashMap() {
        assertEquals("Jogador deve iniciar com 0 espadas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve iniciar com 0 yurts", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.YURT));
        assertEquals("Jogador deve iniciar com 0 moedas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));

        jogadorTeste.adicionarTributo(TipoTributo.ESPADA, 3);
        jogadorTeste.adicionarTributo(TipoTributo.MOEDA, 5);

        assertEquals("Jogador deve ter 3 espadas após adição", 3, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve ter 5 moedas após adição", 5, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));

        boolean removeu = jogadorTeste.removerTributo(TipoTributo.ESPADA, 1);
        assertTrue("A remoção de 1 espada deve ser bem-sucedida", removeu);
        assertEquals("Jogador deve ficar com 2 espadas", 2, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));

        boolean removeuExcesso = jogadorTeste.removerTributo(TipoTributo.ESPADA, 10);
        assertFalse("Não deve permitir remover mais recursos do que o jogador possui", removeuExcesso);
    }

    /**
     * Objetivo: Testar a consistência do contrato equals() e hashCode() para Cidade e Jogador (Capítulo 15).
     * Cenário: Pares de instâncias com atributos identificadores iguais e diferentes.
     * Ação: Comparação via equals() e hashCode().
     * Resultado Esperado: Igualdade lógica e igualdade de código hash para chaves com mesma identidade.
     */
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

    /**
     * Objetivo: Testar a regra de negócio oficial que define quem começa jogando pelo número de descendentes.
     * Cenário: Partida com três jogadores cadastrados com 2, 6 e 3 descendentes.
     * Ação: Invocação de definirPrimeiroJogadorPorDescendentes().
     * Resultado Esperado: O jogador com 6 descendentes (VERDE) deve ser eleito o jogador inicial.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testDefinirPrimeiroJogadorPorDescendentesRegraDeNegocio() {
        Partida partida = new Partida();
        partida.adicionarJogador("Ogedei", Cor.AZUL, 2);
        partida.adicionarJogador("Tolui", Cor.VERDE, 6);
        partida.adicionarJogador("Chagatai", Cor.AMARELO, 3);

        Cor corInicial = partida.definirPrimeiroJogadorPorDescendentes();
        assertEquals("O jogador com mais descendentes (VERDE - 6) deve iniciar", Cor.VERDE, corInicial);
        assertEquals("O jogador atual da partida deve ser atualizado para o inicial", Cor.VERDE, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar a validação de rotas e movimentação de peão entre nós vizinhos no Tabuleiro.
     * Cenário: Jogador atual posicionado na capital "Karakorum".
     * Ação: Mover para um território vizinho válido ("Kazan") e depois tentar movimento inválido para "Bagdá" sem rota direta.
     * Resultado Esperado: Primeiro movimento aceito com atualização de localização; segundo rejeitado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testMoverJogadorAtualPelasRotasDoTabuleiro() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        assertEquals("Peão começa em Karakorum", "Karakorum", partida.getLocalizacaoJogadorAtual());

        // Karakorum possui conexão direta com Kazan (portão de entrada da Rússia)
        boolean moveuValido = partida.moverJogadorAtual("Kazan");
        assertTrue("Movimento para vizinho Kazan deve ser válido", moveuValido);
        assertEquals("Localização atual deve ser Kazan", "Kazan", partida.getLocalizacaoJogadorAtual());

        // Kazan NÃO possui rota direta com Bagdá
        boolean moveuInvalido = partida.moverJogadorAtual("Bagdá");
        assertFalse("Movimento para território não vizinho deve ser recusado", moveuInvalido);
        assertEquals("Localização deve permanecer em Kazan", "Kazan", partida.getLocalizacaoJogadorAtual());
    }

    /**
     * Objetivo: Testar a construção de Yurt consumindo o recurso de Yurt do jogador e registrando no Tabuleiro.
     * Cenário: Jogador atual sem yurts no inventário.
     * Ação: Tentativa de construção sem recurso (falha), seguida de coleta do tributo e construção bem-sucedida.
     * Resultado Esperado: Primeira tentativa retorna falso; após receber recurso, retorna verdadeiro e reduz inventário.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testConstruirYurtConsomeTributoEAtualizaTabuleiro() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        boolean construiuSemRecurso = partida.construirYurtJogadorAtual("Karakorum");
        assertFalse("Não deve construir yurt sem possuir o tributo YURT", construiuSemRecurso);

        partida.coletarTributo(Cor.AZUL, TipoTributo.YURT, 1);
        assertEquals("Jogador deve possuir 1 yurt", 1, partida.getTributosJogador(Cor.AZUL, TipoTributo.YURT));

        boolean construiuComRecurso = partida.construirYurtJogadorAtual("Karakorum");
        assertTrue("Deve construir yurt com sucesso", construiuComRecurso);
        assertEquals("O estoque de yurts do jogador deve ser decrementado para 0", 0, partida.getTributosJogador(Cor.AZUL, TipoTributo.YURT));
        assertTrue("O tabuleiro deve registrar presença de yurt em Karakorum", partida.temYurtNaLocalizacao("Karakorum"));
        assertEquals("O dono do yurt deve ser o jogador AZUL", Cor.AZUL, partida.getDonoYurtNaLocalizacao("Karakorum"));
    }

    /**
     * Objetivo: Testar a regra completa de saque de cidade via API Façade da Partida.
     * Cenário: Jogador atual recebe 1 espada para realizar saque em cidade disponível.
     * Ação: Saque de cidade com sucesso.
     * Resultado Esperado: Consumo de 1 espada, ganho de 1 voto no Kurultai e decremento de tesouros da cidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeViaPartidaConsomeEspadaEGanhaVotos() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        // Identifica uma cidade presente no tabuleiro
        NomeCidade cidadeDisponivel = partida.getTabuleiro().getCidadesDisponiveis().keySet().iterator().next();

        // Tentativa sem espada deve falhar
        boolean saqueSemEspada = partida.saquearCidade(cidadeDisponivel);
        assertFalse("Saque sem espada deve falhar", saqueSemEspada);

        // Jogador coleta 1 espada
        partida.coletarTributo(Cor.AZUL, TipoTributo.ESPADA, 1);
        int tesourosAntes = partida.getTesourosCidade(cidadeDisponivel);

        boolean saqueComEspada = partida.saquearCidade(cidadeDisponivel);
        assertTrue("Saque com espada deve suceder", saqueComEspada);
        assertEquals("Espada deve ser gasta", 0, partida.getTributosJogador(Cor.AZUL, TipoTributo.ESPADA));
        assertEquals("Jogador deve receber 1 voto no Kurultai", 1, partida.getVotosJogadorAtual());
        assertEquals("A cidade deve ter 1 tesouro a menos", tesourosAntes - 1, partida.getTesourosCidade(cidadeDisponivel));
    }

    /**
     * Objetivo: Testar a alternância circular de turnos entre os jogadores.
     * Cenário: Partida com 2 jogadores cadastrados (AZUL e VERMELHO).
     * Ação: Invocação de passarTurno().
     * Resultado Esperado: Alternância para o jogador seguinte e retorno ao primeiro de forma circular.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPassarTurnoAlternaEntreJogadores() {
        Partida partida = new Partida();
        partida.adicionarJogador("Batu", Cor.AZUL, 1);
        partida.adicionarJogador("Genghis", Cor.VERMELHO, 2);

        assertEquals("Jogador inicial deve ser AZUL", Cor.AZUL, partida.getCorJogadorAtual());

        Cor proximo = partida.passarTurno();
        assertEquals("Próximo jogador deve ser VERMELHO", Cor.VERMELHO, proximo);
        assertEquals("Jogador atual deve ser VERMELHO", Cor.VERMELHO, partida.getCorJogadorAtual());

        Cor ciclo = partida.passarTurno();
        assertEquals("Ciclo deve retornar ao jogador AZUL", Cor.AZUL, ciclo);
        assertEquals("Jogador atual deve ser AZUL", Cor.AZUL, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar se a conquista de uma cidade registra o jogador autor do último saque como conquistador.
     * Cenário: Cidade com 4 tesouros e jogador AZUL executando os saques.
     * Ação: Execução de 4 saques associados ao jogador AZUL.
     * Resultado Esperado: Cidade marcada como conquistada e conquistador igual a Cor.AZUL.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testCidadeConquistadaRegistraJogadorConquistador() {
        Cidade cidade = new Cidade(NomeCidade.PEQUIM, Regiao.CHINA);
        assertNull("Cidade recém-criada não possui conquistador", cidade.getConquistador());

        cidade.saquearTesouro(Cor.AZUL);
        cidade.saquearTesouro(Cor.AZUL);
        cidade.saquearTesouro(Cor.AZUL);
        assertFalse("Com 1 tesouro restante ainda não está conquistada", cidade.isConquistada());

        cidade.saquearTesouro(Cor.AZUL); // 4º saque: esgota e conquista
        assertTrue("Cidade deve estar conquistada", cidade.isConquistada());
        assertEquals("O conquistador deve ser o jogador AZUL", Cor.AZUL, cidade.getConquistador());
    }

    /**
     * Objetivo: Testar se o peão registra as localizações visitadas durante o turno e reinicia no próximo turno.
     * Cenário: Peão criado na capital Karakorum.
     * Ação: Movimentação para Kazan e posterior reinício de turno.
     * Resultado Esperado: Histórico de caminho registra Karakorum e Kazan; jaVisitouNoTurno retorna verdadeiro; após iniciarTurno, histórico é resetado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPeaoRegistraCaminhoDoTurnoEVerificaVisitados() {
        Peao peao = new Peao(Cor.AMARELO, "Karakorum");
        assertTrue("Inicia contendo a posição de partida", peao.jaVisitouNoTurno("Karakorum"));
        assertEquals("Caminho inicial contém 1 posição", 1, peao.getCaminhoDoTurno().size());

        peao.mover("Kazan");
        assertTrue("Registra Kazan como visitado no turno", peao.jaVisitouNoTurno("Kazan"));
        assertEquals("Caminho contém 2 posições", 2, peao.getCaminhoDoTurno().size());

        peao.iniciarTurno();
        assertEquals("Novo turno reinicia caminho contendo apenas localização atual (Kazan)", 1, peao.getCaminhoDoTurno().size());
        assertTrue("Contém Kazan", peao.jaVisitouNoTurno("Kazan"));
        assertFalse("Não contém mais Karakorum no turno atual", peao.jaVisitouNoTurno("Karakorum"));
    }

    /**
     * Objetivo: Testar a movimentação e consulta do peão neutro do Khan.
     * Cenário: Partida recém-criada com peão do Khan fora do tabuleiro.
     * Ação: Mover o Khan para Karakorum via API Façade.
     * Resultado Esperado: Localização inicial "Fora do Tabuleiro" atualizada para "Karakorum".
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testMoverPeaoDoKhan() {
        Partida partida = new Partida();
        assertEquals("Khan inicia fora do tabuleiro", "Fora do Tabuleiro", partida.getLocalizacaoKhan());

        partida.moverKhan("Karakorum");
        assertEquals("Khan deve estar posicionado em Karakorum", "Karakorum", partida.getLocalizacaoKhan());
    }
}


