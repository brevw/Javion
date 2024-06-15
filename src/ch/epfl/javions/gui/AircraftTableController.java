package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;


/**
 * This class is responsible for managing the display of aircraft data in a JavaFX TableView.
 * <p>
 * It provides a convenient way to visualize and interact with a set of aircraft states
 * <p>
 * and their associated information. The table can be sorted by any column and supports
 * <p>
 * double-click events for user interactions with the table rows.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public final class AircraftTableController {
    private static final int NUMERICAL_PREF_WIDTH = 85;
    private static final int DEFAULT_INT_FRACTIONAL_DIGITS = 0, DEFAULT_DOUBLE_FRACTIONAL_DIGITS = 4
            , UNUSED_FRACTIONAL_DIGITS = -1;
    private static final int ICAO_PREF_WIDTH = 60, CALL_SIGN_PREF_WIDTH = 70, REGISTRATION_PREF_WIDTH = 90,
            MODEL_PREF_WIDTH = 230, DESIGNATOR_PREF_WIDTH = 50, DESCRIPTION_PREF_WIDTH = 70;
    private final ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties;
    private final ObservableSet<ObservableAircraftState> states;
    private Consumer<ObservableAircraftState> onDoubleClickConsumer;
    private final TableView<ObservableAircraftState> tableView;

    /**
     * Constructs an AircraftTableController with the provided set of aircraft states and a property
     * representing the currently selected aircraft with visible properties.
     * @param states The observable set of aircraft states to be displayed in the TableView.
     * @param aircraftWithVisibleProperties The property representing the currently selected aircraft
     */

    public AircraftTableController(ObservableSet<ObservableAircraftState> states,
                                   ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties) {

        this.aircraftWithVisibleProperties = aircraftWithVisibleProperties;
        this.states = states;
        onDoubleClickConsumer = null;


        TableView<ObservableAircraftState> tableView = new TableView<>();
        this.tableView = tableView;
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        buildTable(tableView);
        tableView.getStylesheets().add("table.css");
        tableView.setTableMenuButtonVisible(true);

        tableView.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY && e.getClickCount()==2){
                ObservableAircraftState selectedAircraft = tableView.getSelectionModel().getSelectedItem();
                if(onDoubleClickConsumer!=null && selectedAircraft!= null) onDoubleClickConsumer.accept(selectedAircraft);
            }
        });

        setUpListeners(tableView);
    }

    /**
     * @return pane(Pane) containing the TableView
     */
    public TableView<ObservableAircraftState> pane(){
        return tableView;
    }

    /**
     * Sets a consumer function to handle double-click events on the TableView rows
     * @param consumer (Consumer) function that takes an ObservableAircraftState object as its input parameter
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        onDoubleClickConsumer = consumer;
    }

    private void buildTable(TableView<ObservableAircraftState> tableView){
        addColumn(tableView, "OACI", ICAO_PREF_WIDTH
                , false, UNUSED_FRACTIONAL_DIGITS
                ,  o -> new ReadOnlyObjectWrapper<>(o.address().string()) );

        addColumn(tableView, "Indicatif", CALL_SIGN_PREF_WIDTH
                , false, UNUSED_FRACTIONAL_DIGITS
                , o -> o.callSignProperty().map(CallSign::string) );

        addColumn(tableView, "Immatriculation", REGISTRATION_PREF_WIDTH
                , false, UNUSED_FRACTIONAL_DIGITS
                , o -> Optional.ofNullable(o.getAircraftData())
                        .map(aircraftData -> aircraftData.registration().string())
                        .map(ReadOnlyObjectWrapper::new)
                        .orElse(new ReadOnlyObjectWrapper<>(null)) );

        addColumn(tableView, "Modèle", MODEL_PREF_WIDTH
                , false,  UNUSED_FRACTIONAL_DIGITS
                , o -> Optional.ofNullable(o.getAircraftData())
                        .map(AircraftData::model)
                        .map(ReadOnlyObjectWrapper::new)
                        .orElse(new ReadOnlyObjectWrapper<>(null)) );

        addColumn(tableView, "Type", DESIGNATOR_PREF_WIDTH
                , false,  UNUSED_FRACTIONAL_DIGITS
                , o -> Optional.ofNullable(o.getAircraftData())
                        .map(aircraftData -> aircraftData.description().string())
                        .map(ReadOnlyObjectWrapper::new)
                        .orElse(new ReadOnlyObjectWrapper<>(null)) );

        addColumn(tableView, "Description", DESCRIPTION_PREF_WIDTH
                , false, UNUSED_FRACTIONAL_DIGITS
                , o -> Optional.ofNullable(o.getAircraftData())
                        .map(aircraftData -> aircraftData.description().string())
                        .map(ReadOnlyObjectWrapper::new)
                        .orElse(new ReadOnlyObjectWrapper<>(null)) );

        addColumn(tableView, "Longitude(°)", NUMERICAL_PREF_WIDTH
                , true, DEFAULT_DOUBLE_FRACTIONAL_DIGITS
                , o -> o.positionProperty()
                        .map(geoPos -> Units.convertTo(geoPos.longitude(), Units.Angle.DEGREE)) );

        addColumn(tableView, "Latitude(°)", NUMERICAL_PREF_WIDTH
                , true, DEFAULT_DOUBLE_FRACTIONAL_DIGITS
                , o -> o.positionProperty()
                        .map(geoPos -> Units.convertTo(geoPos.latitude(), Units.Angle.DEGREE)) );

        addColumn(tableView, "Altitude(m)", NUMERICAL_PREF_WIDTH
                , true, DEFAULT_INT_FRACTIONAL_DIGITS
                , o -> o.altitudeProperty().map(altitude -> Math.round((Double) altitude)) );



        addColumn(tableView, "Vitesse(km/h)", NUMERICAL_PREF_WIDTH
                , true, DEFAULT_INT_FRACTIONAL_DIGITS
                , o -> o.velocityProperty().map(velocity ->
                        Double.isNaN(velocity.doubleValue()) ? Double.NaN : Math.round(Units.convertTo((Double) velocity, Units.Speed.KILOMETER_PER_HOUR))
                ) );
    }

    /**
     *
     * The method first creates a new TableColumn object with the provided text and prefWidth.
     * If isNumeric is true, it sets up a number formatter to format the numerical data to be displayed in
     * the column. It then sets the cell factory for the column to use the number formatter and adds a style
     * class of "numeric" to the cell to allow for styling with CSS. If isNumeric is false, it sets the cell
     * value factory to use the function provided.
     * <p>
     * <Important> if the column is numerical and you want nothing displayed you need to return INVALID_NUMBER_ENTRY </Important>
     * @param tableView (TableView) representing the table to which the column will be added.
     * @param text (String) representing the text to be displayed in the column header.
     * @param prefWidth (int) representing the preferred width of the column.
     * @param isNumeric (boolean) indicating whether the column contains numerical data or not.
     * @param fractionDigits (int) representing the number of fractional digits to be displayed in the column.
     * @param function (Function) A Function that takes an ObservableAircraftState object and returns an
     *                 ObservableValue representing the value to be displayed in the column.
     */

    private void addColumn(TableView<ObservableAircraftState> tableView
            , String text
            , int prefWidth
            , boolean isNumeric
            , int fractionDigits
            , Function<ObservableAircraftState, ObservableValue<?>> function){

        //creating column
        TableColumn<ObservableAircraftState , String> column = new TableColumn<>(text);
        column.setPrefWidth(prefWidth);
        if(isNumeric) {
            //formatting conditions
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(fractionDigits);
            nf.setMaximumFractionDigits(fractionDigits);

            //putting styleClass as numeric
            column.getStyleClass().add("numeric");

            //setting comparator
            column.setComparator((s1, s2) -> {
                if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
                try {
                    return Double.compare(nf.parse(s1).doubleValue(), nf.parse(s2).doubleValue());
                } catch (ParseException ignored) {}
                //return anything (we are sure parsing is done without exception)
                return 1;
            });

            //setting setCellValueFactory
            column.setCellValueFactory( f -> function.apply(f.getValue()).map(number ->
                    Double.isNaN(((Number)number).doubleValue()) ? "" : nf.format(number)
            ));
        }else column.setCellValueFactory(f -> (ObservableValue<String>) function.apply(f.getValue()));

        tableView.getColumns().add(column);
    }

    private void setUpListeners(TableView<ObservableAircraftState> tableView){
        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            } else if(change.wasRemoved()) tableView.getItems().remove(change.getElementRemoved());
        });
        aircraftWithVisibleProperties.addListener((p,o,n) -> {
            if(!Objects.equals(tableView.getSelectionModel().getSelectedItem(), n)) {
                tableView.getSelectionModel().select(n);
                tableView.scrollTo(n);
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((p,o,n) -> aircraftWithVisibleProperties.set(n));
    }


}
