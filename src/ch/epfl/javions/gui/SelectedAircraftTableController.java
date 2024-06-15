package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;


/**
 * Controller class for the selected aircraft table.
 * the table contains 5 rows with data ranging from graphical elements to strings
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public class SelectedAircraftTableController {
    private static final String API_URL = "https://aerodatabox.p.rapidapi.com/aircrafts/icao24/";
    private static final String PRIVATE_TOKEN = "6e47faf105mshcdc0bfb8061bfa3p1514bbjsn2e37842f952b";
    private final Pattern pattern = Pattern.compile("\\\"numSeats\\\":([0-9]+),\\\"rolloutDate\\\":\\\"[0-9\\-]+\\\",\\\"firstFlightDate\\\":\\\"([0-9\\-]+)\\\",\\\"deliveryDate\\\":\\\"[0-9\\-]+\\\",\\\"registrationDate\\\":\\\"[0-9\\-]+\\\",\\\"typeName\\\":\\\"[a-zA-Z \\(\\)0-9]+\\\",\\\"numEngines\\\":([0-9]+),\\\"engineType\\\":\\\"[a-zA-Z ]+\\\",\\\"isFreighter\\\":[a-zA-Z]+,\\\"productionLine\\\":\\\"[a-zA-Z 0-9]+\\\",\\\"ageYears\\\":([.0-9]+)");
    private final ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties;
    private final Pane pane;
    private ObservableList<CustomData> data;


    /**
     * Constructs a new SelectedAircraftTableController with the specified object property of observable aircraft state.
     *
     * @param aircraftWithVisibleProperties the object property of observable aircraft state
     */
    public SelectedAircraftTableController(ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties){
        this.aircraftWithVisibleProperties = aircraftWithVisibleProperties;
        TableView<CustomData> tableView = new TableView<>();
        pane = new Pane(tableView);
        tableView.prefWidthProperty().bind(pane.widthProperty());
        tableView.prefHeightProperty().bind(pane.heightProperty());
        data = null;
        try {
            data = FXCollections.observableArrayList(
                    new CustomData("pays", new Image(new FileInputStream("resources/flags-tiny/blank.png"))),
                    new CustomData("nbrSiege", null),
                    new CustomData("premier vol", null),
                    new CustomData("nbrMoteur", null),
                    new CustomData("age", null)
            );
        } catch (FileNotFoundException ignored) {}


        setUpTable(tableView);
        tableView.setItems(data);
        setUpListeners();



    }
    /**
     * Gets the pane associated with this SelectedAircraftTableController.
     *
     * @return pane (Pane)
     */
    public Pane pane(){
        return pane;
    }

    private void setUpListeners(){
        aircraftWithVisibleProperties.addListener((p,o,n) -> {
            if(data!=null){
                try {
                    data.get(0).getData().set(new Image(new FileInputStream("resources/flags-tiny/"+CountryFlags.fileNameOf(n.address()))));
                } catch (FileNotFoundException ignored) {}
                String requestURL = API_URL + n.address().string();
                try {
                    URL url = new URL(requestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-RapidAPI-Key", PRIVATE_TOKEN );

                    if (conn.getResponseCode() != 200)
                        apiNotWorkingOrDataNotFound();
                    else{
                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                        String output = br.readLine();
                        conn.disconnect();
                        Matcher matcher = pattern.matcher(output);
                        if(matcher.find()){
                            data.get(1).getData().set(matcher.group(1));
                            data.get(2).getData().set(matcher.group(2));
                            data.get(3).getData().set(matcher.group(3));
                            data.get(4).getData().set(matcher.group(4));
                        }else apiNotWorkingOrDataNotFound();

                    }


                } catch (IOException e){
                    throw new UncheckedIOException(e);
                }


            }
        });
    }

    private void setUpTable(TableView<CustomData> tableView){
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);


        TableColumn<CustomData, Object> values = new TableColumn<>();
        values.setStyle("-fx-alignment: CENTER;");
        values.setCellValueFactory(param -> param.getValue().getData());
        values.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (item instanceof Image image) {
                    imageView.setImage(image);
                    setGraphic(imageView);
                } else {
                    setText(item.toString());
                }
            }
        });

        TableColumn<CustomData, String> titles = new TableColumn<>();

        titles.setCellValueFactory(param -> param.getValue().getTitle());
        titles.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(titles, values);
    }


    private void apiNotWorkingOrDataNotFound() throws FileNotFoundException {
        data.get(0).getData().set(new Image(new FileInputStream("resources/flags-tiny/blank.png")));
        data.get(1).getData().set("");
        data.get(2).getData().set("");
        data.get(3).getData().set("");
        data.get(4).getData().set("");
    }
}
