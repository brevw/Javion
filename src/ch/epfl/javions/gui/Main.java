package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main extends Application {
    private static final int VBOX_DEFAULT_SPACING = 10;
    private static final long SECONDE_TO_NANOSECONDE = 1_000_000_000L;
    private final static int DEFAULT_ZOOM = 8, DEFAULT_MIN_X = 33_530, DEFAULT_MIN_Y = 23_070;
    private final static double DEFAULT_APPLICATION_WIDTH = 800d, DEFAULT_APPLICATION_HEIGHT = 600d;
    private final static long MICROSECONDE_NANOSECONDE = 1_000_000;
    private long lastTime = -1;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        // set up the database
        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path path = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(path.toString());
        AircraftStateManager asm = new AircraftStateManager(db);
        StatusLineController slc = new StatusLineController();

        primaryStage.setScene(new Scene(setUpPaneAndGet(asm, slc)));
        primaryStage.setTitle("Javion");
        primaryStage.setMinWidth(DEFAULT_APPLICATION_WIDTH);
        primaryStage.setMinHeight(DEFAULT_APPLICATION_HEIGHT);

        var appIcon = new Image("logo.png");
        primaryStage.getIcons().add(appIcon);


        //Set icon on the taskbar/dock
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage("resources/logo.png");
                taskbar.setIconImage(dockIcon);
            }

        }

        primaryStage.show();

        ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

        Thread thread = new Thread( () -> {
            System.out.println(getParameters().getRaw());
            if(getParameters().getRaw().size()!=0) readFromFile(queue);
            else readFromSystem(queue);

        });
        thread.setDaemon(true);
        thread.start();


        new AnimationTimer() {
            @Override
            public void handle(long now) {

                    while(!queue.isEmpty()) {
                        try {
                            asm.updateWithMessage(queue.poll());
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        slc.messageCountProperty().set(slc.messageCountProperty().get() + 1);
                    }
                    if (now - lastTime > SECONDE_TO_NANOSECONDE) {
                        asm.purge();
                        lastTime = now;
                    }


            }
        }.start();



    }


    private void readFromFile(ConcurrentLinkedQueue<Message> queue){
        try {
            try (DataInputStream s = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream("resources/"+getParameters().getRaw().get(0))
                    ))){

                byte[] bytes = new byte[RawMessage.LENGTH];

                long time = System.nanoTime();
                while (true){
                    long timeStampNs = s.readLong();
                    s.readNBytes(bytes, 0, bytes.length);
                    RawMessage rawMessage= new RawMessage(timeStampNs, new ByteString(bytes));
                    Message message = MessageParser.parse(rawMessage);

                    long waitingTime = ( timeStampNs - (System.nanoTime() - time) )/ MICROSECONDE_NANOSECONDE;
                    if (waitingTime>0)
                        try {
                            Thread.sleep(waitingTime);
                        } catch (InterruptedException e) {
                            throw new Error(e);
                            }

                    if(message != null) {
                        queue.offer(message);
                    }
                }
            }
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
    private void readFromSystem(ConcurrentLinkedQueue<Message> queue){
        try {
            AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
            while(true){
                RawMessage rawMessage = demodulator.nextMessage();
                if(rawMessage!=null){
                    Message message = MessageParser.parse(rawMessage);
                    if(message != null) {
                        queue.offer(message);
                    }
                }
            }
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
    private SplitPane setUpPaneAndGet(AircraftStateManager asm, StatusLineController slc){
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.VERTICAL);

        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(DEFAULT_ZOOM, DEFAULT_MIN_X,DEFAULT_MIN_Y);


        // selected aircraft
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();


        // aircraft and map
        BaseMapController bmc =new BaseMapController(tm, mp);
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);
        MapButtonsController mapButtonsController = new MapButtonsController(mp, bmc, sap);
        AntennaReceiver antennaReceiver = new AntennaReceiver(mp);
        var aircraftPlusMap = new StackPane(bmc.pane(), antennaReceiver.pane(), ac.pane() , mapButtonsController.pane());

        // status line and table
        slc.aircraftCountProperty().bind( Bindings.size(asm.states()) );
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        atc.setOnDoubleClick(state -> bmc.centerOn(state.getPosition()));
        var statusLinePlusTable = new BorderPane();
        statusLinePlusTable.setCenter(atc.pane());
        statusLinePlusTable.setTop(slc.pane());



        AircraftImagePreview aip = new AircraftImagePreview(sap);
        SelectedAircraftTableController satc = new SelectedAircraftTableController(sap);
        TextField textField1 = new TextField("antenne-longitudeT32");
        TextField textField2 = new TextField("antenne-latitudeT32");
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            try {
                antennaReceiver.antennaPosProperty().set(new GeoPos(Integer.parseInt(textField1.getText()), Integer.parseInt(textField2.getText())));
            }catch (NumberFormatException exception){
                antennaReceiver.antennaPosProperty().set(null);
            }
        });
        VBox updateAntennaButton = new VBox(VBOX_DEFAULT_SPACING, textField1, textField2, submitButton);
        updateAntennaButton.setAlignment(Pos.CENTER);
        updateAntennaButton.setMaxHeight(60);

        SplitPane leftSmallPanel = new SplitPane();
        leftSmallPanel.setOrientation(Orientation.VERTICAL);
        leftSmallPanel.getItems().addAll(aip.pane(), satc.pane(), updateAntennaButton);

        pane.getItems().addAll(aircraftPlusMap, statusLinePlusTable);
        SplitPane root = new SplitPane();
        root.setOrientation(Orientation.HORIZONTAL);
        root.getItems().addAll(leftSmallPanel, pane);
        Platform.runLater(() -> root.setDividerPositions(0.2));
        return root;
    }
}
