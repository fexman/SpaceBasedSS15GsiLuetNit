package GUI.investor;

import Factory.IFactory;
import MarketEntities.TradeOrderContainer;
import Model.Stock;
import Model.TradeOrder;
import Service.Subscribing.TradeOrders.ITradeOrderSub;
import Util.XvsmUtil;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.List;

public class Controller implements ITradeOrderSub {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;
    private ObservableList<TradeOrder> activeOrders;
    private final TradeOrder ORDER_FILTER;

    @FXML
    private Label txtStatus;
    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField txtBudget;
    @FXML
    private Button btnEditBudget;
    @FXML
    private TextField txtTotalStockValue;
    @FXML
    private TableView<Stock> tabStocks;
    @FXML
    private Button btnAddOrder;
    @FXML
    private TableView<TradeOrder> tabOrders;

    public Controller() {
        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_DELETED);
    }

    @Override
    public void pushNewTradeOrders(List<TradeOrder> newTradeOrders) {

    }

    private void editBudgetButtonClicked() {

    }

    private void loginButtonClicked() {
        // TODO check input; when user with given id is new
    }

    private void addOrderButtonClicked() {
        //TODO open new_order shit
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
