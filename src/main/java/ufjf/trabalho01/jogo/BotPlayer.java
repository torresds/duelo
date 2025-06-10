package ufjf.trabalho01.jogo;

import ufjf.trabalho01.GridManager;
import ufjf.trabalho01.personagens.Personagem;

public class BotPlayer extends Player {
    private Personagem oponente;
    private boolean usoupoderespecial = false;

    public BotPlayer(String nome, Personagem oponente) {
        super(nome);
        this.oponente = oponente;
    }

    /**
     * Move o personagem na direção indicada.
     *
     * @param gm o GridManager para validar e aplicar o movimento
     * @return true se moveu, false se inválido
     */

    public void ActionChooser() {
        if ((this.personagem.getTipo() == "Mago") && (this.personagem.getPontosDeVida() < this.oponente.getPontosDeVida() * 0.6)) {
            this.personagem.usarPoderEspecial(this.oponente);
        } else if ((this.personagem.getTipo() == "Guerreiro") && (!usoupoderespecial)) {
            this.personagem.usarPoderEspecial(this.oponente);
            usoupoderespecial = true;
        } else if ((this.personagem.getTipo() == "Arqueiro") && (this.personagem.calcularDistancia(this.oponente) > this.personagem.getAlcanceDeAtaque())) {
            this.personagem.usarPoderEspecial(this.oponente);
        } else if ((this.personagem.calcularDistancia(this.oponente) <= this.personagem.getAlcanceDeAtaque()) && (this.personagem.getForcaDeDefesa() > 0)) {
            this.attack();
        } else if (this.personagem.calcularDistancia(this.oponente) > this.personagem.getAlcanceDeAtaque()) {
            this.move(gm);
        } else {
            this.personagem.restoreDefense();
        }
    }


    public int attack() {
        int dano = oponente.diminuirDefesa(personagem.getForcaDeAtaque());
        oponente.receberDano(dano);
        return dano;
    }

    public boolean move(GridManager gm) {
        int x = personagem.getPosicaoX();
        int y = personagem.getPosicaoY();

        int ex = oponente.getPosicaoX();
        int ey = oponente.getPosicaoY();

        if (x + y % 2 == 0) {
            if (ex > x) x++;
            else if (ex < x) x--;
            else if (ey > y) y++;
            else if (ey < y) y--;
        } else {
            if (ey > y) y++;
            else if (ey < y) y--;
            else if (ex > x) x++;
            else if (ex < x) x--;
        }

        return gm.movePersonagem(personagem, x, y);
    }
}

