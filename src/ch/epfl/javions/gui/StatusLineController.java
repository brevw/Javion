package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController(){

        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();



        Text rightText = new Text();
        Text leftText = new Text();

        leftText.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "Aéronefs visibles : "+aircraftCountProperty.get(), aircraftCountProperty
                )
        );
        rightText.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "Messages reçus : "+messageCountProperty.get(), messageCountProperty
                )
        );

        pane = new BorderPane();
        pane.setRight(rightText);
        pane.setLeft(leftText);

        pane.getStylesheets().add("status.css");


    }


    public IntegerProperty aircraftCountProperty(){
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty(){
        return messageCountProperty;
    }

    public Pane pane(){
        return pane;
    }


}
