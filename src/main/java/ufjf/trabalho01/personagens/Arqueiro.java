package ufjf.trabalho01.personagens;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Arqueiro extends Personagem {
    public Arqueiro(String nome) {
        super("Arqueiro", nome, 8, 5, 5); // Ataque: 8, Defesa: 5, Alcance: 5
    }

    @Override
    public String usarPoderEspecial(Personagem oponente) {
        this.alcanceDeAtaque += 1;
        String message = nome + " usou 'Flecha Precisa'! Alcance aumentado para " + alcanceDeAtaque;
        System.out.println(message);
        return message;
    }

    @Override
    protected Node criarView() {
        Image sprite = new Image(getClass().getResource("/ufjf/trabalho01/sprites/arqueiro.png").toExternalForm());
        ImageView spriteView = new ImageView(sprite);

        spriteView.setFitWidth(40);
        spriteView.setFitHeight(40);
        spriteView.setPreserveRatio(true);

        return spriteView;
    }

}