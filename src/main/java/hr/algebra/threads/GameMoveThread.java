package hr.algebra.threads;

import hr.algebra.Model.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class  GameMoveThread {
        private static boolean fileAccessInProgress=false;

private static final String MOVES_FILE="files/moves.dat";

public synchronized void saveMove(GameMove newGameMove){
        while (fileAccessInProgress){
                try {
                        wait();
                } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }
        }
        fileAccessInProgress=true;

        List<GameMove> gameMoveList = getAllMoves();
        gameMoveList.add(newGameMove);

        try(ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(MOVES_FILE))){
        oos.writeObject(gameMoveList);


        }catch (IOException ex){
        ex.printStackTrace();

        }
        fileAccessInProgress=false;
        notifyAll();
        }
private synchronized List<GameMove> getAllMoves(){

        List<GameMove> gameMoveList = new ArrayList<>();
        if(Files.exists(Path.of(MOVES_FILE))) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MOVES_FILE))) {
        gameMoveList.addAll((List<GameMove>) ois.readObject());

        } catch (IOException | ClassNotFoundException ex) {
        ex.printStackTrace();

        }
        }

        return gameMoveList;
        }
public synchronized GameMove getTheLastMove(){
        while (fileAccessInProgress){
                try {
                        wait();
                } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }
        }

        fileAccessInProgress=true;
        List<GameMove> gameMoveList = getAllMoves();
        fileAccessInProgress=false;
        notifyAll();
        return gameMoveList.get(gameMoveList.size()-1);

        }
}
