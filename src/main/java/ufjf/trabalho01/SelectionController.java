package ufjf.trabalho01;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ufjf.trabalho01.jogo.BotPlayer;
import ufjf.trabalho01.jogo.HumanPlayer;
import ufjf.trabalho01.jogo.Player;
import ufjf.trabalho01.personagens.Arqueiro;
import ufjf.trabalho01.personagens.Guerreiro;
import ufjf.trabalho01.personagens.Mago;
import ufjf.trabalho01.personagens.Personagem;

import java.util.Random;
import java.util.function.BiConsumer;

public class SelectionController {

    @FXML private TextField txtName1;
    @FXML private TextField txtName2;
    @FXML private ComboBox<String> cbClass1;
    @FXML private ComboBox<String> cbClass2;
    @FXML private ComboBox<String> cbDifficulty;
    @FXML private RadioButton rbPvP;
    @FXML private RadioButton rbPvC;
    @FXML private ToggleGroup modeGroup;

    private BiConsumer<Player, Player> onStart;

    public void setOnStart(BiConsumer<Player, Player> onStart) {
        this.onStart = onStart;
    }

    @FXML
    public void initialize() {
        modeGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == rbPvC) {
                txtName2.setText("Bot");
                txtName2.setDisable(true);

                cbClass2.setDisable(false);

                cbDifficulty.setVisible(true);
            } else {
                txtName2.clear();
                txtName2.setDisable(false);
                cbClass2.setDisable(false);
                cbDifficulty.setVisible(false);
            }
        });

        cbClass1.getSelectionModel().selectFirst();
        cbClass2.getSelectionModel().selectFirst();
        cbDifficulty.getSelectionModel().selectFirst();

        rbPvP.setSelected(true);
        txtName2.setDisable(false);
        cbClass2.setDisable(false);
        cbDifficulty.setVisible(false);
    }


    @FXML
    private void onStart() {
        String nome1 = txtName1.getText().trim();
        if (nome1.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Por favor, preencha o nome do Jogador 1.").showAndWait();
            return;
        }

        Player p1 = new HumanPlayer(nome1);
        Personagem c1 = createCharacter(cbClass1.getValue(), nome1);
        p1.setPersonagem(c1);

        Player p2;

        if (rbPvC.isSelected()) {
            String botName = "Bot";
            String difficulty = cbDifficulty.getValue();
            p2 = new BotPlayer(botName, c1, difficulty);

            String botClass = cbClass2.getValue();
            if (botClass == null) {
                new Alert(Alert.AlertType.WARNING, "Por favor, selecione uma classe para o Bot.").showAndWait();
                return;
            }
            Personagem c2 = createCharacter(botClass, botName);
            p2.setPersonagem(c2);

        } else {
            String nome2 = txtName2.getText().trim();
            if (nome2.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Por favor, preencha o nome do Jogador 2.").showAndWait();
                return;
            }
            if (cbClass2.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Por favor, selecione uma classe para o Jogador 2.").showAndWait();
                return;
            }
            p2 = new HumanPlayer(nome2);
            Personagem c2 = createCharacter(cbClass2.getValue(), nome2);
            p2.setPersonagem(c2);
        }

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