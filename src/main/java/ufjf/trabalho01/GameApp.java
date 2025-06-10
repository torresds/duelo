package ufjf.trabalho01;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ufjf.trabalho01.jogo.Player;
import ufjf.trabalho01.jogo.HumanPlayer;
import ufjf.trabalho01.jogo.TurnManager;
import ufjf.trabalho01.jogo.Player;
import ufjf.trabalho01.SelectionController;
import ufjf.trabalho01.MainUIController;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;

public class GameApp extends GameApplication {

    private Parent selectionRoot;
    private SelectionController selectionCtrl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Duelo de Personagens");
        settings.setWidth(1024);
        settings.setHeight(600);
    }

    @Override
    protected void initUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ufjf/trabalho01/SelectionScreen.fxml")
            );

            selectionRoot = loader.load();
            selectionCtrl = loader.getController();
            selectionCtrl.setOnStart((p1, p2) -> startGame(p1, p2));

            getGameScene().addUINode(selectionRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startGame(Player p1, Player p2) {
        getGameScene().removeUINode(selectionRoot);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ufjf/trabalho01/MainUI.fxml")
            );
            Parent gameRoot = loader.load();
            MainUIController gameCtrl = loader.getController();

            getGameScene().addUINode(gameRoot);

            gameCtrl.initPlayers(p1, p2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
