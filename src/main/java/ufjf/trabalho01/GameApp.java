package ufjf.trabalho01;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ufjf.trabalho01.personagens.Personagem;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {
    private MainUIController controller;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Duelo de Personagens");
        settings.setWidth(1024);
        settings.setHeight(600);
    }

    @Override
    protected void initUI() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ufjf/trabalho01/MainUI.fxml"));
            Parent ui = loader.load();
            controller = loader.getController();
            getGameScene().addUINode(ui);

        } catch (Exception e) {
            e.printStackTrace();
        }

        GridManager gridManager = new GridManager();
        gridManager.generateGrid();
    }

    public static void main(String[] args) {
        launch(args);
    }
}