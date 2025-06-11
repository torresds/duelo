package ufjf.trabalho01.personagens;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Mago extends Personagem {
    public Mago(String nome) {
        super("Mago", nome, 10, 7, 3); //Ataque: 10, Defesa: 7, Alcance: 3
    }


    @Override
    public String usarPoderEspecial(Personagem oponente) {
        int temp = this.pontosDeVida;
        this.pontosDeVida = oponente.pontosDeVida;
        oponente.pontosDeVida = temp;
        String message = nome + " usou 'Trocar Vida' com " + oponente.getNome() + "!";
        System.out.println(message);
        return message;
    }

    @Override
    protected Node criarView() {
        Image sprite = new Image(getClass().getResource("/ufjf/trabalho01/sprites/mago.png").toExternalForm());
        ImageView spriteView = new ImageView(sprite);

        spriteView.setFitWidth(40);
        spriteView.setFitHeight(40);
        spriteView.setPreserveRatio(true);

        return spriteView;
    }
}