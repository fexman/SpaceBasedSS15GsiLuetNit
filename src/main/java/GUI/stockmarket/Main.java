package GUI.stockmarket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("marketInspector.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Controller controller = (Controller)fxmlLoader.getController();
        controller.addShutdownHook(primaryStage);
        primaryStage.setMinWidth(785d);
        primaryStage.setMinHeight(500d);
        primaryStage.setTitle("MarketInspector");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
