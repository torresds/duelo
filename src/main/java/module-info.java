module ufjf.trabalho01 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens ufjf.trabalho01 to javafx.fxml;
    exports ufjf.trabalho01;
    exports ufjf.trabalho01.personagens;
    opens ufjf.trabalho01.personagens to javafx.fxml;
}