package br.herdeiros.model;

import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ponto de entrada (API / Façade) do componente Model para gerenciar partidas de Herdeiros de Khan.
 * Centraliza as regras de negócio, turno, movimentação, tributos e saques de cidades.
 *
 * Padrões aplicados:
 *  - Singleton: ponto único de acesso ao Model via {@link #getInstance()}.
 *  - Façade: única classe pública com operações do Model; as demais são package-private.
 *  - Observer: é o objeto observado ({@link ObservadoIF}); a cada mudança de estado chama
 *    notify(this) nos observadores, que leem o estado por get(i) (ver {@link IndicesEstado}).
 */
public class Partida implements ObservadoIF {
    private static Partida instancia;

    private Map<Cor, Jogador> jogadores;
    private List<Cor> ordemTurnos;
    private int indiceTurnoAtual;
    private Tabuleiro tabuleiro;
    private BaralhoDeCidades baralho;
    private Jogador jogadorAtual;
    private int movimentosRestantes;
    private boolean partidaEncerrada;
    private boolean podeSalvar;

    // Estado de exibição das janelas (refletido nas Views via Observer)
    private boolean exibirCartela;
    private boolean exibirCartas;
    private boolean exibirInventario;

    private List<ObservadorIF> observadores = new ArrayList<ObservadorIF>();

    /** Construtor restrito ao pacote (Singleton). Visível ao pacote apenas para testes unitários. */
    Partida() {
        inicializarEstado();
    }

    public static synchronized Partida getInstance() {
        if (instancia == null) {
            instancia = new Partida();
        }
        return instancia;
    }

    private void inicializarEstado() {
        this.jogadores = new HashMap<>();
        this.ordemTurnos = new ArrayList<>();
        this.indiceTurnoAtual = 0;
        this.jogadorAtual = null;
        this.movimentosRestantes = 0;
        this.baralho = new BaralhoDeCidades();
        // Inicializa o tabuleiro com as 3 cidades iniciais sacadas do baralho
        this.tabuleiro = new Tabuleiro(baralho.sacarCidadesIniciais());
        this.exibirCartela = false;
        this.exibirCartas = false;
        this.exibirInventario = false;
        this.partidaEncerrada = false;
        this.podeSalvar = true;
    }

    /** Reinicia a partida mantendo os observadores registrados. */
    public void reiniciar() {
        inicializarEstado();
        notificarObservadores();
    }

    // --- Observer (ObservadoIF) ---

    @Override
    public void add(ObservadorIF o) {
        if (o != null && !observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void remove(ObservadorIF o) {
        observadores.remove(o);
    }

    private void notificarObservadores() {
        List<ObservadorIF> copia = new ArrayList<ObservadorIF>(observadores);
        for (ObservadorIF o : copia) {
            o.notify(this);
        }
    }

    @Override
    public int get(int i) {
        switch (i) {
            case IndicesEstado.QTD_JOGADORES: return ordemTurnos.size();
            case IndicesEstado.JOGADOR_DA_VEZ: return jogadorAtual == null ? -1 : ordemTurnos.indexOf(jogadorAtual.getCor());
            case IndicesEstado.MOVIMENTOS_RESTANTES: return movimentosRestantes;
            case IndicesEstado.EXIBIR_CARTELA: return exibirCartela ? 1 : 0;
            case IndicesEstado.EXIBIR_CARTAS: return exibirCartas ? 1 : 0;
            case IndicesEstado.EXIBIR_INVENTARIO: return exibirInventario ? 1 : 0;
            case IndicesEstado.LOCALIDADE_KHAN: return IndicesEstado.indiceLocalidade(getLocalizacaoKhan());
            case IndicesEstado.PARTIDA_ENCERRADA: return partidaEncerrada ? 1 : 0;
            case IndicesEstado.PODE_SALVAR: return podeSalvar ? 1 : 0;
            default: break;
        }
        if (i >= IndicesEstado.YURT_BASE) {
            String loc = IndicesEstado.nomeLocalidade(i - IndicesEstado.YURT_BASE);
            Cor dono = loc == null ? null : tabuleiro.getDonoYurt(loc);
            return dono == null ? -1 : dono.ordinal();
        }
        if (i >= IndicesEstado.CIDADE_BASE) {
            int rel = i - IndicesEstado.CIDADE_BASE;
            int ordinal = rel / IndicesEstado.CIDADE_PASSO;
            int campo = rel % IndicesEstado.CIDADE_PASSO;
            if (ordinal >= NomeCidade.values().length) return -1;
            NomeCidade nc = NomeCidade.values()[ordinal];
            Cidade c = tabuleiro.getCidade(nc);
            if (campo == IndicesEstado.CAMPO_TESOUROS) return c == null ? -1 : c.getQuantidadeTesouros();
            if (campo == IndicesEstado.CAMPO_LOCALIDADE_CIDADE) return IndicesEstado.indiceLocalidade(nc.getNomeFormatado());
            return -1;
        }
        if (i >= IndicesEstado.JOGADOR_BASE) {
            int rel = i - IndicesEstado.JOGADOR_BASE;
            int idx = rel / IndicesEstado.JOGADOR_PASSO;
            int campo = rel % IndicesEstado.JOGADOR_PASSO;
            if (idx >= ordemTurnos.size()) return -1;
            Jogador j = jogadores.get(ordemTurnos.get(idx));
            switch (campo) {
                case IndicesEstado.CAMPO_COR: return j.getCor().ordinal();
                case IndicesEstado.CAMPO_LOCALIDADE: return IndicesEstado.indiceLocalidade(j.getMeuPeao().getLocalizacaoAtual());
                case IndicesEstado.CAMPO_VOTOS: return j.getVotosKurultai();
                case IndicesEstado.CAMPO_ESPADAS: return j.getQuantidadeTributo(TipoTributo.ESPADA);
                case IndicesEstado.CAMPO_YURTS: return j.getQuantidadeTributo(TipoTributo.YURT);
                case IndicesEstado.CAMPO_MOEDAS: return j.getQuantidadeTributo(TipoTributo.MOEDA);
                case IndicesEstado.CAMPO_DESCENDENTES: return j.getQuantidadeDescendentes();
                case IndicesEstado.CAMPO_QTD_CARTAS: return Math.min(j.getCartas().size(), IndicesEstado.MAX_CARTAS);
                case IndicesEstado.CAMPO_CONSELHEIRO:
                    return j.getConselheiro() == null ? -1 : IndicesEstado.codificarCarta(j.getConselheiro());
                case IndicesEstado.CAMPO_SECRETA:
                    return j.getCartaSecreta() == null ? -1 : IndicesEstado.codificarCarta(j.getCartaSecreta());
                default:
                    int k = campo - IndicesEstado.CAMPO_CARTA_0;
                    if (k >= 0 && k < j.getCartas().size()) {
                        return IndicesEstado.codificarCarta(j.getCartas().get(k));
                    }
                    return -1;
            }
        }
        return -1;
    }

    // --- Dados textuais fixos (nomes), expostos à View apenas através do Controller ---

    public String getNomeJogadorPorIndice(int indice) {
        if (indice < 0 || indice >= ordemTurnos.size()) return null;
        return jogadores.get(ordemTurnos.get(indice)).getNome();
    }

    public static String getNomeCor(int ordinal) {
        Cor[] cores = Cor.values();
        return (ordinal >= 0 && ordinal < cores.length) ? cores[ordinal].name() : null;
    }

    /** Cadastra um jogador informando o nome da cor (usado pelo Controller, sem expor o enum à View). */
    public void adicionarJogador(String nome, String nomeCor, int quantidadeDescendentes) {
        try {
            adicionarJogador(nome, Cor.valueOf(nomeCor), quantidadeDescendentes);
        } catch (IllegalArgumentException | NullPointerException e) {
            // cor inválida: ignorada
        }
    }

    // --- Movimentação com cavalos (estado de turno pertence ao Model) ---

    public void definirMovimentos(int quantidade) {
        this.movimentosRestantes = Math.max(0, quantidade);
        this.podeSalvar = false;
        notificarObservadores();
    }

    /**
     * Move o jogador da vez consumindo os movimentos (cavalos) disponíveis no turno.
     * @return true se o movimento foi realizado.
     */
    public boolean moverJogadorDaVez(String destino) {
        if (partidaEncerrada || this.jogadorAtual == null || destino == null || movimentosRestantes <= 0) {
            return false;
        }
        String origem = getLocalizacaoJogadorAtual();
        if (origem == null || origem.equalsIgnoreCase(destino)) {
            return false;
        }
        int distancia = this.tabuleiro.calcularDistancia(origem, destino);
        if (distancia <= 0 || distancia > movimentosRestantes) {
            return false;
        }
        if (moverJogadorAtual(destino)) {
            movimentosRestantes -= distancia;
            notificarObservadores();
            return true;
        }
        return false;
    }

    // --- Regras acionadas pelo jogador da vez a partir da sua localização ---

    public boolean construirYurtNaLocalizacaoAtual() {
        return construirYurtJogadorAtual(getLocalizacaoJogadorAtual());
    }

    public boolean saquearCidadeNaLocalizacaoAtual() {
        String loc = getLocalizacaoJogadorAtual();
        if (loc == null) return false;
        // 1. Caso o jogador esteja diretamente na cidade:
        for (NomeCidade nc : NomeCidade.values()) {
            if (nc.getNomeFormatado().equalsIgnoreCase(loc)) {
                return saquearCidade(nc);
            }
        }
        // 2. Ou caso esteja em uma parada/bolinha adjacente à cidade no mapa:
        Set<String> vizinhos = this.tabuleiro.getVizinhos(loc);
        if (vizinhos != null) {
            for (NomeCidade nc : NomeCidade.values()) {
                if (vizinhos.contains(nc.getNomeFormatado())) {
                    return saquearCidade(nc);
                }
            }
        }
        return false;
    }

    /** @param ordinalTipo índice do tipo de tributo (0 = ESPADA, 1 = YURT, 2 = MOEDA). */
    public void coletarTributoJogadorAtual(int ordinalTipo, int quantidade) {
        TipoTributo[] tipos = TipoTributo.values();
        if (jogadorAtual != null && ordinalTipo >= 0 && ordinalTipo < tipos.length) {
            coletarTributo(jogadorAtual.getCor(), tipos[ordinalTipo], quantidade);
        }
    }

    public static String[] getNomesTributos() {
        TipoTributo[] tipos = TipoTributo.values();
        String[] nomes = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) nomes[i] = tipos[i].name();
        return nomes;
    }

    // --- Exibição de janelas (mudanças de estado refletidas via Observer) ---

    public void setExibirCartela(boolean exibir) {
        this.exibirCartela = exibir;
        notificarObservadores();
    }

    public void setExibirCartas(boolean exibir) {
        this.exibirCartas = exibir;
        notificarObservadores();
    }

    public void setExibirInventario(boolean exibir) {
        this.exibirInventario = exibir;
        notificarObservadores();
    }

    /**
     * Cadastra um novo jogador na partida.
     */
    public void adicionarJogador(String nome, Cor cor, int quantidadeDescendentes) {
        if (cor == null || nome == null || this.jogadores.containsKey(cor)) {
            return;
        }
        Jogador novoJogador = new Jogador(nome, cor, quantidadeDescendentes);
        
        // Distribui as 2 cartas de conselheiro secreto iniciais (regra oficial de preparação, pág. 3)
        int idx = this.jogadores.size();
        int pasta = 3 + (idx % 6); // pastas cartas3 a cartas8
        novoJogador.adicionarCarta("cartas" + pasta + "-1");
        novoJogador.adicionarCarta("cartas" + pasta + "-2");

        this.jogadores.put(cor, novoJogador);
        this.ordemTurnos.add(cor);
        
        if (this.jogadorAtual == null) {
            this.jogadorAtual = novoJogador;
        }
        notificarObservadores();
    }

    /**
     * Regra oficial de Herdeiros de Khan: o jogador com a maior quantidade de descendentes começa.
     * @return a cor do jogador que iniciará a partida, ou null se não houver jogadores.
     */
    public Cor definirPrimeiroJogadorPorDescendentes() {
        if (this.jogadores.isEmpty()) {
            return null;
        }

        Jogador herdeiroMaisDescendentes = null;
        for (Jogador j : this.jogadores.values()) {
            if (herdeiroMaisDescendentes == null || j.getQuantidadeDescendentes() > herdeiroMaisDescendentes.getQuantidadeDescendentes()) {
                herdeiroMaisDescendentes = j;
            }
        }

        this.jogadorAtual = herdeiroMaisDescendentes;
        this.indiceTurnoAtual = this.ordemTurnos.indexOf(herdeiroMaisDescendentes.getCor());
        notificarObservadores();
        return herdeiroMaisDescendentes.getCor();
    }

    /**
     * Passa a vez para o próximo jogador da rodada.
     */
    public Cor passarTurno() {
        if (this.ordemTurnos.isEmpty()) {
            return null;
        }
        this.indiceTurnoAtual = (this.indiceTurnoAtual + 1) % this.ordemTurnos.size();
        Cor proximaCor = this.ordemTurnos.get(this.indiceTurnoAtual);
        this.jogadorAtual = this.jogadores.get(proximaCor);
        if (this.jogadorAtual != null && this.jogadorAtual.getMeuPeao() != null) {
            this.jogadorAtual.getMeuPeao().iniciarTurno();
        }
        this.movimentosRestantes = 0;
        this.podeSalvar = true;
        // Ao trocar de jogador, janelas privadas (cartela/cartas/inventário) passam a mostrar o novo jogador
        notificarObservadores();
        return proximaCor;
    }

    /**
     * Move o peão do jogador atual para a localização de destino.
     * @param destino nome da localização de destino.
     * @return true se o movimento foi válido e realizado, false caso contrário.
     */
    public boolean moverJogadorAtual(String destino) {
        if (this.jogadorAtual == null || destino == null) {
            return false;
        }
        String origem = this.jogadorAtual.getMeuPeao().getLocalizacaoAtual();
        if (origem.equals(destino)) {
            return false;
        }
        int dist = this.tabuleiro.calcularDistancia(origem, destino);
        if (dist > 0) {
            this.podeSalvar = false;
            this.jogadorAtual.getMeuPeao().mover(destino);
            notificarObservadores();
            return true;
        }
        return false;
    }

    /**
     * Retorna o menor número de passos (distância no grafo) entre origem e destino.
     */
    public int calcularDistancia(String origem, String destino) {
        return this.tabuleiro.calcularDistancia(origem, destino);
    }

    /**
     * Retorna a lista de cartas em posse do jogador atual da vez.
     */
    public java.util.List<String> getCartasJogadorAtual() {
        if (this.jogadorAtual == null) {
            return java.util.Collections.emptyList();
        }
        return this.jogadorAtual.getCartas();
    }

    /**
     * Constrói um assentamento (Yurt) na localização informada, gastando 1 tributo de Yurt do jogador atual.
     */
    public boolean construirYurtJogadorAtual(String localizacao) {
        if (partidaEncerrada || this.jogadorAtual == null || localizacao == null) {
            return false;
        }
        if (this.jogadorAtual.getQuantidadeTributo(TipoTributo.YURT) < 1) {
            return false;
        }
        boolean construiu = this.tabuleiro.construirYurt(localizacao, this.jogadorAtual.getCor());
        if (construiu) {
            this.podeSalvar = false;
            this.jogadorAtual.removerTributo(TipoTributo.YURT, 1);
            notificarObservadores();
            return true;
        }
        return false;
    }

    /**
     * Coleta tributos para o jogador da cor especificada.
     */
    public void coletarTributo(Cor cor, TipoTributo tipo, int quantidade) {
        Jogador j = this.jogadores.get(cor);
        if (j != null && tipo != null && quantidade > 0) {
            this.podeSalvar = false;
            j.adicionarTributo(tipo, quantidade);
            notificarObservadores();
        }
    }

    /**
     * Saqueia um tesouro de uma cidade disponível no tabuleiro.
     * Exige que o jogador atual possua pelo menos 1 espada.
     * Concede 1 voto no Kurultai pelo saque. Caso a cidade seja dominada (último tesouro),
     * concede +2 votos bônus e saca uma nova cidade do baralho para substituir no tabuleiro.
     */
    public boolean saquearCidade(NomeCidade nomeCidade) {
        if (partidaEncerrada || this.jogadorAtual == null || nomeCidade == null) {
            return false;
        }
        Cidade cidade = this.tabuleiro.getCidade(nomeCidade);
        if (cidade == null || cidade.isConquistada()) {
            return false;
        }
        if (this.jogadorAtual.getQuantidadeTributo(TipoTributo.ESPADA) < 1) {
            return false;
        }

        boolean sucesso = cidade.saquearTesouro(this.jogadorAtual.getCor());
        if (sucesso) {
            this.podeSalvar = false;
            this.jogadorAtual.removerTributo(TipoTributo.ESPADA, 1);
            this.jogadorAtual.ganharVotos(1);

            // Se o saque esgotou os tesouros, a cidade foi conquistada
            if (cidade.isConquistada()) {
                this.jogadorAtual.ganharVotos(2); // bônus de conquista
                Cidade novaCidade = this.baralho.sacarCidadeDoTopo();
                this.tabuleiro.substituirCidadeConquistada(cidade, novaCidade);
                // Baralho de cidades esgotado: a partida termina
                if (novaCidade == null) {
                    this.partidaEncerrada = true;
                }
            }
            notificarObservadores();
            return true;
        }
        return false;
    }

    /**
     * Move o peão neutro do Khan pelo mapa.
     */
    public void moverKhan(String destino) {
        if (destino != null) {
            this.podeSalvar = false;
            this.tabuleiro.getPeaoDoKhan().mover(destino);
            notificarObservadores();
        }
    }

    public String getLocalizacaoKhan() {
        return this.tabuleiro.getPeaoDoKhan().getLocalizacaoAtual();
    }

    public boolean jaVisitouNoTurnoAtual(String localizacao) {
        return this.jogadorAtual != null && this.jogadorAtual.getMeuPeao().jaVisitouNoTurno(localizacao);
    }

    public List<String> getCaminhoDoTurnoJogadorAtual() {
        if (this.jogadorAtual == null || this.jogadorAtual.getMeuPeao() == null) {
            return Collections.emptyList();
        }
        return this.jogadorAtual.getMeuPeao().getCaminhoDoTurno();
    }

    public Cor getConquistadorCidade(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null ? c.getConquistador() : null;
    }

    // --- Métodos de consulta da API pública ---

    public Cor getCorJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getCor() : null;
    }

    public String getNomeJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getNome() : null;
    }

    public int getVotosJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getVotosKurultai() : 0;
    }

    public String getLocalizacaoJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getMeuPeao().getLocalizacaoAtual() : null;
    }

    public String getNomeJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getNome() : null;
    }

    public int getVotosJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getVotosKurultai() : 0;
    }

    public int getTributosJogador(Cor cor, TipoTributo tipo) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getQuantidadeTributo(tipo) : 0;
    }

    public String getLocalizacaoJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getMeuPeao().getLocalizacaoAtual() : null;
    }

    public int getQuantidadeJogadores() {
        return this.jogadores.size();
    }

    public Set<Cor> getCoresJogadores() {
        return Collections.unmodifiableSet(this.jogadores.keySet());
    }

    public boolean isCidadeDisponivel(NomeCidade nomeCidade) {
        return this.tabuleiro.getCidade(nomeCidade) != null;
    }

    public int getTesourosCidade(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null ? c.getQuantidadeTesouros() : 0;
    }

    public boolean isCidadeConquistada(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null && c.isConquistada();
    }

    public Set<String> getVizinhosLocalizacao(String localizacao) {
        return this.tabuleiro.getVizinhos(localizacao);
    }

    public boolean temYurtNaLocalizacao(String localizacao) {
        return this.tabuleiro.temYurt(localizacao);
    }

    public Cor getDonoYurtNaLocalizacao(String localizacao) {
        return this.tabuleiro.getDonoYurt(localizacao);
    }

    // --- Métodos de acesso para classes do pacote de modelo e testes unitários ---

    Jogador getJogador(Cor cor) {
        return this.jogadores.get(cor);
    }

    Map<Cor, Jogador> getJogadores() {
        return Collections.unmodifiableMap(jogadores);
    }

    Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    BaralhoDeCidades getBaralho() {
        return baralho;
    }

    Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    // --- Fim de partida ---

    public boolean isPartidaEncerrada() {
        return partidaEncerrada;
    }

    /** Encerra a partida (apuração final dos votos do Kurultai). */
    public void encerrarPartida() {
        if (!partidaEncerrada && !jogadores.isEmpty()) {
            partidaEncerrada = true;
            notificarObservadores();
        }
    }

    private List<Jogador> rankingFinal() {
        List<Jogador> ranking = new ArrayList<Jogador>(jogadores.values());
        Collections.sort(ranking, new java.util.Comparator<Jogador>() {
            @Override
            public int compare(Jogador a, Jogador b) {
                if (a.getVotosKurultai() != b.getVotosKurultai()) {
                    return b.getVotosKurultai() - a.getVotosKurultai();
                }
                // Desempate 1 (Regra oficial): maior estoque de recursos/tributos acumulados
                int recA = a.getQuantidadeTributo(TipoTributo.ESPADA) + a.getQuantidadeTributo(TipoTributo.YURT) + a.getQuantidadeTributo(TipoTributo.MOEDA);
                int recB = b.getQuantidadeTributo(TipoTributo.ESPADA) + b.getQuantidadeTributo(TipoTributo.YURT) + b.getQuantidadeTributo(TipoTributo.MOEDA);
                if (recA != recB) {
                    return recB - recA;
                }
                // Desempate 2: maior número de descendentes
                return b.getQuantidadeDescendentes() - a.getQuantidadeDescendentes();
            }
        });
        return ranking;
    }

    public String getNomeVencedor() {
        List<Jogador> ranking = rankingFinal();
        return ranking.isEmpty() ? null : ranking.get(0).getNome();
    }

    /** Texto com o ranking final e a indicação do vencedor. */
    public String getResumoFinal() {
        List<Jogador> ranking = rankingFinal();
        if (ranking.isEmpty()) {
            return "Nenhum jogador na partida.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("VENCEDOR: ").append(ranking.get(0).getNome())
          .append(" (").append(ranking.get(0).getCor().name()).append(")\n\n");
        sb.append("Ranking final (votos no Kurultai):\n");
        int pos = 1;
        for (Jogador j : ranking) {
            sb.append(pos++).append("º - ").append(j.getNome())
              .append(" (").append(j.getCor().name()).append(") - ")
              .append(j.getVotosKurultai()).append(" votos\n");
        }
        return sb.toString();
    }

    // --- Herdeiro, conselheiro e carta secreta ---

    public void definirHerdeiro(String nomeCor, String herdeiro) {
        try {
            Jogador j = jogadores.get(Cor.valueOf(nomeCor));
            if (j != null) {
                j.setHerdeiro(herdeiro);
                notificarObservadores();
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // cor inválida: ignorada
        }
    }

    public String getHerdeiroPorIndice(int indice) {
        if (indice < 0 || indice >= ordemTurnos.size()) return null;
        return jogadores.get(ordemTurnos.get(indice)).getHerdeiro();
    }

    public List<String> getCartasJogadorPorIndice(int indice) {
        if (indice < 0 || indice >= ordemTurnos.size()) return Collections.emptyList();
        return jogadores.get(ordemTurnos.get(indice)).getCartas();
    }

    /** Escolhe qual carta do jogador é o conselheiro; a outra vira a carta secreta. */
    public boolean escolherConselheiro(int indiceJogador, int indiceCarta) {
        if (indiceJogador < 0 || indiceJogador >= ordemTurnos.size()) return false;
        boolean ok = jogadores.get(ordemTurnos.get(indiceJogador)).escolherConselheiro(indiceCarta);
        if (ok) notificarObservadores();
        return ok;
    }

    // --- Salvamento e recuperacao da partida em arquivo texto (.txt) ASCII puro ---

    public boolean salvar(String caminho) {
        if (caminho == null) return false;
        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(caminho), java.nio.charset.StandardCharsets.US_ASCII))) {

            pw.println("[PARTIDA]");
            pw.println("indiceTurnoAtual=" + indiceTurnoAtual);
            pw.println("jogadorAtual=" + (jogadorAtual == null ? "null" : jogadorAtual.getCor().name()));
            pw.println("movimentosRestantes=" + movimentosRestantes);
            pw.println("partidaEncerrada=" + partidaEncerrada);
            pw.println("podeSalvar=" + podeSalvar);
            pw.println("khanLocal=" + (tabuleiro != null && tabuleiro.getPeaoDoKhan() != null ? tabuleiro.getPeaoDoKhan().getLocalizacaoAtual() : "Fora do Tabuleiro"));

            pw.println("\n[ORDEM_TURNOS]");
            StringBuilder sbOrdem = new StringBuilder();
            for (int i = 0; i < ordemTurnos.size(); i++) {
                if (i > 0) sbOrdem.append(",");
                sbOrdem.append(ordemTurnos.get(i).name());
            }
            pw.println(sbOrdem.toString());

            pw.println("\n[JOGADORES]");
            for (Cor c : ordemTurnos) {
                Jogador j = jogadores.get(c);
                if (j == null) continue;
                pw.println("---");
                pw.println("cor=" + j.getCor().name());
                pw.println("nome=" + j.getNome());
                pw.println("herdeiro=" + (j.getHerdeiro() == null ? "" : j.getHerdeiro()));
                pw.println("descendentes=" + j.getQuantidadeDescendentes());
                pw.println("votos=" + j.getVotosKurultai());
                pw.println("localizacao=" + (j.getMeuPeao() == null ? "Karakorum" : j.getMeuPeao().getLocalizacaoAtual()));
                pw.println("espadas=" + j.getQuantidadeTributo(TipoTributo.ESPADA));
                pw.println("yurts=" + j.getQuantidadeTributo(TipoTributo.YURT));
                pw.println("moedas=" + j.getQuantidadeTributo(TipoTributo.MOEDA));
                pw.println("conselheiro=" + (j.getConselheiro() == null ? "" : j.getConselheiro()));
                pw.println("cartaSecreta=" + (j.getCartaSecreta() == null ? "" : j.getCartaSecreta()));
                StringBuilder sbCartas = new StringBuilder();
                List<String> cartas = j.getCartas();
                for (int k = 0; k < cartas.size(); k++) {
                    if (k > 0) sbCartas.append(",");
                    sbCartas.append(cartas.get(k));
                }
                pw.println("cartas=" + sbCartas.toString());
            }

            pw.println("\n[YURTS]");
            if (tabuleiro != null) {
                Map<String, Cor> yurts = tabuleiro.getYurts();
                for (Map.Entry<String, Cor> entry : yurts.entrySet()) {
                    pw.println(entry.getKey() + "=" + entry.getValue().name());
                }
            }

            pw.println("\n[CIDADES_TABULEIRO]");
            if (tabuleiro != null) {
                Map<NomeCidade, Cidade> cidades = tabuleiro.getCidadesDisponiveis();
                for (Cidade cid : cidades.values()) {
                    pw.println(cid.getNomeCidade().name() + ";" +
                               cid.getQuantidadeTesouros() + ";" +
                               cid.isConquistada() + ";" +
                               (cid.getConquistador() == null ? "null" : cid.getConquistador().name()));
                }
            }

            pw.println("\n[BARALHO_PILHA]");
            if (baralho != null) {
                StringBuilder sbBaralho = new StringBuilder();
                java.util.Deque<Cidade> pilha = baralho.getPilha();
                boolean primeiro = true;
                for (Cidade cid : pilha) {
                    if (!primeiro) sbBaralho.append(",");
                    sbBaralho.append(cid.getNomeCidade().name());
                    primeiro = false;
                }
                pw.println(sbBaralho.toString());
            }

            return true;
        } catch (java.io.IOException ex) {
            return false;
        }
    }

    public boolean carregar(String caminho) {
        if (caminho == null) return false;
        java.io.File f = new java.io.File(caminho);
        if (!f.exists() || !f.isFile()) return false;

        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(f), java.nio.charset.StandardCharsets.US_ASCII))) {

            String secao = "";
            String linha;
            Map<String, String> dadosPartida = new HashMap<String, String>();
            List<Cor> novaOrdemTurnos = new ArrayList<Cor>();
            Map<Cor, Jogador> novosJogadores = new HashMap<Cor, Jogador>();
            Map<String, Cor> novosYurts = new HashMap<String, Cor>();
            Map<NomeCidade, Cidade> novasCidades = new HashMap<NomeCidade, Cidade>();
            List<String> cartasBaralho = new ArrayList<String>();

            Jogador jogadorTemp = null;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;

                if (linha.startsWith("[") && linha.endsWith("]")) {
                    secao = linha.substring(1, linha.length() - 1);
                    continue;
                }

                if ("PARTIDA".equals(secao)) {
                    int eq = linha.indexOf('=');
                    if (eq > 0) {
                        dadosPartida.put(linha.substring(0, eq).trim(), linha.substring(eq + 1).trim());
                    }
                } else if ("ORDEM_TURNOS".equals(secao)) {
                    String[] tokens = linha.split(",");
                    for (String t : tokens) {
                        t = t.trim();
                        if (!t.isEmpty()) {
                            novaOrdemTurnos.add(Cor.valueOf(t));
                        }
                    }
                } else if ("JOGADORES".equals(secao)) {
                    if (linha.equals("---")) {
                        jogadorTemp = null;
                        continue;
                    }
                    int eq = linha.indexOf('=');
                    if (eq > 0) {
                        String chave = linha.substring(0, eq).trim();
                        String valor = linha.substring(eq + 1).trim();
                        if ("cor".equals(chave)) {
                            Cor cor = Cor.valueOf(valor);
                            jogadorTemp = new Jogador("", cor, 0);
                            novosJogadores.put(cor, jogadorTemp);
                        } else if (jogadorTemp != null) {
                            if ("nome".equals(chave)) {
                                jogadorTemp.setNome(valor);
                            } else if ("herdeiro".equals(chave)) {
                                jogadorTemp.setHerdeiro(valor.isEmpty() ? null : valor);
                            } else if ("descendentes".equals(chave)) {
                                jogadorTemp.setQuantidadeDescendentes(Integer.parseInt(valor));
                            } else if ("votos".equals(chave)) {
                                jogadorTemp.setVotosKurultai(Integer.parseInt(valor));
                            } else if ("localizacao".equals(chave)) {
                                jogadorTemp.getMeuPeao().mover(valor);
                            } else if ("espadas".equals(chave)) {
                                jogadorTemp.definirTributoDireto(TipoTributo.ESPADA, Integer.parseInt(valor));
                            } else if ("yurts".equals(chave)) {
                                jogadorTemp.definirTributoDireto(TipoTributo.YURT, Integer.parseInt(valor));
                            } else if ("moedas".equals(chave)) {
                                jogadorTemp.definirTributoDireto(TipoTributo.MOEDA, Integer.parseInt(valor));
                            } else if ("conselheiro".equals(chave)) {
                                jogadorTemp.setConselheiro(valor.isEmpty() ? null : valor);
                            } else if ("cartaSecreta".equals(chave)) {
                                jogadorTemp.setCartaSecreta(valor.isEmpty() ? null : valor);
                            } else if ("cartas".equals(chave)) {
                                jogadorTemp.limparCartas();
                                if (!valor.isEmpty()) {
                                    String[] cartas = valor.split(",");
                                    for (String c : cartas) {
                                        if (!c.trim().isEmpty()) {
                                            jogadorTemp.adicionarCarta(c.trim());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if ("YURTS".equals(secao)) {
                    int eq = linha.indexOf('=');
                    if (eq > 0) {
                        String loc = linha.substring(0, eq).trim();
                        Cor cor = Cor.valueOf(linha.substring(eq + 1).trim());
                        novosYurts.put(loc, cor);
                    }
                } else if ("CIDADES_TABULEIRO".equals(secao)) {
                    String[] partes = linha.split(";");
                    if (partes.length >= 4) {
                        NomeCidade nc = NomeCidade.valueOf(partes[0].trim());
                        Regiao r = Cidade.regiaoDe(nc);
                        Cidade c = new Cidade(nc, r);
                        c.setQuantidadeTesouros(Integer.parseInt(partes[1].trim()));
                        c.setConquistada(Boolean.parseBoolean(partes[2].trim()));
                        String conq = partes[3].trim();
                        if (!"null".equalsIgnoreCase(conq) && !conq.isEmpty()) {
                            c.setConquistador(Cor.valueOf(conq));
                        }
                        novasCidades.put(nc, c);
                    }
                } else if ("BARALHO_PILHA".equals(secao)) {
                    String[] tokens = linha.split(",");
                    for (String t : tokens) {
                        t = t.trim();
                        if (!t.isEmpty()) {
                            cartasBaralho.add(t);
                        }
                    }
                }
            }

            // Aplicar estado restaurado
            this.jogadores = novosJogadores;
            this.ordemTurnos = novaOrdemTurnos;
            this.indiceTurnoAtual = Integer.parseInt(dadosPartida.getOrDefault("indiceTurnoAtual", "0"));
            this.movimentosRestantes = Integer.parseInt(dadosPartida.getOrDefault("movimentosRestantes", "0"));
            this.partidaEncerrada = Boolean.parseBoolean(dadosPartida.getOrDefault("partidaEncerrada", "false"));
            this.podeSalvar = Boolean.parseBoolean(dadosPartida.getOrDefault("podeSalvar", "true"));

            String corAtualStr = dadosPartida.get("jogadorAtual");
            if (corAtualStr != null && !"null".equalsIgnoreCase(corAtualStr)) {
                this.jogadorAtual = this.jogadores.get(Cor.valueOf(corAtualStr));
            } else if (!this.ordemTurnos.isEmpty()) {
                this.jogadorAtual = this.jogadores.get(this.ordemTurnos.get(this.indiceTurnoAtual));
            }

            // Tabuleiro
            this.tabuleiro = new Tabuleiro(new ArrayList<Cidade>(novasCidades.values()));
            this.tabuleiro.limparYurts();
            for (Map.Entry<String, Cor> entry : novosYurts.entrySet()) {
                this.tabuleiro.construirYurt(entry.getKey(), entry.getValue());
            }
            String khanLoc = dadosPartida.getOrDefault("khanLocal", "Fora do Tabuleiro");
            if (this.tabuleiro.getPeaoDoKhan() != null) {
                this.tabuleiro.getPeaoDoKhan().mover(khanLoc);
            }

            // Baralho
            this.baralho = new BaralhoDeCidades();
            java.util.Deque<Cidade> novaPilha = new java.util.ArrayDeque<Cidade>();
            for (String ncStr : cartasBaralho) {
                try {
                    NomeCidade nc = NomeCidade.valueOf(ncStr);
                    novaPilha.addLast(new Cidade(nc, Cidade.regiaoDe(nc)));
                } catch (IllegalArgumentException e) {}
            }
            this.baralho.setPilha(novaPilha);

            this.exibirCartela = false;
            this.exibirCartas = false;
            this.exibirInventario = false;

            notificarObservadores();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void definirJogadorAtual(Jogador jogador) {
        if (jogador != null && this.jogadores.containsKey(jogador.getCor())) {
            this.jogadorAtual = jogador;
            this.indiceTurnoAtual = this.ordemTurnos.indexOf(jogador.getCor());
            notificarObservadores();
        }
    }
}