package ufjf.trabalho01.jogo;

import ufjf.trabalho01.GridManager;
import ufjf.trabalho01.personagens.Personagem;

public class HumanPlayer extends Player {
    public HumanPlayer(String nome) {
        super(nome);
    }

    /**
     * Move o personagem na direção indicada.
     * @param dir 'C','B','E','D'
     * @param gm o GridManager para validar e aplicar o movimento
     * @return true se moveu, false se inválido
     */
    public boolean move(char dir, GridManager gm) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();
        switch (Character.toUpperCase(dir)) {
            case 'C' -> y--;
            case 'B' -> y++;
            case 'E' -> x--;
            case 'D' -> x++;
            default  -> { return false; }
        }
        return gm.movePersonagem(personagem, x, y);
    }

    public int attack(Player oponente) {
        int dist = personagem.calcularDistancia(oponente.getPersonagem());
        if (dist > personagem.getAlcanceDeAtaque()) {
            return -1;
        }

        int danoTotal = personagem.getForcaDeAtaque();
        oponente.getPersonagem().sofrerDano(danoTotal);

        return danoTotal;
    }

    public void defend() {
        personagem.restoreDefense();
    }

    public String usePower(Player oponente) {
        return personagem.usarPoderEspecial(oponente.getPersonagem());
    }
}