package GUI.stockmarket;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import MarketEntities.TradeOrderContainer;
import MarketEntities.TransactionHistoryContainer;
import Model.*;
import Service.ConnectionErrorException;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.List;

public class Controller implements ITradeOrderSub, IStockPricesSub, ITransactionHistorySub {

    private IFactory factory;

    private TradeOrderContainer ordersContainer;
    private ObservableList<TradeOrder> orders;
    private final TradeOrder ORDER_FILTER;

    private StockPricesContainer stockPricesContainer;
    private ObservableList<MarketValue> stockPrices;

    private TransactionHistoryContainer transactionHistoryContainer;
    private ObservableList<HistoryEntry> historyEntries;

    public Controller() {
        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_COMPLETED);
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
    @FXML
    private TableView<HistoryEntry> tableHistory;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        //Init protocolField
        ObservableList<String> protocols = FXCollections.observableArrayList();
        protocols.add("XVSM");
        protocols.add("RMI");
        protocolField.setItems(protocols);
        protocolField.setValue(protocols.get(0));

        initTableOrders();

        initTableStocks();

        initTableHistory();
    }

    private void initTableOrders() {
        TableColumn orders_idCol = new TableColumn("ID");
        orders_idCol.setPrefWidth(100d);
        orders_idCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("id"));
        TableColumn orders_typeCol = new TableColumn("Type");
        orders_typeCol.setPrefWidth(40d);
        orders_typeCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Type>("type"));
        TableColumn orders_investorIdCol = new TableColumn("Investor ID");
        orders_investorIdCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("investorId"));
        TableColumn orders_tradeObjectIdCol = new TableColumn("TO-ID");
        orders_tradeObjectIdCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("tradeObjectId"));
        TableColumn orders_totalAmountCol = new TableColumn("Total");
        orders_totalAmountCol.setPrefWidth(40d);
        orders_totalAmountCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Integer>("totalAmount"));
        TableColumn orders_completedAmountCol = new TableColumn("Completed");
        orders_completedAmountCol.setPrefWidth(40d);
        orders_completedAmountCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Integer>("completedAmount"));
        TableColumn orders_priceLimitCol = new TableColumn("Price");
        orders_priceLimitCol.setPrefWidth(50d);
        orders_priceLimitCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Double>("priceLimit"));
        TableColumn orders_statusCol = new TableColumn("Status");
        orders_statusCol.setPrefWidth(50d);
        orders_statusCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Status>("status"));
        TableColumn orders_prioritizedCol = new TableColumn("Prioritized");
        orders_prioritizedCol.setPrefWidth(70d);
        orders_prioritizedCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Boolean>("prioritized"));
        tableOrders.getColumns().setAll(orders_idCol, orders_typeCol, orders_investorIdCol, orders_tradeObjectIdCol, orders_totalAmountCol, orders_completedAmountCol, orders_priceLimitCol, orders_statusCol,orders_prioritizedCol);
    }

    private void initTableStocks() {
        TableColumn stockprices_idCol = new TableColumn("ID");
        stockprices_idCol.setPrefWidth(75d);
        stockprices_idCol.setCellValueFactory(new PropertyValueFactory<MarketValue, String>("id"));
        TableColumn stockprices_priceCol = new TableColumn("Price");
        stockprices_priceCol.setPrefWidth(75d);
        stockprices_priceCol.setCellValueFactory(new PropertyValueFactory<MarketValue, Double>("price"));
        TableColumn stockprices_tradeVolCol = new TableColumn("Trade Volume");
        stockprices_tradeVolCol.setPrefWidth(80d);
        stockprices_tradeVolCol.setCellValueFactory(new PropertyValueFactory<MarketValue, Integer>("tradeVolume"));
        tableStockPrices.getColumns().setAll(stockprices_idCol,stockprices_priceCol,stockprices_tradeVolCol);
    }

    private void initTableHistory() {
        TableColumn history_transactionIdCol = new TableColumn("Trans. ID");
        history_transactionIdCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, String>("transactionId"));
        TableColumn history_brokerIdCol = new TableColumn("Broker ID");
        history_brokerIdCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, String>("brokerId"));
        TableColumn history_buyerCol = new TableColumn("Buyer");
        history_buyerCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, Investor>("buyer"));
        TableColumn history_sellerCol = new TableColumn("Seller");
        history_sellerCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, StockOwner>("seller"));
        TableColumn history_stockCol = new TableColumn("Stock");
        history_stockCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, String>("stockName"));
        TableColumn history_buyOrderIdCol = new TableColumn("Buy Order");
        history_buyOrderIdCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, String>("buyOrderId"));
        TableColumn history_sellOrderIdCol = new TableColumn("Sell Order");
        history_sellOrderIdCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, String>("sellOrderId"));
        TableColumn history_tradedMarketValueCol = new TableColumn("Trading price");
        history_tradedMarketValueCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, Double>("tradedMarketValue"));
        TableColumn history_amountOfStocksCol = new TableColumn("Amount");
        history_amountOfStocksCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, Integer>("amountOfStocks"));
        TableColumn history_totalPriceCol = new TableColumn("Total price");
        history_totalPriceCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, Double>("totalPrice"));
        TableColumn history_provisionCol = new TableColumn("Provision");
        history_provisionCol.setCellValueFactory(new PropertyValueFactory<HistoryEntry, Double>("provision"));
        tableHistory.getColumns().setAll(history_transactionIdCol, history_brokerIdCol, history_buyerCol, history_sellerCol,
                history_stockCol, history_buyOrderIdCol, history_sellOrderIdCol, history_tradedMarketValueCol, history_amountOfStocksCol,
                history_totalPriceCol, history_provisionCol);
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

    public void protocolFieldChanged() {
        if (protocolField.getValue().equals("XVSM")) {
            adressField.setText("xvsm://localhost:12345");
        } else {
            adressField.setText("localhost:12345");
        }
    }

    public void connectButtonClicked() {
        factory = null;
        try {
            if (protocolField.getValue().equals("XVSM")) {
                factory = new XvsmFactory(adressField.getText());
            } else {
                factory = new RmiFactory(adressField.getText());
            }

            ordersContainer = factory.newTradeOrdersContainer();
            orders = FXCollections.observableList(ordersContainer.getOrders(ORDER_FILTER, null));
            tableOrders.setItems(orders);
            ordersContainer.subscribe(factory.newTradeOrderSubManager(this), null);

            stockPricesContainer = factory.newStockPricesContainer();
            stockPrices = FXCollections.observableList(stockPricesContainer.getAll(null));
            tableStockPrices.setItems(stockPrices);
            stockPricesContainer.subscribe(factory.newStockPricesSubManager(this), null);

            transactionHistoryContainer = factory.newTransactionHistoryContainer();
            historyEntries = FXCollections.observableList(transactionHistoryContainer.getTransactionHistory(null));
            tableHistory.setItems(historyEntries);
            transactionHistoryContainer.subscribe(factory.newTransactionHistorySubManager(this), null);

            statusLabel.textFillProperty().setValue(Color.DARKGREEN);
            statusLabel.setText("Connected.");
            FadeTransition ft = new FadeTransition(Duration.millis(2000), statusLabel);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();

            System.out.println("Connected!");

        } catch (ConnectionErrorException e) {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Connection failed.");
            e.printStackTrace();
        }
    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {

        if (tradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || tradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED)) {
            if (orders.contains(tradeOrder)) {
                int index = orders.indexOf(tradeOrder);
                orders.set(index, tradeOrder);
            } else {
                orders.add(tradeOrder);
            }
        } else {
            orders.remove(tradeOrder);  // only removes if present
        }

        tableOrders.setItems(orders);

        //Refresh GUI (is buggy here, dont know why)
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableOrders.getColumns().get(0).setVisible(false);
                tableOrders.getColumns().get(0).setVisible(true);
            }
        });

    }


    @Override
    public void pushNewMarketValues(List<MarketValue> newMarketValues) {
        for (MarketValue mwNew : newMarketValues) {
            if (stockPrices.contains(mwNew)) {
                System.out.println("Updated: " + stockPrices.indexOf(mwNew) + " with " + mwNew.getPrice());
                int index = stockPrices.indexOf(mwNew);
                stockPrices.set(index, mwNew);
            } else {
                stockPrices.add(mwNew);
            }
        }

        tableStockPrices.setItems(stockPrices);

        //Refresh GUI (is buggy here, dont know why)
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableStockPrices.getColumns().get(0).setVisible(false);
                tableStockPrices.getColumns().get(0).setVisible(true);
            }
        });
    }


    @Override
    public void pushNewHistoryEntry(HistoryEntry historyEntry) {
        tableHistory.getItems().add(historyEntry);
    }
}
