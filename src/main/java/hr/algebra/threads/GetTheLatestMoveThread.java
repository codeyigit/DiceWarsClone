package hr.algebra.threads;

import hr.algebra.Model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetTheLatestMoveThread extends GameMoveThread implements Runnable{


    private Label theLastMoveLabel;
    public GetTheLatestMoveThread(Label theLastMoveLabel){
        this.theLastMoveLabel=theLastMoveLabel;
    }

    @Override
    public void run() {
        while (true){
            Platform.runLater(() -> {
                GameMove theLastGameMove = getTheLastMove();

                theLastMoveLabel.setText("The last successful attack : " + theLastGameMove.getPlayer() + " X : " +
                        theLastGameMove.getLocation().getX() + " Y : " +
                        theLastGameMove.getLocation().getY() + " Time " + theLastGameMove.getDateTime());

            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
