package GUI.investor;

import Factory.IFactory;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Service.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.List;

public class Controller implements ITradeOrderSub {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;
    private ObservableList<TradeOrder> activeOrders;

    //TODO implement InvestorContainer

    @Override
    public void pushNewTradeOrders(List<TradeOrder> newTradeOrders) {

    }

    private void editBudgetButtonClicked() {

    }

    private void loginButtonClicked() {

    }

    public void addShutdownHook(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (factory != null) {
                    factory.destroy();
                }
            }
        });
    }
}
