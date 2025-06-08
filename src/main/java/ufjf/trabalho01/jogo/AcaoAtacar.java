package ufjf.trabalho01.jogo;

public class AcaoAtacar implements Acao {
    @Override
    public void executar(Player ator, Player oponente) {
        int dano = ator.getPersonagem().getForcaDeAtaque()
                - oponente.getPersonagem().getForcaDeDefesa();
        if (dano < 0) dano = 0;
        oponente.getPersonagem().receberDano(dano);
    }
}
