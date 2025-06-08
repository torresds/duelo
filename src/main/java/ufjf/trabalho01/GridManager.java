package ufjf.trabalho01;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import ufjf.trabalho01.personagens.Mago;
import ufjf.trabalho01.personagens.Personagem;

import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;


public class GridManager {

    public static final int GRID_SIZE = 10;
    public static final int CELL_SIZE = 48;

    private StackPane[][] cells = new StackPane[GRID_SIZE][GRID_SIZE];
    private Map<Posicao, Personagem> personagens = new HashMap<>();
    private static class Posicao {
        int x,y;
        Posicao(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof  Posicao)) return false;
            Posicao p = (Posicao) o;
            return p.x == this.x && p.y == this.y;
        }
        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    public boolean adicionarPersonagem(Personagem personagem, int x, int y) {
        if (x < 0 || x > GRID_SIZE  || y < 0 || y >= GRID_SIZE) {
            return false;
        }

        Posicao pos = new Posicao(x,y);
        if (personagens.containsKey(pos)) return false;
        personagem.setPosicaoX(x);
        personagem.setPosicaoY(y);
        Node view = personagem.getView();
        StackPane cell = this.getCell(x,y);
        cell.getChildren().add(view);

        view.setTranslateX(0);
        view.setTranslateY(0);
        return true;
    }

    public void generateGrid() {
        double gridWidth = GRID_SIZE * CELL_SIZE;
        double gridHeight = GRID_SIZE * CELL_SIZE;

        double offsetX = (getAppWidth() - gridWidth) / 2.0;
        double offsetY = (getAppHeight() - gridHeight) / 2.0;

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                StackPane cell = createCell(x, y);
                cell.setTranslateX(offsetX + x * CELL_SIZE);
                cell.setTranslateY(offsetY + y * CELL_SIZE);

                getGameScene().addUINode(cell);
                cells[y][x] = cell;
            }
        }

        // Testes (remover)
        adicionarPersonagem(new Mago("Mago"), 0, 0);
        adicionarPersonagem(new Mago("Mago"), 5, 5);
    }


    private StackPane createCell(int x, int y) {
        Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
        background.setArcWidth(6);
        background.setArcHeight(6);
        background.setStroke(Color.DARKSLATEGRAY);
        background.setFill((x + y) % 2 == 0 ? Color.web("#2e2e2e") : Color.web("#3e3e3e"));

        Text coordinates = new Text(x + "," + y);
        coordinates.setFill(Color.GRAY);
        coordinates.setFont(Font.font("Consolas", 10));

        StackPane cell = new StackPane(background, coordinates);
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        return cell;
    }

    public StackPane getCell(int x, int y) {
        return cells[y][x];
    }

}
