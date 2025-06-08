package ufjf.trabalho01.jogo;
import ufjf.trabalho01.personagens.Personagem;

public abstract class Player {
    protected String nome;
    protected Personagem personagem;

    public Player(String nome) {
        this.nome = nome;
    }

    public void setPersonagem(Personagem p) {
        this.personagem = p;
    }

    public String getNome() {
        return nome;
    }

    public Personagem getPersonagem() {
        return personagem;
    }

    public abstract Acao escolherAcao();
}
