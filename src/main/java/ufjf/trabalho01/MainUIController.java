package ufjf.trabalho01;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
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
    private List<Player> jogadores;


    public void initPlayers(Player p1, Player p2) {
        gridManager = new GridManager();
        gridManager.generateGrid(gridPane);

        jogadores   = List.of(p1, p2);
        turnManager = new TurnManager(jogadores);

        gridManager.adicionarPersonagem(p1.getPersonagem(), 0, 0);
        gridManager.adicionarPersonagem(
                p2.getPersonagem(),
                GridManager.GRID_SIZE - 1,
                GridManager.GRID_SIZE - 1
        );

        updateTurnDisplay();
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
        lblLog.setText(ok
                ? "Movimentou para " + result.get().toUpperCase()
                : "Movimento inválido!");
        if (ok) endTurn();
    }

    @FXML
    private void onAtacar() {
        Player atacante = turnManager.getJogadorAtual();
        Player alvo     = turnManager.getNextPlayer();
        int dano = ((HumanPlayer) atacante).attack(alvo);

        if (dano < 0) {
            lblLog.setText("Alvo fora de alcance! Vez perdida.");
            endTurn();
            return;
        }

        lblLog.setText(atacante.getNome() + " causou " + dano + " de dano.");
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
        Player atacante = turnManager.getJogadorAtual();
        Player alvo     = turnManager.getNextPlayer();
        String msg = ((HumanPlayer) atacante).usePower(alvo);
        lblLog.setText(msg);

        if (checkVictory()) return;
        endTurn();
    }

    // ——— Helpers ———

    private void endTurn() {
        turnManager.nextTurn();
        updateTurnDisplay();
    }

    private void updateTurnDisplay() {
        Player atual    = turnManager.getJogadorAtual();
        Personagem p    = atual.getPersonagem();

        lblTurno.setText("Turno: " + atual.getNome());
        lblNome .setText("Personagem: " + atual.getNome());
        lblVida .setText("PV: "      + p.getPontosDeVida());
        lblAtaque.setText("Ataque: " + p.getForcaDeAtaque());
        lblDefesa.setText("Defesa: " + p.getForcaDeDefesa());
        lblAlcance.setText("Alcance: " + p.getAlcanceDeAtaque());

        gridManager.clearHighlights();
        gridManager.getCell(p.getPosicaoX(), p.getPosicaoY())
                .setStyle("-fx-border-color: gold; -fx-border-width: 3;");
    }

    private boolean checkVictory() {
        Player derrotado = turnManager.getNextPlayer();
        if (derrotado.getPersonagem().getPontosDeVida() <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fim de Jogo");
            alert.setHeaderText("Vitória de " + turnManager.getJogadorAtual().getNome() + "!");
            alert.showAndWait();

            btnMover.setDisable(true);
            btnAtacar.setDisable(true);
            btnDefender.setDisable(true);
            btnPoder.setDisable(true);
            return true;
        }
        return false;
    }
}
