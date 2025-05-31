package ufjf.trabalho01;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainUIController {

    @FXML private Label lblNome;
    @FXML private Label lblVida;
    @FXML private Label lblAtaque;
    @FXML private Label lblDefesa;
    @FXML private Label lblAlcance;
    @FXML private Label lblLog;

    @FXML private Button btnMover;
    @FXML private Button btnAtacar;
    @FXML private Button btnDefender;
    @FXML private Button btnPoder;

    public void initialize() {
        System.out.println("[UI] Carregado.");
    }
}