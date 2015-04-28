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
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class InvestorController implements OnBudgetChangedListener {

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
        //TODO change this
        colOrderOpenAmount.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Type>("totalAmount"));

        dataContainer.setVisible(false);
    }

    public void protocolFieldChanged() {
        if (protocolField.getValue().equals("XVSM")) {
            serverAdressAndPort = "xvsm://localhost:12345";
        } else {
            serverAdressAndPort = "localhost:12345";
        }
    }

    private void populateActiveStocksTable() {
        //TODO implement
    }

    private void populateOpenOrdersTable() throws ConnectionError {
        TradeOrder filter = new TradeOrder();
        filter.setInvestor(investor);
        filter.setStatus(TradeOrder.Status.NOT_COMPLETED);

        activeOrders = FXCollections.observableList(tradeOrderContainer.getOrders(filter, null));

        tabOrders.setItems(activeOrders);
    }

    public void editBudgetButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("increase_budget.fxml"));
            fxmlLoader.setController(new BudgetController(factory, investor, this));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Increase Budget");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginButtonClicked() {
        if (txtUsername.getText().isEmpty()) {
            return;
        }

        investor = new Investor(txtUsername.getText());

        initFactory();

        try {
            // get/create necessary containers
            depotInvestor = factory.newDepotInvestor(investor, null);
            tradeOrderContainer = factory.newTradeOrdersContainer();
            stockPricesContainer = factory.newStockPricesContainer();

            // initialize rest of UI after references to containers are set
            initUi();
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

    private void initUi() throws ConnectionError {
//        try {
            // make login invisible
            loginContainer.setVisible(false);
            loginContainer.setPrefHeight(0);

            // load initial data
            double budget = depotInvestor.getBudget(null);
            txtBudget.setText("" + budget);
            txtTotalStockValue.setText("" + calculateTotalValueOfStocks());

            populateActiveStocksTable();

            populateOpenOrdersTable();

            // make data container visible
            dataContainer.setVisible(true);
//        } catch (ConnectionError connectionError) {
//            statusLabel.textFillProperty().setValue(Color.RED);
//            statusLabel.setText("Loading data for investor failed.");
//        }
    }

    private double calculateTotalValueOfStocks() throws ConnectionError {
        //TODO alternative solution: read distinct stocks to reduce getMarketValue() calls

        // get all stocks for investor
        List<Stock> allStocksInDepot = depotInvestor.readAllStocks(null);

        double totalValue = 0;

        for (Stock stock : allStocksInDepot) {
            totalValue += stockPricesContainer.getMarketValue(stock.getCompany(), null).getPrice();
        }

        return totalValue;
    }

    public void addOrderButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("new_order.fxml"));
            fxmlLoader.setController(new NewOrderController(factory, investor));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Add trade order");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onBudgetChanged() {
        try {
            double budget = depotInvestor.getBudget(null);
            txtBudget.setText("" + budget);
        } catch (ConnectionError connectionError) {
            statusLabel.setText("Unable to load new budget.");
        }
    }
}
