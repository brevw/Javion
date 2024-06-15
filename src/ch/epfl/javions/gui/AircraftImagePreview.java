package ch.epfl.javions.gui;


import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 The AircraftImagePreview class is responsible for displaying an aircraft image preview based on its ICAO address.
 It retrieves the image from the server using the ICAO address and updates the preview accordingly.
 * @author: Tlili Ahmed (344939)
 * @author: Bouden Omar (341381)
 */
public class AircraftImagePreview {
    private static final Pattern pattern = Pattern.compile("\\\"thumbnail\\\":\\{\\\"src\\\":\\\"(https:\\\\/\\\\/[a-zA-Z.\\\\/0-9_\\-]+)\\\"");
    private static final String SERVER_NAME_WITH_ICAO_ADDRESS = "https://api.planespotters.net/pub/photos/hex/";

    private static final int IMAGE_FIXED_WIDTH = 200, IMAGE_FIXED_HEIGHT = 200;
    private static final double DEFAULT_SPACING = 10d;
    private final VBox pane;

    /**
     * Constructs an AircraftImagePreview object.
     * @param aircraftWithVisibleProperties An ObjectProperty representing the observable aircraft state.
     */
    public AircraftImagePreview(ObjectProperty<ObservableAircraftState> aircraftWithVisibleProperties) {
        ImageView flag = new ImageView();
        Canvas canvas = new Canvas(IMAGE_FIXED_WIDTH, IMAGE_FIXED_HEIGHT);
        Label label = new Label("rien n'est encore selectionné");



        pane = new VBox(label, canvas, flag);
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(DEFAULT_SPACING);

        aircraftWithVisibleProperties.addListener((p,o,n) -> {
            label.textProperty().set(null);
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            Image image = null;
            try {
                if(n.getAircraftData()!=null)
                    image = updateImage(n.address().string());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (InterruptedException e){
                throw new Error(e);
            }
            if(image!=null) {
                double x = (IMAGE_FIXED_WIDTH - image.getWidth())/2;
                double y = (IMAGE_FIXED_HEIGHT - image.getHeight())/2;
                canvas.getGraphicsContext2D().drawImage(image, x, y);
            }
        });

    }

    /**
     * Returns the VBox pane containing the aircraft image preview.
     */
    public VBox pane(){
        return pane;
    }

    public Image updateImage(String icaoString) throws IOException, InterruptedException {
            String url = SERVER_NAME_WITH_ICAO_ADDRESS+icaoString;
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Javion")
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(httpResponse.statusCode()!=200) return null;
            Matcher matcher = pattern.matcher(httpResponse.body());
            if(!matcher.find()) return null;

            URL u = new URL(matcher.group(1).replace("\\/","/"));
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");
            try(InputStream i = c.getInputStream()){
                return new Image(i);
            }
    }


}
