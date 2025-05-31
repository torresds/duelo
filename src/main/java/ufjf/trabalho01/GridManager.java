package ufjf.trabalho01;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GridManager {

    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 48;

    private StackPane[][] cells = new StackPane[GRID_SIZE][GRID_SIZE];

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
