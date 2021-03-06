package GUI.investor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InvestorMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("investor.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        InvestorController controller = fxmlLoader.getController();
        controller.addShutdownHook(primaryStage);
        primaryStage.setTitle("Investor");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
