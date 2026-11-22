package br.herdeiros.controller;

import br.herdeiros.model.Partida;
import br.herdeiros.observer.ObservadorIF;
import br.herdeiros.view.JanelaEscolhaPersonagens;
import br.herdeiros.view.JanelaMenuInicial;
import br.herdeiros.view.JanelaPrincipal;

import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Controlador da aplicação (Controller do MVC) - Singleton.
 *
 * Único responsável por organizar a sequência de eventos da partida:
 * menu inicial -> escolha de personagens -> partida (ou carregar jogo salvo).
 * A View aciona as regras chamando JogoController.getInstance().xxx(), e o
 * Controller repassa para a fachada do Model (Partida.getInstance()). As Views
 * se registram como observadoras do Model exclusivamente através de
 * {@link #registra(ObservadorIF)}.
 *
 * SEM uso de expressões lambda em listeners (regra obrigatória da disciplina).
 */
public class JogoController {

    private static final String ARQUIVO_PADRAO = "herdeiros_save.dat";

    private static JogoController instancia;

    private JanelaPrincipal janelaPrincipal;

    private JogoController() {
    }

    public static synchronized JogoController getInstance() {
        if (instancia == null) {
            instancia = new JogoController();
        }
        return instancia;
    }

    private Partida model() {
        return Partida.getInstance();
    }

    // ------------------------------------------------------------------
    // Observer: registro dos observadores (Views) no observado (Model)
    // ------------------------------------------------------------------

    public void registra(ObservadorIF o) {
        model().add(o);
        // Sincroniza imediatamente o observador recém-registrado com o estado atual
        o.notify(model());
    }

    public void remove(ObservadorIF o) {
        model().remove(o);
    }

    // ------------------------------------------------------------------
    // Sequência de eventos da aplicação
    // ------------------------------------------------------------------

    public void iniciar() {
        abrirMenuInicial();
    }

    private void abrirMenuInicial() {
        final JanelaMenuInicial[] refMenu = new JanelaMenuInicial[1];
        refMenu[0] = new JanelaMenuInicial(new JanelaMenuInicial.OuvinteMenuInicial() {
            @Override
            public void aoIniciarNovoJogo() {
                refMenu[0].dispose();
                abrirEscolhaPersonagens();
            }

            @Override
            public void aoContinuarJogoSalvo() {
                JFileChooser chooser = new JFileChooser(new File("."));
                chooser.setDialogTitle("Carregar partida salva");
                chooser.setSelectedFile(new File(ARQUIVO_PADRAO));
                if (chooser.showOpenDialog(refMenu[0]) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                if (model().carregar(chooser.getSelectedFile().getAbsolutePath())) {
                    refMenu[0].dispose();
                    abrirJanelaPrincipal();
                    verificarFim();
                } else {
                    JOptionPane.showMessageDialog(refMenu[0],
                            "Não foi possível carregar o arquivo selecionado.",
                            "Carregar Jogo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refMenu[0].exibir();
    }

    private void abrirEscolhaPersonagens() {
        final JanelaEscolhaPersonagens[] refEscolha = new JanelaEscolhaPersonagens[1];
        refEscolha[0] = new JanelaEscolhaPersonagens(new JanelaEscolhaPersonagens.OuvinteConfiguracaoJogo() {
            @Override
            public void aoConfirmarConfiguracao(List<JanelaEscolhaPersonagens.DadosJogadorConfig> jogadores) {
                refEscolha[0].dispose();
                novaPartida(jogadores);
            }

            @Override
            public void aoVoltarMenu() {
                refEscolha[0].dispose();
                abrirMenuInicial();
            }
        });
        refEscolha[0].exibir();
    }

    private void novaPartida(List<JanelaEscolhaPersonagens.DadosJogadorConfig> configs) {
        Partida p = model();
        p.reiniciar();
        for (JanelaEscolhaPersonagens.DadosJogadorConfig cfg : configs) {
            p.adicionarJogador(cfg.nome, cfg.cor, cfg.descendentes);
            p.definirHerdeiro(cfg.cor, cfg.herdeiro);
        }
        // Regra oficial: o jogador com o maior número de descendentes começa
        p.definirPrimeiroJogadorPorDescendentes();
        escolherConselheiros();
        abrirJanelaPrincipal();
    }

    /** Cada jogador escolhe qual das suas cartas é o conselheiro; a outra é a carta secreta. */
    private void escolherConselheiros() {
        Partida p = model();
        for (int i = 0; i < p.get(br.herdeiros.observer.IndicesEstado.QTD_JOGADORES); i++) {
            List<String> cartas = p.getCartasJogadorPorIndice(i);
            if (cartas.size() < 2) {
                continue;
            }
            String[] opcoes = new String[cartas.size()];
            for (int k = 0; k < opcoes.length; k++) {
                opcoes[k] = "Carta " + (k + 1) + " (" + cartas.get(k) + ")";
            }
            int escolha = JOptionPane.showOptionDialog(null,
                    p.getNomeJogadorPorIndice(i) + ": escolha qual carta será o seu CONSELHEIRO.\n"
                            + "A outra carta será a sua CARTA SECRETA.",
                    "Escolha do conselheiro e da carta secreta",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
            p.escolherConselheiro(i, escolha < 0 ? 0 : escolha);
        }
    }

    private void abrirJanelaPrincipal() {
        if (janelaPrincipal == null) {
            janelaPrincipal = new JanelaPrincipal();
        }
        janelaPrincipal.exibir();
    }

    // ------------------------------------------------------------------
    // Regras de jogo acionadas pela View
    // ------------------------------------------------------------------

    /**
     * Clique do mouse sobre uma localidade do tabuleiro (única forma de mover o
     * peão).
     */
    public void localidadeClicada(String localidade, boolean moverKhan) {
        if (localidade == null) {
            return;
        }
        if (moverKhan) {
            model().moverKhan(localidade);
            return;
        }
        if (!model().moverJogadorDaVez(localidade)) {
            System.out.println("[DEBUG] Movimento recusado: passos insuficientes ou local desconectado.");
        }
    }

    public void encerrarTurno() {
        model().passarTurno();
    }

    public void definirMovimentos(int quantidade) {
        model().definirMovimentos(quantidade);
    }

    public boolean construirYurt() {
        return model().construirYurtNaLocalizacaoAtual();
    }

    public boolean saquearCidade() {
        boolean ok = model().saquearCidadeNaLocalizacaoAtual();
        verificarFim();
        return ok;
    }

    public void coletarTributo(int indiceTipo, int quantidade) {
        model().coletarTributoJogadorAtual(indiceTipo, quantidade);
    }

    public void exibirCartela() {
        model().setExibirCartela(true);
    }

    public void fecharCartela() {
        model().setExibirCartela(false);
    }

    public void exibirCartas() {
        model().setExibirCartas(true);
    }

    public void fecharCartas() {
        model().setExibirCartas(false);
    }

    public void exibirInventario() {
        model().setExibirInventario(true);
    }

    public void fecharInventario() {
        model().setExibirInventario(false);
    }

    // ------------------------------------------------------------------
    // Salvamento e fim de partida
    // ------------------------------------------------------------------

    /** Pergunta o arquivo e salva a partida atual. */
    public void salvarPartida() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Salvar partida");
        chooser.setSelectedFile(new File(ARQUIVO_PADRAO));
        if (chooser.showSaveDialog(janelaPrincipal) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        boolean ok = model().salvar(chooser.getSelectedFile().getAbsolutePath());
        JOptionPane.showMessageDialog(janelaPrincipal,
                ok ? "Partida salva com sucesso." : "Erro ao salvar a partida.",
                "Salvar Partida",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    /** Encerra a partida por decisão dos jogadores e mostra o vencedor. */
    public void encerrarPartida() {
        model().encerrarPartida();
        verificarFim();
    }

    private void verificarFim() {
        if (model().isPartidaEncerrada()) {
            JOptionPane.showMessageDialog(janelaPrincipal, model().getResumoFinal(),
                    "Fim da partida - Herdeiros de Khan", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Dados textuais (nomes) - a View não acessa o Model diretamente
    // ------------------------------------------------------------------

    public String getNomeJogador(int indiceJogador) {
        return model().getNomeJogadorPorIndice(indiceJogador);
    }

    public String getHerdeiro(int indiceJogador) {
        return model().getHerdeiroPorIndice(indiceJogador);
    }

    public String getNomeCor(int ordinalCor) {
        return Partida.getNomeCor(ordinalCor);
    }

    public String[] getNomesTributos() {
        return Partida.getNomesTributos();
    }
}
