package edu.ktu.ds.lab2.vaitkevicius;

import edu.ktu.ds.lab2.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

/*
 * Darbo atlikimo tvarka - čia yra pradinė klasė.
 */
public class BookExecution extends Application {

    public static void main(String[] args) {
        BookExecution.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US); // Suvienodiname skaičių formatus 
        ManualTest.executeTest();
        MainWindow.createAndShowGui(primaryStage);
    }
}
