package edu.ktu.ds.lab2.gui;

import edu.ktu.ds.lab2.utils.BstSet;
import edu.ktu.ds.lab2.vaitkevicius.Book;
import edu.ktu.ds.lab2.vaitkevicius.BooksGenerator;
import edu.ktu.ds.lab2.vaitkevicius.SimpleBenchmark;
import edu.ktu.ds.lab2.utils.ParsableAvlSet;
import edu.ktu.ds.lab2.utils.ParsableBstSet;
import edu.ktu.ds.lab2.utils.ParsableSortedSet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

/**
 * Lab2 langas su JavaFX
 * <pre>
 *                     MainWindow (BorderPane)
 *  |-------------------------Top-------------------------|
 *  |                   MainWindowMenu                    |
 *  |------------------------Center-----------------------|
 *  |  |-----------------------------------------------|  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                    taOutput                   |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |-----------------------------------------------|  |                                           |
 *  |------------------------Bottom-----------------------|
 *  |  |~~~~~~~~~~~~~~~~~~~paneBottom~~~~~~~~~~~~~~~~~~|  |
 *  |  |                                               |  |
 *  |  |  |-------------||------------||------------|  |  |
 *  |  |  | paneButtons || paneParam1 || paneParam2 |  |  |
 *  |  |  |             ||            ||            |  |  |
 *  |  |  |-------------||------------||------------|  |  |
 *  |  |                                               |  |
 *  |  |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|  |
 *  |-----------------------------------------------------|
 * </pre>
 *
 * @author darius.matulis@ktu.lt
 */
public class MainWindow extends BorderPane implements EventHandler<ActionEvent> {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("edu.ktu.ds.lab2.gui.messages");

    private static final int TF_WIDTH = 120;
    private static final int TF_WIDTH_SMALLER = 70;

    private static final double SPACING = 5.0;
    private static final Insets INSETS = new Insets(SPACING);
    private static final double SPACING_SMALLER = 2.0;
    private static final Insets INSETS_SMALLER = new Insets(SPACING_SMALLER);

    private final TextArea taOutput = new TextArea();
    private final GridPane paneBottom = new GridPane();
    private final GridPane paneParam2 = new GridPane();
    private final TextField tfDelimiter = new TextField();
    private final TextField tfInput = new TextField();
    private final ComboBox cmbTreeType = new ComboBox();

    private Panels paneParam1, paneButtons;
    private MainWindowMenu mainWindowMenu;
    private final Stage stage;

    private static ParsableSortedSet<Book> booksSet;
    private BooksGenerator booksGenerator = new BooksGenerator();

    private int sizeOfInitialSubSet, sizeOfGenSet, sizeOfLeftSubSet;
    private double shuffleCoef;
    private final String[] errors;

    public MainWindow(Stage stage) {
        this.stage = stage;
        errors = new String[]{
                MESSAGES.getString("badSetSize"),
                MESSAGES.getString("badInitialData"),
                MESSAGES.getString("badSetSizes"),
                MESSAGES.getString("badShuffleCoef")
        };
        initComponents();
    }

    private void initComponents() {
        //======================================================================
        // Sudaromas rezultatų išvedimo VBox klasės objektas, kuriame
        // talpinamas Label ir TextArea klasės objektai
        //======================================================================        
        VBox vboxTaOutput = new VBox();
        vboxTaOutput.setPadding(INSETS_SMALLER);
        VBox.setVgrow(taOutput, Priority.ALWAYS);
        vboxTaOutput.getChildren().addAll(new Label(MESSAGES.getString("border1")), taOutput);
        //======================================================================
        // Formuojamas mygtukų tinklelis (mėlynas). Naudojama klasė Panels.
        //======================================================================
        paneButtons = new Panels(
                new String[]{
                        MESSAGES.getString("button1"),
                        MESSAGES.getString("button2"),
                        MESSAGES.getString("button3"),
                        MESSAGES.getString("button4"),
                        MESSAGES.getString("button5"),
                        MESSAGES.getString("button6"),
                        MESSAGES.getString("button7"),
                        MESSAGES.getString("button8"),
                        MESSAGES.getString("button9"),
                        MESSAGES.getString("button10"),
                        MESSAGES.getString("button11"),
                        MESSAGES.getString("button12"),
                        MESSAGES.getString("button13")},
                2, 8);
        disableButtons(true);
        //======================================================================
        // Formuojama pirmoji parametrų lentelė (žalia). Naudojama klasė Panels.
        //======================================================================
        paneParam1 = new Panels(
                new String[]{
                        MESSAGES.getString("lblParam11"),
                        MESSAGES.getString("lblParam12"),
                        MESSAGES.getString("lblParam13"),
                        MESSAGES.getString("lblParam14"),
                        MESSAGES.getString("lblParam15")},
                new String[]{
                        MESSAGES.getString("tfParam11"),
                        MESSAGES.getString("tfParam12"),
                        MESSAGES.getString("tfParam13"),
                        MESSAGES.getString("tfParam14"),
                        MESSAGES.getString("tfParam15")},
                TF_WIDTH_SMALLER);
        //======================================================================
        // Formuojama antroji parametrų lentelė (gelsva).
        //======================================================================
        paneParam2.setAlignment(Pos.CENTER);
        paneParam2.setNodeOrientation(NodeOrientation.INHERIT);
        paneParam2.setVgap(SPACING);
        paneParam2.setHgap(SPACING);
        paneParam2.setPadding(INSETS);

        paneParam2.add(new Label(MESSAGES.getString("lblParam21")), 0, 0);
        paneParam2.add(new Label(MESSAGES.getString("lblParam22")), 0, 1);
        paneParam2.add(new Label(MESSAGES.getString("lblParam23")), 0, 2);

        cmbTreeType.setItems(FXCollections.observableArrayList(
                MESSAGES.getString("cmbTreeType1"),
                MESSAGES.getString("cmbTreeType2"),
                MESSAGES.getString("cmbTreeType3")
        ));
        cmbTreeType.setPrefWidth(TF_WIDTH);
        cmbTreeType.getSelectionModel().select(0);
        paneParam2.add(cmbTreeType, 1, 0);

        tfDelimiter.setPrefWidth(TF_WIDTH);
        tfDelimiter.setAlignment(Pos.CENTER);
        paneParam2.add(tfDelimiter, 1, 1);

        // Vėl pirmas stulpelis, tačiau plotis - 2 celės
        tfInput.setEditable(false);
        tfInput.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam2.add(tfInput, 0, 3, 2, 1);
        //======================================================================
        // Formuojamas bendras parametrų panelis (tamsiai pilkas).
        //======================================================================
        paneBottom.setPadding(INSETS);
        paneBottom.setHgap(SPACING);
        paneBottom.setVgap(SPACING);
        paneBottom.add(paneButtons, 0, 0);
        paneBottom.add(paneParam1, 1, 0);
        paneBottom.add(paneParam2, 2, 0);
        paneBottom.alignmentProperty().bind(new SimpleObjectProperty<>(Pos.CENTER));

        mainWindowMenu = new MainWindowMenu() {
            @Override
            public void handle(ActionEvent ae) {
                Region region = (Region) taOutput.lookup(".content");
                region.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

                try {
                    Object source = ae.getSource();
                    if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(0))) {
                        fileChooseMenu();
                    } else if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(1))) {
                        KsGui.ounerr(taOutput, MESSAGES.getString("notImplemented"));
                    } else if (source.equals(mainWindowMenu.getMenus().get(0).getItems().get(3))) {
                        System.exit(0);
                    } else if (source.equals(mainWindowMenu.getMenus().get(1).getItems().get(0))) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle(MESSAGES.getString("menuItem21"));
                        alert.setHeaderText(MESSAGES.getString("author"));
                        alert.showAndWait();
                    }
                } catch (ValidationException e) {
                    KsGui.ounerr(taOutput, e.getMessage());
                } catch (Exception e) {
                    KsGui.ounerr(taOutput, MESSAGES.getString("systemError"));
                    e.printStackTrace(System.out);
                }
                KsGui.setFormatStartOfLine(false);
            }
        };

        //======================================================================
        // Formuojamas Lab2 langas
        //======================================================================          
        // ..viršuje, centre ir apačioje talpiname objektus..
        setTop(mainWindowMenu);
        setCenter(vboxTaOutput);

        VBox vboxPaneBottom = new VBox();
        VBox.setVgrow(paneBottom, Priority.ALWAYS);
        vboxPaneBottom.getChildren().addAll(new Label(MESSAGES.getString("border2")), paneBottom);
        setBottom(vboxPaneBottom);
        appearance();

        paneButtons.getButtons().forEach(btn -> btn.setOnAction(this));
        cmbTreeType.setOnAction(this);
    }

    private void appearance() {
        paneButtons.setBackground(new Background(new BackgroundFill(Color.rgb(112, 162, 255)/* Blyškiai mėlyna */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam1.setBackground(new Background(new BackgroundFill(Color.rgb(204, 255, 204)/* Šviesiai žalia */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam1.getTfOfTable().get(2).setEditable(false);
        paneParam1.getTfOfTable().get(2).setStyle("-fx-text-fill: red");
        paneParam1.getTfOfTable().get(4).setEditable(false);
        paneParam1.getTfOfTable().get(4).setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam2.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 153)/* Gelsva */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneBottom.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        taOutput.setFont(Font.font("Monospaced", 12));
        taOutput.setStyle("-fx-text-fill: black;");
        taOutput.setEditable(false);
    }

    @Override
    public void handle(ActionEvent ae) {
        try {
            System.gc();
            System.gc();
            System.gc();
            Region region = (Region) taOutput.lookup(".content");
            region.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

            Object source = ae.getSource();
            if (source instanceof Button) {
                handleButtons(source);
            } else if (source instanceof ComboBox && source.equals(cmbTreeType)) {
                disableButtons(true);
            }
        } catch (ValidationException e) {
            if (e.getCode() >= 0 && e.getCode() <= 3) {
                KsGui.ounerr(taOutput, errors[e.getCode()] + ": " + e.getMessage());
                if (e.getCode() == 2) {
                    paneParam1.getTfOfTable().get(0).setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    paneParam1.getTfOfTable().get(1).setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            } else if (e.getCode() == 4) {
                KsGui.ounerr(taOutput, MESSAGES.getString("allSetIsPrinted"));
            } else {
                KsGui.ounerr(taOutput, e.getMessage());
            }
        } catch (UnsupportedOperationException e) {
            KsGui.ounerr(taOutput, e.getLocalizedMessage());
        } catch (Exception e) {
            KsGui.ounerr(taOutput, MESSAGES.getString("systemError"));
            e.printStackTrace(System.out);
        }
    }

    private void handleButtons(Object source) throws ValidationException {
        if (source.equals(paneButtons.getButtons().get(0))) {
            treeGeneration(null);
        } else if (source.equals(paneButtons.getButtons().get(1))) {
            treeIteration();
        } else if (source.equals(paneButtons.getButtons().get(2))) {
            treeAdd();
        } else if (source.equals(paneButtons.getButtons().get(3))) {
            treeEfficiency();
        } else if (source.equals(paneButtons.getButtons().get(4))) {
            treeRemove();
        } else if (source.equals(paneButtons.getButtons().get(5))) {
            addAll();
         } else if (source.equals(paneButtons.getButtons().get(6))){
            higher();
         } else if (source.equals(paneButtons.getButtons().get(7))){
            pollLast();
         } else if (source.equals(paneButtons.getButtons().get(8))){
            lower();
         } else if (source.equals(paneButtons.getButtons().get(9))){
            retainAll();
         } else if (source.equals(paneButtons.getButtons().get(10))){
            headSet();
         } else if (source.equals(paneButtons.getButtons().get(11))){
            setMethods();
         } else if (source.equals(paneButtons.getButtons().get(12))){
            DepthComparedToElements();
         } 
    }
    
    private void addAll() {
        ParsableBstSet<Book> newSet = (ParsableBstSet<Book>) booksSet;
        BstSet<Book> elementsToAdd = new BstSet<>();
        Arrays.stream(booksGenerator.generateShuffle(3, 3, shuffleCoef)).forEach(elementsToAdd::add);
        KsGui.oun(taOutput, "Elementai, kuriuos prides");
        KsGui.oun(taOutput, elementsToAdd.toVisualizedString(tfDelimiter.getText()));
        if (booksSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
        } else {
            newSet.addAll(elementsToAdd);
            KsGui.oun(taOutput, newSet.toVisualizedString(tfDelimiter.getText()));
        }
    }
    
    private void higher() {
        ParsableBstSet<Book> testSet = (ParsableBstSet<Book>) booksSet;
        Book higher = testSet.higher((Book) booksSet.toArray()[new Random().nextInt(booksSet.size())]);
        try
        {
            KsGui.oun(taOutput, higher, "higher metodas:");
        }
        catch(Exception e)
        {
            KsGui.oun(taOutput, "Nerastas elementas");
        }
    }
    
    private void pollLast() {
        ParsableBstSet<Book> testSet = (ParsableBstSet<Book>) booksSet;
        
        KsGui.oun(taOutput, testSet.toVisualizedString(tfDelimiter.getText()), "Pries pollLast() metodo:");
        if (testSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
            return;
        }
        Book books = testSet.pollLast();
        
        KsGui.oun(taOutput, testSet.toVisualizedString(tfDelimiter.getText()), "Po pollLast() metodo:");
    }
    
    private void lower() {
        
        ParsableBstSet<Book> testSet = (ParsableBstSet<Book>) booksSet;
        Book lower = testSet.lower((Book) booksSet.toArray()[new Random().nextInt(booksSet.size())]);
        try
        {
            KsGui.oun(taOutput, lower, "lower metodas:");
        }
        catch(Exception e)
        {
            KsGui.oun(taOutput, "Nerastas elementas");
        }
    }
    
    private void retainAll() {
        ParsableBstSet<Book> newSet = (ParsableBstSet<Book>) booksSet;
        BstSet<Book> elementsToAdd = new BstSet<>();
        elementsToAdd.add((Book)newSet.toArray()[(int)booksSet.size()/2]);
        elementsToAdd.add(newSet.first());
        //Arrays.stream(booksGenerator.generateShuffle(3, 3, shuffleCoef)).forEach(elementsToAdd::add);
        KsGui.oun(taOutput, "Elementai, kuriuos lygins");
        KsGui.oun(taOutput, elementsToAdd.toVisualizedString(tfDelimiter.getText()));
        if (booksSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
        } else {
            KsGui.oun(taOutput, "Set, pries retainAll()");
            KsGui.oun(taOutput, newSet.toVisualizedString(tfDelimiter.getText()));
            newSet.retainAll(elementsToAdd);
            KsGui.oun(taOutput, "Set, po retainAll()");
            KsGui.oun(taOutput, newSet.toVisualizedString(tfDelimiter.getText()));
        }
    }
    
    private void headSet() {
        ParsableBstSet<Book> testSet = (ParsableBstSet<Book>) booksSet;
        edu.ktu.ds.lab2.utils.SortedSet<Book> sortedSet;
        Book end = (Book)testSet.toArray()[(int)booksSet.size()/2];
        
        KsGui.oun(taOutput, "Vidurinis");
        KsGui.oun(taOutput, end.ToString_data1());
        if (testSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
        } else {
            KsGui.setFormatStartOfLine(true);
            KsGui.oun(taOutput, "Po Metodo: ");
            sortedSet = testSet.headSet(end, true);
            KsGui.oun(taOutput, sortedSet.toVisualizedString(tfDelimiter.getText()));
            KsGui.setFormatStartOfLine(false);
        }
    }
    
    private void setMethods() {
        KsGui.setFormatStartOfLine(true);
        ParsableBstSet<Book> testSet = (ParsableBstSet<Book>) booksSet;
        Book start = testSet.first();
        Book end = (Book)testSet.toArray()[(int)testSet.size()/2];
        edu.ktu.ds.lab2.utils.Set<Book> headSet = testSet.headSet(end);
        edu.ktu.ds.lab2.utils.Set<Book> tailSet = testSet.tailSet(start);
        edu.ktu.ds.lab2.utils.Set<Book> subSet = testSet.subSet(start, end);
        KsGui.oun(taOutput, start, "Set metodu pradzia. Startinis elementas:");
        KsGui.oun(taOutput, end, "Vidurinis dvejetainio medzio elementas:");
        KsGui.oun(taOutput, headSet.toVisualizedString(tfDelimiter.getText()), "headSet(). Argumentas: vidurinis:");
        KsGui.oun(taOutput, tailSet.toVisualizedString(tfDelimiter.getText()), "tailSet(). Argumentas: startinis:");
        KsGui.oun(taOutput, subSet.toVisualizedString(tfDelimiter.getText()), "subSet()");
        KsGui.setFormatStartOfLine(false);
    }
    
    private void DepthComparedToElements() {
    KsGui.setFormatStartOfLine(true);
        BstSet<Book> bst = new BstSet<>();
        Book[] books = booksGenerator.generateShuffle(10000, 10000, shuffleCoef);
        KsGui.oun(taOutput, books.length);
        for(int i = 0; i < books.length; i++)
        {
            bst.add(books[i]);
        }
        
        KsGui.oun(taOutput, "Elementu kiekio kiekviename medzio gylyje tyrimas. Dydis: " + bst.size());
        ArrayList<Integer> bstArray = bst.depthCount();
        KsGui.oun(taOutput, "Gylis      BST");
        for(int i = 0; i<20; i++)
        {
            KsGui.oun(taOutput, String.format("%5d      %3d", i, bstArray.get(i)));
        }
        KsGui.setFormatStartOfLine(false);
    }

    private void treeGeneration(String filePath) throws ValidationException {
        // Nuskaitomi parametrai
        readTreeParameters();
        // Sukuriamas aibės objektas, priklausomai nuo medžio pasirinkimo
        // cmbTreeType objekte
        createTree();

        Book[] booksArray;
        // Jei failas nenurodytas - generuojama
        if (filePath == null) {
            booksArray = booksGenerator.generateShuffle(sizeOfGenSet, sizeOfInitialSubSet, shuffleCoef);
            paneParam1.getTfOfTable().get(2).setText(String.valueOf(sizeOfLeftSubSet));
        } else { // Skaitoma is failo
            booksSet.load(filePath);
            booksArray = new Book[booksSet.size()];
            int i = 0;
            for (Object o : booksSet.toArray()) {
                booksArray[i++] = (Book) o;
            }
            // Skaitant iš failo išmaišoma standartiniu Collections.shuffle metodu.
            Collections.shuffle(Arrays.asList(booksArray), new Random());
        }

        // Išmaišyto masyvo elementai surašomi i aibę
        booksSet.clear();
        Arrays.stream(booksArray).forEach(booksSet::add);

        // Išvedami rezultatai
        // Nustatoma, kad eilutės pradžioje neskaičiuotų išvedamų eilučių skaičiaus
        KsGui.setFormatStartOfLine(true);
        KsGui.oun(taOutput, booksSet.toVisualizedString(tfDelimiter.getText()),
                MESSAGES.getString("setInTree"));
        // Nustatoma, kad eilutės pradžioje skaičiuotų išvedamų eilučių skaičių
        KsGui.setFormatStartOfLine(false);
        disableButtons(false);
    }

    private void treeAdd() throws ValidationException {
        KsGui.setFormatStartOfLine(true);
        Book book = booksGenerator.takeBook();
        booksSet.add(book);
        paneParam1.getTfOfTable().get(2).setText(String.valueOf(--sizeOfLeftSubSet));
        KsGui.oun(taOutput, book, MESSAGES.getString("setAdd"));
        KsGui.oun(taOutput, booksSet.toVisualizedString(tfDelimiter.getText()));
        KsGui.setFormatStartOfLine(false);
    }

    private void treeRemove() {
        KsGui.setFormatStartOfLine(true);
        if (booksSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
            KsGui.oun(taOutput, booksSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            int nr = new Random().nextInt(booksSet.size());
            Book book = (Book) booksSet.toArray()[nr];
            booksSet.remove(book);
            KsGui.oun(taOutput, book, MESSAGES.getString("setRemoval"));
            KsGui.oun(taOutput, booksSet.toVisualizedString(tfDelimiter.getText()));
        }
        KsGui.setFormatStartOfLine(false);
    }

    private void treeIteration() {
        KsGui.setFormatStartOfLine(true);
        if (booksSet.isEmpty()) {
            KsGui.ounerr(taOutput, MESSAGES.getString("setIsEmpty"));
        } else {
            KsGui.oun(taOutput, booksSet, MESSAGES.getString("setIterator"));
        }
        KsGui.setFormatStartOfLine(false);
    }

    private void treeEfficiency() {
        KsGui.setFormatStartOfLine(true);
        KsGui.oun(taOutput, "", MESSAGES.getString("benchmark"));
        paneBottom.setDisable(true);
        mainWindowMenu.setDisable(true);

        BlockingQueue<String> resultsLogger = new SynchronousQueue<>();
        Semaphore semaphore = new Semaphore(-1);
        SimpleBenchmark simpleBenchmark = new SimpleBenchmark(resultsLogger, semaphore);

        // Ši gija paima rezultatus iš greitaveikos tyrimo gijos ir išveda
        // juos į taOutput. Gija baigia darbą kai gaunama FINISH_COMMAND
        new Thread(() -> {
            try {
                String result;
                while (!(result = resultsLogger.take())
                        .equals(SimpleBenchmark.FINISH_COMMAND)) {
                    KsGui.ou(taOutput, result);
                    semaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            semaphore.release();
            paneBottom.setDisable(false);
            mainWindowMenu.setDisable(false);
        }, "Greitaveikos_rezultatu_gija").start();

        //Šioje gijoje atliekamas greitaveikos tyrimas
        new Thread(simpleBenchmark::startBenchmark, "Greitaveikos_tyrimo_gija").start();
    }

    private void readTreeParameters() throws ValidationException {
        // Truputėlis kosmetikos..
        for (int i = 0; i < 2; i++) {
            paneParam1.getTfOfTable().get(i).setStyle("-fx-control-inner-background: white; ");
            paneParam1.getTfOfTable().get(i).applyCss();
        }
        // Nuskaitomos parametrų reiksmės. Jei konvertuojant is String
        // įvyksta klaida, sugeneruojama NumberFormatException situacija. Tam, kad
        // atskirti kuriame JTextfield'e ivyko klaida, panaudojama nuosava
        // situacija MyException
        int i = 0;
        try {
            // Pakeitimas (replace) tam, kad sukelti situaciją esant
            // neigiamam skaičiui        
            sizeOfGenSet = Integer.parseInt(paneParam1.getParametersOfTable().get(i).replace("-", "x"));
            sizeOfInitialSubSet = Integer.parseInt(paneParam1.getParametersOfTable().get(++i).replace("-", "x"));
            sizeOfLeftSubSet = sizeOfGenSet - sizeOfInitialSubSet;
            ++i;
            shuffleCoef = Double.parseDouble(paneParam1.getParametersOfTable().get(++i).replace("-", "x"));
        } catch (NumberFormatException e) {
            // Galima ir taip: pagauti exception'ą ir vėl mesti
            throw new ValidationException(paneParam1.getParametersOfTable().get(i), e, i);
        }
    }

    private void createTree() throws ValidationException {
        switch (cmbTreeType.getSelectionModel().getSelectedIndex()) {
            case 0:
                booksSet = new ParsableBstSet<>(Book::new);
                break;
            case 1:
                booksSet = new ParsableAvlSet<>(Book::new);
                break;
            default:
                disableButtons(true);
                throw new ValidationException(MESSAGES.getString("notImplemented"));
        }
    }

    private void disableButtons(boolean disable) {
        for (int i : new int[]{1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}) {
            if (i < paneButtons.getButtons().size() && paneButtons.getButtons().get(i) != null) {
                paneButtons.getButtons().get(i).setDisable(disable);
            }
        }
    }

    private void fileChooseMenu() throws ValidationException {
        FileChooser fc = new FileChooser();
        // Papildoma mūsų sukurtais filtrais
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("txt", "*.txt")
        );
        fc.setTitle((MESSAGES.getString("menuItem11")));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            treeGeneration(file.getAbsolutePath());
        }
    }

    public static void createAndShowGui(Stage stage) {
        Locale.setDefault(Locale.US); // Suvienodiname skaičių formatus
        MainWindow window = new MainWindow(stage);
        stage.setScene(new Scene(window, 1100, 650));
        stage.setTitle(MESSAGES.getString("title"));
        stage.getIcons().add(new Image("file:" + MESSAGES.getString("icon")));
        stage.show();
    }
}
