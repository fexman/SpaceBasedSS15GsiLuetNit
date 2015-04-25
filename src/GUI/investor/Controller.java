package GUI.investor;

import Factory.IFactory;
import Factory.XvsmFactory;
import MarketEntities.TradeOrderContainer;
import Model.Stock;
import Model.TradeOrder;
import javafx.collections.FXCollections;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private ComboBox<String> protocolField;
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
    private TableColumn colStockId;
    @FXML
    private TableColumn colStockCount;
    @FXML
    private TableColumn colStockMarketValue;
    @FXML
    private TableColumn colStockValue;
    @FXML
    private Button btnAddOrder;
    @FXML
    private TableView<TradeOrder> tabOrders;
    @FXML
    private TableColumn colOrderId;
    @FXML
    private TableColumn colOrderType;
    @FXML
    private TableColumn colOrderStockId;
    @FXML
    private TableColumn colOrderOrderLimit;
    @FXML
    private TableColumn colOrderOpenAmount;

    public Controller() {
        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_DELETED);
    }

    @FXML
    private void initialize() {
        ObservableList<String> protocols = FXCollections.observableArrayList();
        protocols.add("XVSM");
        protocols.add("RMI");
        protocolField.setItems(protocols);
        protocolField.setValue(protocols.get(0));

        // stocks table


        // orders table
    }

    private void populateStocksTable() {
    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {

    }

    private void editBudgetButtonClicked() {

    }

    private void loginButtonClicked() {

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
