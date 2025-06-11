package ufjf.trabalho01.personagens;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Guerreiro extends Personagem {
    public Guerreiro(String nome) {
        super("Guerreiro", nome, 15, 10, 1); // Ataque: 15, Defesa: 10, Alcance: 1
    }

    @Override
    public String usarPoderEspecial(Personagem oponente) {
        this.forcaDeAtaque = 30;
        String message = nome + " usou 'Carga Brutal'! Ataque aumentado para 30.";
        System.out.println(message);
        return message;
    }

    @Override
    public String getTipo() {
        return "Guerreiro";
    }

    @Override
    protected Node criarView() {
        Image sprite = new Image(getClass().getResource("/ufjf/trabalho01/sprites/guerreiro.png").toExternalForm());
        ImageView spriteView = new ImageView(sprite);

        spriteView.setFitWidth(40);
        spriteView.setFitHeight(40);
        spriteView.setPreserveRatio(true); // Mantém a proporção da imagem

        return spriteView;
    }

}