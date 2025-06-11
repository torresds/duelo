package ufjf.trabalho01;

import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import ufjf.trabalho01.jogo.BotPlayer;
import ufjf.trabalho01.jogo.HumanPlayer;
import ufjf.trabalho01.jogo.Player;
import ufjf.trabalho01.jogo.TurnManager;
import ufjf.trabalho01.personagens.Personagem;

import java.util.List;
import java.util.Optional;

public class MainUIController {

    @FXML private GridPane gridPane;
    @FXML private Label lblNome;
    @FXML private Label lblVida;
    @FXML private Label lblAtaque;
    @FXML private Label lblDefesa;
    @FXML private Label lblAlcance;
    @FXML private Label lblTurno;
    @FXML private Label lblLog;

    @FXML private Button btnMover;
    @FXML private Button btnAtacar;
    @FXML private Button btnDefender;
    @FXML private Button btnPoder;

    private GridManager gridManager;
    private TurnManager turnManager;

    public void initPlayers(Player p1, Player p2) {
        gridManager = new GridManager();
        gridManager.generateGrid(gridPane);

        turnManager = new TurnManager(List.of(p1, p2));

        gridManager.adicionarPersonagem(p1.getPersonagem(), 0, 0);
        gridManager.adicionarPersonagem(
                p2.getPersonagem(),
                GridManager.GRID_SIZE - 1,
                GridManager.GRID_SIZE - 1
        );

        processTurn();
    }

    private void processTurn() {
        updateTurnDisplay();
        Player currentPlayer = turnManager.getJogadorAtual();

        if (currentPlayer instanceof BotPlayer) {
            setButtonsDisabled(true);
            lblLog.setText(currentPlayer.getNome() + " está pensando...");

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> executeBotTurn());
            pause.play();
        } else {
            setButtonsDisabled(false);
        }
    }

    private void executeBotTurn() {
        BotPlayer bot = (BotPlayer) turnManager.getJogadorAtual();
        Player oponente = turnManager.getNextPlayer();

        String logMessage = bot.chooseAndExecuteAction(gridManager, oponente);
        lblLog.setText(logMessage);

        if (checkVictory()) return;
        endTurn();
    }

    @FXML
    private void onMover() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mover");
        dialog.setHeaderText("Informe a direção: C (Cima), B (Baixo), E (Esquerda), D (Direita)");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().isBlank()) return;

        char dir = result.get().toUpperCase().charAt(0);
        boolean ok = ((HumanPlayer) turnManager.getJogadorAtual()).move(dir, gridManager);
        lblLog.setText(ok ? "Movimentou para " + result.get().toUpperCase() : "Movimento inválido!");
        if (ok) endTurn();
    }

    @FXML
    private void onAtacar() {
        HumanPlayer atacante = (HumanPlayer) turnManager.getJogadorAtual();
        Player alvo = turnManager.getNextPlayer();
        int dano = atacante.attack(alvo);

        if (dano < 0) {
            lblLog.setText("Alvo fora de alcance! Vez perdida.");
        } else {
            lblLog.setText(atacante.getNome() + " atacou com força de " + dano + ".");
        }
        if (checkVictory()) return;
        endTurn();
    }

    @FXML
    private void onDefender() {
        HumanPlayer atual = (HumanPlayer) turnManager.getJogadorAtual();
        atual.defend();
        lblLog.setText(atual.getNome() + " restaurou sua defesa!");
        endTurn();
    }

    @FXML
    private void onPoder() {
        HumanPlayer atacante = (HumanPlayer) turnManager.getJogadorAtual();
        Player alvo = turnManager.getNextPlayer();
        String msg = atacante.usePower(alvo);
        lblLog.setText(msg);

        if (checkVictory()) return;
        endTurn();
    }

    private void endTurn() {
        turnManager.nextTurn();
        processTurn();
    }

    private void updateTurnDisplay() {
        Player atual = turnManager.getJogadorAtual();
        Personagem p = atual.getPersonagem();

        lblTurno.setText("Turno: " + atual.getNome());
        lblNome.setText("Personagem: " + p.getNome() + " (" + p.getTipo() + ")");
        lblVida.setText("PV: " + p.getPontosDeVida());
        lblAtaque.setText("Ataque: " + p.getForcaDeAtaque());

        lblDefesa.setText("Defesa: " + p.getPontosDeDefesa() + "/" + p.getPontosDeDefesaMax());

        lblAlcance.setText("Alcance: " + p.getAlcanceDeAtaque());

        gridManager.clearHighlights();
        gridManager.getCell(p.getPosicaoX(), p.getPosicaoY())
                .setStyle("-fx-border-color: gold; -fx-border-width: 3;");
    }

    private boolean checkVictory() {
        Player oponente = turnManager.getNextPlayer();
        if (oponente.getPersonagem().getPontosDeVida() <= 0) {
            setButtonsDisabled(true);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fim de Jogo");
                alert.setHeaderText("Vitória de " + turnManager.getJogadorAtual().getNome() + "!");
                alert.showAndWait();
            });
            return true;
        }
        return false;
    }

    private void setButtonsDisabled(boolean disabled) {
        btnMover.setDisable(disabled);
        btnAtacar.setDisable(disabled);
        btnDefender.setDisable(disabled);
        btnPoder.setDisable(disabled);
    }
}