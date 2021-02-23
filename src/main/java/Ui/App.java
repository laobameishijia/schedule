package Ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("国家助学贷款排班工具");
        primaryStage.getIcons().add(new Image("icon.jpg"));
        URL url = new File("src/main/resources/Ui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, 660, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        this.primaryStage = primaryStage;
    }
}