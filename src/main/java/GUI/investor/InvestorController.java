package GUI.investor;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.*;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import Model.*;
import Service.ConnectionErrorException;
import Util.TransactionTimeout;
import com.sun.org.apache.xalan.internal.xsltc.dom.AdaptiveResultTreeImpl;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestorController implements ITradeOrderSub, IInvestorDepotSub, IStockPricesSub {


    private HashMap<String,TradeOrderContainer> tradeOrderContainers;
    private HashMap<String,StockPricesContainer> stockPricesContainers;
    private HashMap<String,DepotInvestor> depots;
    private FondsIndexContainer fondsIndexContainer;

    private ObservableList<TradeOrder> activeOrders;
    private TradeOrder ORDER_FILTER;


    private ObservableList<StockStats> stockStats;

    private List<TradeObject> allTradeObjectsInDepot;
    private List<MarketValue> allMarketValues;
    private HashMap<String, Integer> stockNamesAndCount;
    private HashMap<String, IFactory> markets;
    private ObservableList<AddressInfo> addresses;


    @FXML
    private Label txtStatus;
    @FXML
    private TextField txtUsername;
    @FXML
    private CheckBox isFondsmanager;
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
    private TableColumn addressColumn;
    @FXML
    private TableColumn protocolColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label modeLabel;
    @FXML
    private VBox dataContainer;
    @FXML
    private HBox loginContainer;
    @FXML
    private Button btnDeleteOrder;
    @FXML
    private TableView<AddressInfo> marketsTable;
    @FXML
    private Button addMarketBtn;

    private Investor investor;

    private String serverAdressAndPort;

    private TradeOrder selectedOrder;




    public InvestorController() {

    }

    @FXML
    private void initialize() {

        //init Multi-address table

        addressColumn.setCellValueFactory(new PropertyValueFactory<AddressInfo, String>("address"));
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<AddressInfo, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<AddressInfo, String> t) {
                        ((AddressInfo) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setAddress(t.getNewValue());
                    }
                }
        );
        ObservableList<AddressInfo.Protocol> protocols = FXCollections.observableArrayList(AddressInfo.Protocol.values());
        protocolColumn.setCellFactory((ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), protocols)));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<AddressInfo, AddressInfo.Protocol>("protocol"));
        protocolColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<AddressInfo, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<AddressInfo, String> t) {
                        ((AddressInfo) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setProtocol(AddressInfo.Protocol.byName(t.getNewValue()));
                    }
                }
        );
        addresses = FXCollections.observableList(new ArrayList<AddressInfo>());
        addresses.add(new AddressInfo("xvsm://localhost:12345", AddressInfo.Protocol.XVSM));
        marketsTable.setItems(addresses);

        colOrderId.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("id"));
        colOrderType.setCellValueFactory(new PropertyValueFactory<TradeOrder, TradeOrder.Type>("type"));
        colOrderStockId.setCellValueFactory(new PropertyValueFactory<TradeOrder, String>("tradeObjectId"));
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

    public void editBudgetButtonClicked() {
        try {
            for (String address : markets.keySet()) {
                IFactory factory = markets.get(address);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("increase_budget.fxml"));
                fxmlLoader.setController(new BudgetController(factory, investor));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Increase Budget: " + address + " - " + factory.getAddressInfo().getProtocol());
                stage.setScene(new Scene(root1));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMarketButtonClicked() {
        addresses.add(new AddressInfo("localhost:12346", AddressInfo.Protocol.RMI));
        marketsTable.setItems(addresses);
    }

    public void loginButtonClicked() {
        if (txtUsername.getText().isEmpty()) {
            return;
        }

        investor = new Investor(txtUsername.getText());
        if (isFondsmanager.isSelected()) {
            investor.setFonds(true);
        }


        ((Stage) txtUsername.getScene().getWindow()).setTitle(investor.getId() + "'s Overview");

        ORDER_FILTER = new TradeOrder();
        ORDER_FILTER.setInvestor(investor);
        ORDER_FILTER.setStatus(TradeOrder.Status.NOT_COMPLETED);

        initFactories();

        try {
            depots = new HashMap<>();
            tradeOrderContainers = new HashMap<>();
            stockPricesContainers = new HashMap<>();
            // get/create necessary containers
            for (String address: markets.keySet()) {
                IFactory factory = markets.get(address);
                System.out.println("Creating containers for: " + address + " - " + factory.getAddressInfo().getProtocol());



                DepotInvestor depotInvestor = factory.newDepotInvestor(investor, null);
                depotInvestor.subscribe(factory.newInvestorDepotSubManager(this), null);
                depots.put(address,depotInvestor);

                TradeOrderContainer tradeOrderContainer = factory.newTradeOrdersContainer();
                tradeOrderContainer.subscribe(factory.newTradeOrderSubManager(this), null);
                tradeOrderContainers.put(address, tradeOrderContainer);

                StockPricesContainer stockPricesContainer = factory.newStockPricesContainer();
                stockPricesContainer.subscribe(factory.newStockPricesSubManager(this), null);
                stockPricesContainers.put(address, stockPricesContainer);

            }
            // initialize rest of UI after references to containers are set
            initUi();
        } catch (ConnectionErrorException connectionErrorException) {
            connectionErrorException.printStackTrace();
        }
    }

    private void initFactories() {
        try {
            markets = new HashMap<>();
            for (AddressInfo a: addresses) {
                if (a.getProtocol().equals(AddressInfo.Protocol.XVSM)) {
                    markets.put(a.getAddress(),new XvsmFactory(a.getAddress()));
                } else {
                    markets.put(a.getAddress(),new RmiFactory(a.getAddress()));
                }
        }
        } catch (Exception e) {
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

            //Check for existing investor type and adjust mode if necessary
            boolean investorExists = false;
            boolean marketValueExists = false;
            for (String address : depots.keySet()) {
                investorExists = (depots.get(address).getBudget(null) != 0.0) ? true : false;
                marketValueExists = (stockPricesContainers.get(address).getMarketValue(investor.getId(), null) != null) ? true : false;
                break; //ASSUMING INVESTOR IS THE SAME TYPE OF INVESTOR ACROSS ALL MARKETS
            }


            if (!investorExists) {
                if (!investor.isFonds()) {
                    // show budget prompt
                    editBudgetButtonClicked();
                } else {
                    // show fonds prompt
                    try {
                        for (String address : markets.keySet()) {
                            IFactory factory = markets.get(address);
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("set_fonds.fxml"));
                            fxmlLoader.setController(new FondsController(factory, investor));
                            Parent root1 = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setTitle("Init Fonds: "+address+" - "+factory.getAddressInfo().getProtocol());
                            stage.setScene(new Scene(root1));
                            stage.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (marketValueExists) {
                    investor.setFonds(true);
                } else {
                    investor.setFonds(false);
                }
            }

            if (investor.isFonds()) {
                modeLabel.setText("FONDSMANAGER MODE");
                btnEditBudget.setVisible(false); //Fondsmanager may not change its budget
                ORDER_FILTER.setTradeObjectType(TradeOrder.TradeObjectType.STOCK); //Fondsmanager may only buy/sell stocks

                //Register all markets for markagent
                List<AddressInfo> addresses = new ArrayList<>();
                for (String address : markets.keySet()) {
                    if (address.startsWith("xvsm")) {
                        addresses.add(new AddressInfo(address, AddressInfo.Protocol.XVSM));
                    } else {
                        addresses.add(new AddressInfo(address, AddressInfo.Protocol.RMI));
                    }
                }
                for (IFactory factory : markets.values()) {
                    FondsIndexContainer fondsIndexContainer = factory.newFondsIndexContainer();
                    fondsIndexContainer.registerMarkets(investor,addresses,null);
                }
            } else {
                modeLabel.setText("INVESTOR MODE");
            }

            double budget = 0d;
            System.out.println("depots-size: "+depots.size());
            for (DepotInvestor depotInvestor : depots.values()) {
                double curBudget = depotInvestor.getBudget(null);
                System.out.println("Found budget: "+curBudget);
                budget +=curBudget;
            }

            System.out.println("SETTINGS BUDGET TO: "+budget);
            txtBudget.setText("" + budget);

            allTradeObjectsInDepot = new ArrayList<>();
            for (DepotInvestor depotInvestor: depots.values()) {
                allTradeObjectsInDepot.addAll(depotInvestor.readAllTradeObjects(null));
            }

            allMarketValues = new ArrayList<>();
            for (StockPricesContainer stockPricesContainer: stockPricesContainers.values()) {
                if (investor.isFonds()) {
                    allMarketValues.addAll(stockPricesContainer.getCompanies(null));
                } else {
                    allMarketValues.addAll(stockPricesContainer.getAll(null));
                }
            }

            stockNamesAndCount = new HashMap<>();
            updateStockNamesAndCount(allTradeObjectsInDepot);

            txtTotalStockValue.setText("" + calculateTotalValueOfStocks());

            populateStockStatsTable();

            // init open orders table
            activeOrders = FXCollections.observableList(new ArrayList<TradeOrder>());
            for (TradeOrderContainer tradeOrderContainer: tradeOrderContainers.values()) {
                activeOrders = FXCollections.observableList(tradeOrderContainer.getOrders(ORDER_FILTER, null));
            }
            tabOrders.setItems(activeOrders);

            // make data container visible
            setDataContainerVisible(true);
        } catch (ConnectionErrorException connectionErrorException) {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Loading data for investor failed.\n\n"+connectionErrorException.getMessage()+"\n"+connectionErrorException.getCause());
            connectionErrorException.printStackTrace();
        }
    }

    private double calculateTotalValueOfStocks() {
        // for every market value, multiply the number of stocks with the current stock price to get the total value
        double totalValue = 0;
        for (MarketValue marketValue : allMarketValues) {
            Double price = marketValue.getPrice();
            Integer numberOfStocks = stockNamesAndCount.get(marketValue.getId());
            if (numberOfStocks != null) {
                totalValue += (price * numberOfStocks);
            }
        }
        return totalValue;
    }

    public void addOrderButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("new_order.fxml"));
            fxmlLoader.setController(new NewOrderController(markets, investor));
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


                lookForMarket: for (String address : markets.keySet()) {
                    IFactory factory = markets.get(address);

                    //Select correct market
                    if (tradeOrderContainers.get(address).getOrders(selectedOrder,null).size() > 0) {
                        String transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);
                        selectedOrder.setStatus(TradeOrder.Status.DELETED);
                        selectedOrder.setJustChanged(false);
                        tradeOrderContainers.get(address).addOrUpdateOrder(selectedOrder, transactionId);
                        factory.commitTransaction(transactionId);
                        break lookForMarket;
                    }
                }

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
                if (markets != null) {
                    for (IFactory factory : markets.values()) {
                        factory.destroy();
                    }
                }
            }
        });
    }

    @Override
    public void pushNewTradeOrders(final TradeOrder tradeOrder) {
        if (!(tradeOrder.getTradeObjectType() == TradeOrder.TradeObjectType.FOND && investor.isFonds())) { //To prevent fondmanager from trading with fonds
            if (tradeOrder.getInvestorId().equals(investor.getId())) {
                if ((tradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || tradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED))) {
                    if (activeOrders.contains(tradeOrder)) {
                        int index = activeOrders.indexOf(tradeOrder);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                activeOrders.set(index, tradeOrder);
                            }
                        });
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


                if (tradeOrder.getType().equals(TradeOrder.Type.SELL_ORDER)) {
                    try {
                        DepotInvestor depotInvestor = null;
                        depotLoop: for (DepotInvestor di : depots.values()) {

                            //Assuming that no tradeObjects are shared accross markets and each tradeObjectId is unique accross markets.
                            if (di.getTradeObjectAmount(tradeOrder.getTradeObjectId(),null) > 0) {
                                depotInvestor = di;
                                break depotLoop;
                            }
                        }
                        // update stock amount when selling stocks from own depot
                        int updatedAmountOfStock = depotInvestor.getTradeObjectAmount(tradeOrder.getTradeObjectId(), null);
                        if (updatedAmountOfStock > 0) {
                            stockNamesAndCount.put(tradeOrder.getTradeObjectId(), updatedAmountOfStock);
                        } else {
                            stockNamesAndCount.remove(tradeOrder.getTradeObjectId());
                        }
                        populateStockStatsTable();
                    } catch (ConnectionErrorException e) {
                        e.printStackTrace();
                    }
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tabOrders.setItems(activeOrders);
                    }
                });
            }
        }
    }

    @Override
    public void pushNewTradeObjects(final List<TradeObject> newTradeObjects) {
        // assumption -> stocks are ALL from the same company
        for (TradeObject tradeObject : newTradeObjects) {
            if (!(tradeObject instanceof Fond && investor.isFonds())) { //No Fonds for fondmanagers
                String tradeObjectId = tradeObject.getId();
                if (stockNamesAndCount.containsKey(tradeObjectId)) {
                    // stock with company name <xy> already in map -> increase number
                    stockNamesAndCount.put(tradeObjectId, stockNamesAndCount.get(tradeObjectId) + 1);
                } else {
                    // stocks of a new company acquired
                    stockNamesAndCount.put(tradeObjectId, 1);
                }
            }
        }
        populateStockStatsTable();
    }

    private void updateStockNamesAndCount(List<TradeObject> newTradeObjects) {
        // stocks of more than one company can be in here
        for (TradeObject tradeObject : newTradeObjects) {
            if (!(tradeObject instanceof Fond && investor.isFonds())) { //Fonds not considered when fondmanager
                String tradeObjectId = tradeObject.getId();
                if (stockNamesAndCount.containsKey(tradeObjectId)) {
                    stockNamesAndCount.put(tradeObjectId, stockNamesAndCount.get(tradeObjectId) + 1);
                } else {
                    stockNamesAndCount.put(tradeObjectId, 1);
                }
            } else {
                //Ignore Fonds if investor isFondManager
            }
        }
    }

    @Override
    public void pushNewBudget(final Double budget) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double curBudget = 0d;
                for (DepotInvestor depotInvestor: depots.values()) {
                    try {
                        curBudget += depotInvestor.getBudget(null);
                    } catch (ConnectionErrorException e) {
                        System.out.println("Failed getting budget on pushNewBudget!");
                        e.printStackTrace();
                    }
                }
                txtBudget.setText("" + curBudget);
                txtTotalStockValue.setText("" + calculateTotalValueOfStocks());
            }
        });
    }

    private void populateStockStatsTable() {
        HashMap<String, StockStats> statsMapping = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stockNamesAndCount.entrySet()) {
            for (MarketValue marketValue : allMarketValues) {
                if (marketValue.getId().equals(entry.getKey())) {
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

            if (!newMarketValue.isCompany() && investor.isFonds()) { //We are not interested in fonds as a fondmanager
                return;
            }

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
