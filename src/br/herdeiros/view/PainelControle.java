package br.herdeiros.view;

import br.herdeiros.model.Partida;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Painel lateral de controle da partida.
 * Oferece ações de turno (Encerrar Turno) e modo testador (forçar quantidade de movimentos/cavalos).
 * Os dados foram completamente removidos de acordo com as regras de Herdeiros de Khan.
 */
public class PainelControle extends JPanel {

    private Partida partida;
    private PainelStatusTurno painelStatus;
    private JButton btnEncerrarTurno;
    private JButton btnVerCartelas;
    private JButton btnVerCartas;
    private JButton btnVerInventario;
    private JComboBox<Integer> comboMovimentos;
    private JButton btnDefinirMovimentos;

    public PainelControle(Partida partida) {
        this.partida = partida;

        setPreferredSize(new Dimension(300, 1050));
        setBackground(new Color(240, 240, 245));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));

        // Título principal do painel lateral
        JLabel lblTitulo = new JLabel("HERDEIROS DE KHAN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(40, 40, 40));
        add(lblTitulo);

        // 1. Painel com status visual do jogador da vez e movimentos restantes
        painelStatus = new PainelStatusTurno(partida);
        painelStatus.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Turno / Estado da Jogada",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(80, 80, 80)
        ));
        add(painelStatus);

        // 2. Botão principal para encerrar o turno do jogador atual
        btnEncerrarTurno = new JButton("Encerrar Turno");
        btnEncerrarTurno.setPreferredSize(new Dimension(260, 45));
        btnEncerrarTurno.setFont(new Font("Arial", Font.BOLD, 14));
        btnEncerrarTurno.setBackground(new Color(41, 128, 185));
        btnEncerrarTurno.setForeground(Color.WHITE);
        btnEncerrarTurno.setFocusPainted(false);
        add(btnEncerrarTurno);

        // 2.1 Botões auxiliares para Cartelas, Cartas e Inventário
        btnVerCartelas = new JButton("Ver Meu Tabuleiro (Cartela)");
        btnVerCartelas.setPreferredSize(new Dimension(260, 34));
        btnVerCartelas.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerCartelas.setBackground(new Color(52, 73, 94));
        btnVerCartelas.setForeground(Color.WHITE);
        btnVerCartelas.setFocusPainted(false);
        add(btnVerCartelas);

        btnVerCartas = new JButton("Ver Minhas Cartas (Conselheiros)");
        btnVerCartas.setPreferredSize(new Dimension(260, 34));
        btnVerCartas.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerCartas.setBackground(new Color(142, 68, 173));
        btnVerCartas.setForeground(Color.WHITE);
        btnVerCartas.setFocusPainted(false);
        add(btnVerCartas);

        btnVerInventario = new JButton("Ver Meu Inventário");
        btnVerInventario.setPreferredSize(new Dimension(260, 34));
        btnVerInventario.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerInventario.setBackground(new Color(39, 174, 96));
        btnVerInventario.setForeground(Color.WHITE);
        btnVerInventario.setFocusPainted(false);
        add(btnVerInventario);

        // 3. Painel do Modo Testador (permite forçar a quantidade de movimentos de cavalos)
        JPanel painelTestador = new JPanel();
        painelTestador.setPreferredSize(new Dimension(260, 110));
        painelTestador.setBackground(Color.WHITE);
        painelTestador.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Modo Testador (Cavalos / Movimento)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11),
                new Color(100, 100, 100)
        ));

        Integer[] opcoesCavalos = {1, 2, 3, 4, 5, 6};
        comboMovimentos = new JComboBox<Integer>(opcoesCavalos);
        comboMovimentos.setPreferredSize(new Dimension(70, 28));

        btnDefinirMovimentos = new JButton("Definir Movimentos");
        btnDefinirMovimentos.setPreferredSize(new Dimension(240, 32));
        btnDefinirMovimentos.setFont(new Font("Arial", Font.PLAIN, 12));

        painelTestador.add(new JLabel("Cavalos:"));
        painelTestador.add(comboMovimentos);
        painelTestador.add(btnDefinirMovimentos);

        add(painelTestador);
    }

    public JButton getBtnEncerrarTurno() {
        return btnEncerrarTurno;
    }

    public JButton getBtnVerCartelas() {
        return btnVerCartelas;
    }

    public JButton getBtnVerCartas() {
        return btnVerCartas;
    }

    public JButton getBtnVerInventario() {
        return btnVerInventario;
    }

    public JComboBox<Integer> getComboMovimentos() {
        return comboMovimentos;
    }

    public JButton getBtnDefinirMovimentos() {
        return btnDefinirMovimentos;
    }

    public PainelStatusTurno getPainelStatus() {
        return painelStatus;
    }
}