public class Mago extends Personagem {
    public Mago(String nome) {
        super(nome, 10, 7, 3); //Ataque: 10, Defesa: 7, Alcance: 3
    }


  @Override
    public void usarPoderEspecial(Personagem oponente) {
        int temp = this.pontosDeVida;
        this.pontosDeVida = oponente.pontosDeVida;
        oponente.pontosDeVida = temp;
        System.out.println(nome + " usou 'Trocar Vida' com " + oponente.getNome() + "!");
    }
}