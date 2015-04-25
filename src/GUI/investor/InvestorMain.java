package GUI.investor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InvestorMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("investor.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        InvestorController controller = (InvestorController) fxmlLoader.getController();
        controller.addShutdownHook(primaryStage);
        primaryStage.setMinWidth(785d);
        primaryStage.setMinHeight(500d);
        primaryStage.setTitle("Investor");
        primaryStage.setScene(new Scene(root, 530, 575));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
