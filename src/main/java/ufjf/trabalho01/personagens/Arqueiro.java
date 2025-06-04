package ufjf.trabalho01.personagens;

public class Arqueiro extends Personagem {
    public Arqueiro(String nome) {
        super("Arqueiro", nome, 8, 5, 5); // Ataque: 8, Defesa: 5, Alcance: 5
    }

    @Override
    public void usarPoderEspecial(Personagem oponente) {
        this.alcanceDeAtaque += 1;
        System.out.println(nome + " usou 'Flecha Precisa'! Alcance aumentado para " + alcanceDeAtaque);
    }
}