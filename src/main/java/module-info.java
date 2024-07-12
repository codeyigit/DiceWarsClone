module hr.algebra.dicewarsgameclone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.xml;


    opens hr.algebra.dicewarsgameclone to javafx.fxml;
    exports hr.algebra.dicewarsgameclone;
    exports hr.algebra.chat to java.rmi;
}