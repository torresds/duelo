
public class Guerreiro extends Personagem {
    public Guerreiro(String nome) {
        super(nome, 15, 10, 1); // Ataque: 15, Defesa: 10, Alcance: 1
    }

    @Override
    public void usarPoderEspecial(Personagem oponente) {
        this.forcaDeAtaque = 30;
        System.out.println(nome + " usou 'Carga Brutal'! Ataque aumentado para 30.");
    }
}
