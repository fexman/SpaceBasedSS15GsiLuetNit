package GUI.stockmarket;

import Factory.IFactory;
import Factory.XvsmFactory;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.MarketValue;
import Model.TradeOrder;
import Service.ConnectionError;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.List;

public class Controller implements ITradeOrderSub, IStockPricesSub {

    private IFactory factory;

    private TradeOrderContainer ordersContainer;
    private ObservableList<TradeOrder> orders;
    private final TradeOrder ORDER_FILTER;

    private StockPricesContainer stockPricesContainer;
    private ObservableList<MarketValue> stockPrices;

    public Controller() {
        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_DELETED);
    }

    @FXML
    private TextField adressField;

    @FXML
    private ComboBox<String> protocolField;

    @FXML
    private Button connectButton;

    @FXML
    private TableView<TradeOrder> tableOrders;

    @FXML
    private TableView<MarketValue> tableStockPrices;

    //TODO: TABLEVIEW FOR HISTORY AND STOCK PRICES

    @FXML
    private void initialize() {

        //Init protocolField
        ObservableList<String> protocols = FXCollections.observableArrayList();
        protocols.add("XVSM");
        protocols.add("RMI");
        protocolField.setItems(protocols);
        protocolField.setValue(protocols.get(0));

        //Init personTable
        TableColumn orders_idCol = new TableColumn("ID");
        orders_idCol.setPrefWidth(125d);
        orders_idCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("id"));
        TableColumn orders_typeCol = new TableColumn("Type");
        orders_typeCol.setPrefWidth(40d);
        orders_typeCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, TradeOrder.Type>("type"));
        TableColumn orders_investorIdCol = new TableColumn("Investor ID");
        orders_investorIdCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("investorId"));
        TableColumn orders_companyIdCol = new TableColumn("Company ID");
        orders_companyIdCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("companyId"));
        TableColumn orders_totalAmountCol = new TableColumn("Total");
        orders_totalAmountCol.setPrefWidth(40d);
        orders_totalAmountCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Integer>("totalAmount"));
        TableColumn orders_completedAmountCol = new TableColumn("Completed");
        orders_completedAmountCol.setPrefWidth(80d);
        orders_completedAmountCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Integer>("completedAmount"));
        TableColumn orders_priceLimitCol = new TableColumn("Price");
        orders_priceLimitCol.setPrefWidth(50d);
        orders_priceLimitCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Double>("priceLimit"));
        TableColumn orders_statusCol = new TableColumn("Status");
        orders_statusCol.setPrefWidth(50d);
        orders_statusCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Status>("status"));
        tableOrders.getColumns().setAll(orders_idCol, orders_typeCol, orders_investorIdCol, orders_companyIdCol, orders_totalAmountCol, orders_completedAmountCol, orders_priceLimitCol, orders_statusCol);

        TableColumn stockprices_idCol = new TableColumn("Company");
        stockprices_idCol.setCellValueFactory(new PropertyValueFactory<MarketValue, String>("companyId"));
        TableColumn stockprices_priceCol = new TableColumn("Price");
        stockprices_priceCol.setCellValueFactory(new PropertyValueFactory<MarketValue, Double>("price"));
        tableStockPrices.getColumns().setAll(stockprices_idCol,stockprices_priceCol);
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

    public void connectButtonClicked() {
        factory = null;
        try {
            if (protocolField.getValue().equals("XVSM")) {
                factory = new XvsmFactory(adressField.getText());
            } else {
                //TODO: RMIFACTORY
            }

            ordersContainer = factory.newTradeOrdersContainer();
            orders = FXCollections.observableList(ordersContainer.getOrders(ORDER_FILTER, null));
            tableOrders.setItems(orders);
            ordersContainer.subscribe(factory.newTradeOrderSubManager(this),null);

            stockPricesContainer = factory.newStockPricesContainer();
            stockPrices = FXCollections.observableList(stockPricesContainer.getAll(null));
            tableStockPrices.setItems(stockPrices);
            stockPricesContainer.subscribe(factory.newStockPricesSubManager(this),null);

            System.out.println("Connected.");

        } catch (ConnectionError e) {
            e.printStackTrace();
        }


    }

    @Override
    public void pushNewTradeOrders(List<TradeOrder> newTradeOrders) {
        try {
            System.out.println("Trade Orders Callback.");
            orders = FXCollections.observableList(ordersContainer.getOrders(ORDER_FILTER, null));
            tableOrders.setItems(orders);
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }


    @Override
    public void pushNewMarketValues(List<MarketValue> newMarketValues) {
        try {
            System.out.println("Stock Prices Callback.");
            stockPrices = FXCollections.observableList(stockPricesContainer.getAll(null));
            tableStockPrices.setItems(stockPrices);
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }
}
