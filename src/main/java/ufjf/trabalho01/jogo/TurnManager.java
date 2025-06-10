package ufjf.trabalho01.jogo;

import java.util.List;
import java.util.Objects;

public class TurnManager {
    private final List<Player> jogadores;
    private int currentIndex = 0;

    public TurnManager(List<Player> jogadores) {
        Objects.requireNonNull(jogadores);
        if (jogadores.size() < 2)
            throw new IllegalArgumentException("São necessários 2+ jogadores");
        this.jogadores = jogadores;
    }

    public Player getJogadorAtual() {
        return jogadores.get(currentIndex);
    }

    public Player getNextPlayer() {
        return jogadores.get((currentIndex + 1) % jogadores.size());
    }

    public void nextTurn() {
        currentIndex = (currentIndex + 1) % jogadores.size();
    }
}
