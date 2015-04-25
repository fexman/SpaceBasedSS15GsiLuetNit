package GUI.investor;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.Investor;
import Model.Stock;
import Model.TradeOrder;
import Service.ConnectionError;
import javafx.collections.FXCollections;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.capi3.Query;

import java.util.List;

public class InvestorController implements ITradeOrderSub {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;
    private ObservableList<TradeOrder> activeOrders;
    private final TradeOrder ORDER_FILTER;

    private DepotInvestor depotInvestor;

    private StockPricesContainer stockPricesContainer;

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
    private TableColumn colStockAmount;
    @FXML
    private TableColumn colStockMarketValue;
    @FXML
    private TableColumn colStockPrice;
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
    private TableColumn colOrderLimit;
    @FXML
    private TableColumn colOrderOpenAmount;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox dataContainer;
    @FXML
    private HBox loginContainer;

    private Investor investor;
    private String serverAdressAndPort;

    public InvestorController() {
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

        colOrderId.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("id"));
        colOrderType.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Type>("type"));
        colOrderStockId.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("companyId"));
        colOrderLimit.setCellValueFactory(new PropertyValueFactory<TradeOrder, Double>("priceLimit"));
        colOrderOpenAmount.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Type>("openAmount"));

        dataContainer.setVisible(false);
    }

    public void protocolFieldChanged() {
        if (protocolField.getValue().equals("XVSM")) {
            serverAdressAndPort = "xvsm://localhost:12345";
        } else {
            serverAdressAndPort = "localhost:12345";
        }
    }

    private void populateActiveStocksTable(String transactionId) {
        //TODO implement

    }

    private void populateOpenOrdersTable(String transactionId) throws ConnectionError {
        TradeOrder filter = new TradeOrder();
        filter.setInvestor(investor);
        filter.setStatus(TradeOrder.Status.OPEN);

        activeOrders = FXCollections.observableList(tradeOrderContainer.getOrders(filter, transactionId));

        tabOrders.setItems(activeOrders);
    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {

    }

    public void editBudgetButtonClicked() {
        //TODO implement
    }

    public void loginButtonClicked() {
        if (txtUsername.getText().isEmpty()) {
            return;
        }

        investor = new Investor(txtUsername.getText());

        initFactory();

        try {
            String transactionId = factory.createTransaction();

            // get/create necessary containers
            depotInvestor = factory.newDepotInvestor(investor, transactionId);
            tradeOrderContainer = factory.newTradeOrdersContainer();
            stockPricesContainer = factory.newStockPricesContainer();

            // initialize rest of UI after references to containers are set
            initUi(transactionId);
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }

    private void initFactory() {
        try {
            if (protocolField.getValue().equals("XVSM")) {
                factory = new XvsmFactory(serverAdressAndPort);
            } else {
                factory = new RmiFactory(serverAdressAndPort);
            }
        } catch (ConnectionError e) {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Connection failed.");
            e.printStackTrace();
        }
    }

    private void initUi(String transactionId) throws ConnectionError {
//        try {
            // make login invisible
            loginContainer.setVisible(false);
            loginContainer.setPrefHeight(0);

            // load data
            double budget = depotInvestor.getBudget(transactionId);
            txtBudget.setText("" + budget);
            txtTotalStockValue.setText("" + calculateTotalValueOfStocks(transactionId));

            populateActiveStocksTable(transactionId);

            populateOpenOrdersTable(transactionId);

            // make data container visible
            dataContainer.setVisible(true);
//        } catch (ConnectionError connectionError) {
//            statusLabel.textFillProperty().setValue(Color.RED);
//            statusLabel.setText("Loading data for investor failed.");
//        }
    }

    private double calculateTotalValueOfStocks(String transactionId) throws ConnectionError {
        //TODO alternative solution: read distinct stocks to reduce getMarketValue() calls

        // get all stocks for investor
        List<Stock> allStocksInDepot = depotInvestor.readAllStocks(transactionId);

        double totalValue = 0;

        for (Stock stock : allStocksInDepot) {
            totalValue += stockPricesContainer.getMarketValue(stock.getCompany(), transactionId).getPrice();
        }

        return totalValue;
    }

    public void addOrderButtonClicked() {
        //TODO open new_order
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
