package co.uniquindio.plataforma.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class plataformaApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(plataformaApp.class.getResource("/ventanas/inicio.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Agencia De Viajes");
        stage.show();
    }

    public static void main(String[] args) {
        launch(plataformaApp.class, args);
    }
}

