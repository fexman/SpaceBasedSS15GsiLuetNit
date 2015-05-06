package GUI.investor;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.*;
import Service.ConnectionErrorException;
import Util.TransactionTimeout;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvestorController implements ITradeOrderSub, IInvestorDepotSub, IStockPricesSub {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;
    private ObservableList<TradeOrder> activeOrders;
    private TradeOrder ORDER_FILTER;

    private DepotInvestor depotInvestor;
    private ObservableList<StockStats> stockStats;
    private List<Stock> allStocks;

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
    private TableView<StockStats> tabStocks;
    @FXML
    private TableColumn colStockId;
    @FXML
    private TableColumn colStockAmount;
    @FXML
    private TableColumn colStockMarketValue;
    @FXML
    private TableColumn colTotalValue;
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
    @FXML
    private Button btnDeleteOrder;

    private Investor investor;

    private String serverAdressAndPort;

    private TradeOrder selectedOrder;

    public InvestorController() {

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

        colStockId.setCellValueFactory(new PropertyValueFactory<StockStats, String>("stockName"));
        colStockAmount.setCellValueFactory(new PropertyValueFactory<StockStats, Integer>("amount"));
        colStockMarketValue.setCellValueFactory(new PropertyValueFactory<StockStats, Double>("marketValue"));
        colTotalValue.setCellValueFactory(new PropertyValueFactory<StockStats, Double>("totalValue"));

        tabOrders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TradeOrder>() {
            @Override
            public void changed(ObservableValue<? extends TradeOrder> observable, TradeOrder oldValue, TradeOrder newValue) {
                selectedOrder = newValue;
                if (selectedOrder != null && btnDeleteOrder.isDisabled()) {
                    btnDeleteOrder.setDisable(false);
                }
            }
        });

        setDataContainerVisible(false);
    }

    private void setDataContainerVisible(boolean visible) {
        if (visible) {
            dataContainer.setVisible(true);
            dataContainer.setPrefHeight(738);
        } else {
            dataContainer.setVisible(false);
            dataContainer.setMinHeight(0);
            dataContainer.setPrefHeight(0);
        }
    }

    public void protocolFieldChanged() {
        if (protocolField.getValue().equals("XVSM")) {
            serverAdressAndPort = "xvsm://localhost:12345";
        } else {
            serverAdressAndPort = "localhost:12345";
        }
    }

    public void editBudgetButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("increase_budget.fxml"));
            fxmlLoader.setController(new BudgetController(factory, investor));
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

        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setInvestor(investor);
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_COMPLETED);

        initFactory();

        try {
            // get/create necessary containers
            depotInvestor = factory.newDepotInvestor(investor, null);
            depotInvestor.subscribe(factory.newInvestorDepotSubManager(this), null);

            tradeOrderContainer = factory.newTradeOrdersContainer();
            tradeOrderContainer.subscribe(factory.newTradeOrderSubManager(this), null);

            stockPricesContainer = factory.newStockPricesContainer();
            stockPricesContainer.subscribe(factory.newStockPricesSubManager(this), null);

            // initialize rest of UI after references to containers are set
            initUi();
        } catch (ConnectionErrorException connectionErrorException) {
            connectionErrorException.printStackTrace();
        }
    }

    private void initFactory() {
        try {
            if (protocolField.getValue().equals("XVSM")) {
                factory = new XvsmFactory(serverAdressAndPort);
            } else {
                factory = new RmiFactory(serverAdressAndPort);
            }
        } catch (ConnectionErrorException e) {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Connection failed.");
            e.printStackTrace();
        }
    }

    private void initUi() throws ConnectionErrorException {
        try {
            // make login invisible
            loginContainer.setVisible(false);
            loginContainer.setMinHeight(0);
            loginContainer.setPrefHeight(0);

            // load initial data
            double budget = depotInvestor.getBudget(null);
            if (budget == 0.0) {
                // show budget prompt
                editBudgetButtonClicked();
            }
            txtBudget.setText("" + budget);
            txtTotalStockValue.setText("" + calculateTotalValueOfStocks());

            // init owned stocks table
            allStocks = depotInvestor.readAllStocks(null);
            populateStockStatsTable();

            // init open orders table
            activeOrders = FXCollections.observableList(tradeOrderContainer.getOrders(ORDER_FILTER, null));
            tabOrders.setItems(activeOrders);

            // make data container visible
            setDataContainerVisible(true);
        } catch (ConnectionErrorException connectionErrorException) {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Loading data for investor failed.");
        }
    }

    private double calculateTotalValueOfStocks() throws ConnectionErrorException {
        // get all stocks for investor
        List<Stock> allStocksInDepot = depotInvestor.readAllStocks(null);

        // get distinct stock names and count
        HashMap<String, Integer> stockNamesAndCount = new HashMap<>();
        for (Stock s : allStocksInDepot) {
            if (stockNamesAndCount.get(s.getCompany().getId()) == null) {
                stockNamesAndCount.put(s.getCompany().getId(), 1);
            } else {
                stockNamesAndCount.put(s.getCompany().getId(), stockNamesAndCount.get(s.getCompany().getId()) + 1);
            }
        }

        double totalValue = 0;

        for (String s : stockNamesAndCount.keySet()) {  // only <disctinct company stocks> space operations needed
            Double price = stockPricesContainer.getMarketValue(new Company(s), null).getPrice();
            if (price != null) {
                totalValue += (price * stockNamesAndCount.get(s));
            }
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

    public void deleteOrderButtonClicked() {
        if (selectedOrder != null) {
            System.out.println("To delete: " + selectedOrder);
            try {
                //TODO transaction timeout SOGAR in XVSM (AUCH MIT INFINITE TIMEOUT!) -> addOrUpdateTradeOrder wirft Exception -> Transaction Timeout!
                String transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);
                selectedOrder.setStatus(TradeOrder.Status.DELETED);
                tradeOrderContainer.addOrUpdateOrder(selectedOrder, transactionId);
                factory.commitTransaction(transactionId);

                btnDeleteOrder.setDisable(true);
            } catch (ConnectionErrorException connectionError) {
                System.out.print(connectionError);
            }
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
    public void pushNewTradeOrders(final TradeOrder tradeOrder) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (tradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || tradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED)) {
                    if (activeOrders.contains(tradeOrder)) {
                        int index = activeOrders.indexOf(tradeOrder);
                        activeOrders.set(index, tradeOrder);
                    } else {
                        activeOrders.add(tradeOrder);
                    }
                } else {
                    activeOrders.remove(tradeOrder);  // only removes if present
                }

                tabOrders.setItems(activeOrders);

                tabOrders.getColumns().get(4).setVisible(false);
                tabOrders.getColumns().get(4).setVisible(true);
            }
        });
    }

    @Override
    public void pushNewStocks(final List<Stock> newStocks) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Stock newStock : newStocks) {
                    if (allStocks.contains(newStock)) {
                        System.out.println("Updated: " + allStocks.indexOf(newStock) + " with " + newStock);
                        int index = activeOrders.indexOf(newStock);
                        allStocks.set(index, newStock);
                    } else {
                        allStocks.add(newStock);
                    }
                }

                try {
                    populateStockStatsTable();
                } catch (ConnectionErrorException connectionErrorException) {
                    connectionErrorException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void pushNewBudget(final Double budget) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtBudget.setText("" + budget.doubleValue());
                try {
                    txtTotalStockValue.setText("" + calculateTotalValueOfStocks());
                } catch (ConnectionErrorException connectionErrorException) {
                    connectionErrorException.printStackTrace();
                }
            }
        });
    }

    private void populateStockStatsTable() throws ConnectionErrorException {
        // evaluate stock statistics
        HashMap<String, StockStats> statsMapping = new HashMap<>();
        for (Stock s : allStocks) {
            StockStats currentStockStats = statsMapping.get(s.getCompany().getId());
            if (currentStockStats == null) {
                // no stock stats for this company evaluated yet (get current market value here)
                Double marketValue = stockPricesContainer.getMarketValue(s.getCompany(), null).getPrice();
                statsMapping.put(s.getCompany().getId(), new StockStats(s.getCompany().getId(), 1, marketValue, marketValue));
            } else {
                // update stock stats amount and totalValue accordingly
                statsMapping.put(s.getCompany().getId(), new StockStats(s.getCompany().getId(), currentStockStats.getAmount() + 1,
                        currentStockStats.getMarketValue(), currentStockStats.getMarketValue() * (currentStockStats.getAmount() + 1)));
            }
        }
        // populate table with results
        stockStats = FXCollections.observableList(new ArrayList<>(statsMapping.values()));
        tabStocks.setItems(stockStats);
    }

    @Override
    public void pushNewMarketValues(List<MarketValue> newMarketValues) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    allStocks = depotInvestor.readAllStocks(null);
                    txtTotalStockValue.setText("" + calculateTotalValueOfStocks());
                    populateStockStatsTable();
                } catch (ConnectionErrorException connectionErrorException) {
                    connectionErrorException.printStackTrace();
                }
            }
        });
    }

}
