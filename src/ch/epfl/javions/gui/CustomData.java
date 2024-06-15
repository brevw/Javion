package ch.epfl.javions.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * Represents custom data with a title and associated object.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public class CustomData {
    private final SimpleObjectProperty<Object> data;
    private final SimpleStringProperty title;

    /**
     * Constructs a new CustomData object with the specified title and data.
     *
     * @param title the title of the custom data
     * @param data  the associated data object
     */
    public CustomData(String title, Object data){
        this.data = new SimpleObjectProperty<>(data);
        this.title = new SimpleStringProperty(title);
    }

    /**
     * Gets the data property associated with this CustomData object.
     *
     * @return the data property
     */
    public SimpleObjectProperty<Object> getData(){
        return data;
    }

    /**
     * Gets the title property associated with this CustomData object.
     *
     * @return the title property
     */
    public SimpleStringProperty getTitle(){
        return title;
    }
}
