package br.herdeiros.controller;

import br.herdeiros.view.JanelaPrincipal;
import br.herdeiros.view.PainelTabuleiro;

import java.awt.Color; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class JogoController {
    
    private JanelaPrincipal view;
    private int passosDisponiveis = 0; // Guarda a soma dos dados lançados

    public JogoController(JanelaPrincipal view) {
        this.view = view; 
        
        // Configura o ouvinte de cliques no tabuleiro
        this.view.getPainelTabuleiro().setOuvinteClique(new PainelTabuleiro.OuvinteCliqueTabuleiro() {
            @Override
            public void aoClicarNoTabuleiro(int x, int y) {
                // 1. Verifica se o jogador já lançou os dados antes de clicar no tabuleiro
                if (passosDisponiveis <= 0) {
                    System.out.println("Clique ignorado: você precisa lançar os dados primeiro!");
                    return;
                }

                // 2. Mapeia o clique (x, y) para uma casa/cômodo
                String destino = identificarCasaClicada(x, y);
                
                if (destino != null) {
                    // 3. Validação do movimento (Aqui integrará com o seu Model futuramente)
                    boolean movimentoValido = true; // Simulando movimento válido por enquanto
                    
                    if (movimentoValido) {
                        System.out.println("Movimento efetuado com sucesso para: " + destino + " (Gastou " + passosDisponiveis + " passos)");
                        
                        // Redesenha o tabuleiro para refletir a nova posição do pião
                        view.getPainelTabuleiro().repaint();
                        
                        // Zera os passos após o movimento para obrigar a rolar os dados novamente
                        passosDisponiveis = 0; 
                    } else {
                        // Movimento inválido: A regra exige que NÃO SE FAÇA NADA (sem mensagens de erro)[cite: 11]
                    }
                }
            }
        });
        
        iniciarControles();
    }

    private void iniciarControles() {
        this.view.getPainelControle().getBtnJogarDados().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lê os valores definidos pelo testador nas caixas JComboBox
                int dado1 = (int) view.getPainelControle().getComboDado1().getSelectedItem();
                int dado2 = (int) view.getPainelControle().getComboDado2().getSelectedItem();
                
                // Soma os dados para definir quantos passos o jogador pode andar
                passosDisponiveis = dado1 + dado2;
                
                // Envia os números e a cor do jogador para o painel desenhar
                view.getPainelControle().getPainelDados().atualizarDados(dado1, dado2, Color.BLUE);
                
                System.out.println("Dados lançados! Passos disponíveis para este turno: " + passosDisponiveis);
            }
        });
    }

    // Método auxiliar para traduzir as coordenadas (x, y) do ecrã numa casa do jogo
    private String identificarCasaClicada(int x, int y) {
        System.out.println("Coordenadas do clique recebidas -> X: " + x + ", Y: " + y);
        
        // Exemplo genérico de zona (pode ajustar depois conforme o seu tabuleiro)
        if (x >= 50 && x <= 250 && y >= 50 && y <= 250) {
            return "Cozinha";
        }
        
        return "Casa Genérica (X: " + x + ", Y: " + y + ")";
    }

	private Object println(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}