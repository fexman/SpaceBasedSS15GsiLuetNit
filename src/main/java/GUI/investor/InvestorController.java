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
import java.util.Map;

public class InvestorController implements ITradeOrderSub, IInvestorDepotSub, IStockPricesSub {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;
    private ObservableList<TradeOrder> activeOrders;
    private TradeOrder ORDER_FILTER;

    private DepotInvestor depotInvestor;
    private ObservableList<StockStats> stockStats;

    private List<Stock> allStocksInDepot;
    private List<MarketValue> allMarketValues;
    private HashMap<String, Integer> stockNamesAndCount;

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
        serverAdressAndPort = "xvsm://localhost:12345";

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("increase_budget.fxml"));
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

            allStocksInDepot = depotInvestor.readAllStocks(null);
            allMarketValues = stockPricesContainer.getAll(null);
            stockNamesAndCount = new HashMap<>();
            updateStockNamesAndCount(allStocksInDepot);

            txtTotalStockValue.setText("" + calculateTotalValueOfStocks());

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

    private double calculateTotalValueOfStocks() {
        // for every market value, multiply the number of stocks with the current stock price to get the total value
        double totalValue = 0;
        for (MarketValue marketValue : allMarketValues) {
            Double price = marketValue.getPrice();
            Integer numberOfStocks = stockNamesAndCount.get(marketValue.getCompanyId());
            if (numberOfStocks != null) {
                totalValue += (price * numberOfStocks);
            }
        }
        return totalValue;
    }

    public void addOrderButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("new_order.fxml"));
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
                String transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);
                selectedOrder.setStatus(TradeOrder.Status.DELETED);
                tradeOrderContainer.addOrUpdateOrder(selectedOrder, transactionId);
                factory.commitTransaction(transactionId);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btnDeleteOrder.setDisable(true);
                    }
                });
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
            if (tradeOrder.getInvestorId().equals(investor.getId()) &&
                    (tradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || tradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED))) {
                if (activeOrders.contains(tradeOrder)) {
                    int index = activeOrders.indexOf(tradeOrder);
                    activeOrders.set(index, tradeOrder);
                } else {
                    activeOrders.add(tradeOrder);
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (activeOrders.contains(tradeOrder)) {
                            activeOrders.remove(tradeOrder);
                        }
                    }
                });
            }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabOrders.setItems(activeOrders);
            }
            });
    }

    @Override
    public void pushNewStocks(final List<Stock> newStocks) {
        // assumption -> stocks are ALL from the same company
        if (newStocks != null && newStocks.size() > 0) {
            String companyId = newStocks.get(0).getCompany().getId();
            if (stockNamesAndCount.containsKey(companyId)) {
                // stock with company name <xy> already in map -> increase number
                stockNamesAndCount.put(companyId, stockNamesAndCount.get(companyId) + newStocks.size());
            } else {
                // stocks of a new company acquired
                stockNamesAndCount.put(companyId, newStocks.size());
            }
        }
        populateStockStatsTable();
    }

    private void updateStockNamesAndCount(List<Stock> newStocks) {
        // stocks of more than one company can be in here
        for (Stock stock : newStocks) {
            String companyId = stock.getCompany().getId();
            if (stockNamesAndCount.containsKey(companyId)) {
                stockNamesAndCount.put(companyId, stockNamesAndCount.get(companyId) + 1);
            } else {
                stockNamesAndCount.put(companyId, 1);
            }
        }
    }

    @Override
    public void pushNewBudget(final Double budget) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtBudget.setText("" + budget.doubleValue());
                txtTotalStockValue.setText("" + calculateTotalValueOfStocks());
            }
        });
    }

    private void populateStockStatsTable() {
        HashMap<String, StockStats> statsMapping = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stockNamesAndCount.entrySet()) {
            for (MarketValue marketValue : allMarketValues) {
                if (marketValue.getCompanyId().equals(entry.getKey())) {
                    Double price = marketValue.getPrice();
                    statsMapping.put(entry.getKey(), new StockStats(entry.getKey(), entry.getValue(), price, price * entry.getValue()));
                }
            }
        }
        // populate table with results
        stockStats = FXCollections.observableList(new ArrayList<>(statsMapping.values()));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabStocks.setItems(stockStats);
            }
        });
    }


    @Override
    public void pushNewMarketValues(List<MarketValue> newMarketValues) {
        // here we are only interested in adding/updating market values (not removing)
        for (MarketValue newMarketValue : newMarketValues) {
            if (allMarketValues.contains(newMarketValue)) {
                int index = allMarketValues.indexOf(newMarketValue);
                allMarketValues.set(index, newMarketValue);
            } else {
                allMarketValues.add(newMarketValue);
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtTotalStockValue.setText("" + calculateTotalValueOfStocks());
            }
        });

        populateStockStatsTable();
    }

}