module hr.algebra.dicewarsgameclone {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.algebra.dicewarsgameclone to javafx.fxml;
    exports hr.algebra.dicewarsgameclone;
}