package ufjf.trabalho01;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ufjf.trabalho01.jogo.HumanPlayer;
import ufjf.trabalho01.jogo.Player;
import ufjf.trabalho01.personagens.Arqueiro;
import ufjf.trabalho01.personagens.Guerreiro;
import ufjf.trabalho01.personagens.Mago;
import ufjf.trabalho01.personagens.Personagem;

import java.util.function.BiConsumer;

public class SelectionController {

    @FXML private TextField txtName1;
    @FXML private TextField txtName2;
    @FXML private ComboBox<String> cbClass1;
    @FXML private ComboBox<String> cbClass2;

    private BiConsumer<Player, Player> onStart;


    public void setOnStart(BiConsumer<Player, Player> onStart) {
        this.onStart = onStart;
    }

    @FXML
    public void initialize() {
        cbClass1.getItems().addAll("Arqueiro", "Guerreiro", "Mago");
        cbClass2.getItems().addAll("Arqueiro", "Guerreiro", "Mago");
        cbClass1.getSelectionModel().selectFirst();
        cbClass2.getSelectionModel().selectFirst();
    }

    @FXML
    private void onStart() {
        String nome1 = txtName1.getText().trim();
        String nome2 = txtName2.getText().trim();

        if (nome1.isEmpty() || nome2.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Por favor, preencha os nomes de ambos os jogadores.")
                    .showAndWait();
            return;
        }

        Player p1 = new HumanPlayer(nome1);
        Player p2 = new HumanPlayer(nome2);

        Personagem c1 = createCharacter(cbClass1.getValue(), nome1);
        Personagem c2 = createCharacter(cbClass2.getValue(), nome2);

        p1.setPersonagem(c1);
        p2.setPersonagem(c2);

        if (onStart != null) {
            onStart.accept(p1, p2);
        } else {
            System.err.println("Callback onStart não configurado!");
        }
    }

    private Personagem createCharacter(String tipo, String nome) {
        return switch (tipo) {
            case "Arqueiro"  -> new Arqueiro(nome);
            case "Guerreiro" -> new Guerreiro(nome);
            case "Mago"      -> new Mago(nome);
            default          -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
        };
    }
}
