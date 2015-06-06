package GUI.investor;

import Factory.IFactory;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.AddressInfo;
import Model.Company;
import Model.Investor;
import Model.TradeOrder;
import Service.ConnectionErrorException;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by j0h1 on 26.04.2015.
 */
public class NewOrderController {

    private HashMap<String, IFactory> markets;

    private HashMap<String, TradeOrderContainer> tradeOrderContainers;
    private HashMap<String, StockPricesContainer> stockpricesContainers;

    private Investor investor;

    @FXML
    private ComboBox<String> stockMarket;
    @FXML
    private ComboBox<String> stockName;
    @FXML
    private ComboBox<TradeOrder.Type> orderType;
    @FXML
    private TextField txtStockAmount;
    @FXML
    private TextField txtOrderLimit;
    @FXML
    private Label txtPriceLimit;
    @FXML
    private Label statusLabel;
    @FXML
    private CheckBox isPrioritized;

    private Integer stockAmount;
    private Double orderLimit;


    public NewOrderController() {
    }

    public NewOrderController(HashMap<String, IFactory> markets, Investor investor) {
        this.markets = markets;
        this.investor = investor;

        tradeOrderContainers = new HashMap<>();
        stockpricesContainers = new HashMap<>();

        for (String address: markets.keySet()) {
            IFactory factory = markets.get(address);
            tradeOrderContainers.put(address,factory.newTradeOrdersContainer());
            stockpricesContainers.put(address,factory.newStockPricesContainer());
        }
    }

    public void setFactories(HashMap<String, IFactory> markets) {
        this.markets = markets;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    @FXML
    private void initialize() {
        String address = populateMarkets();

        address = address.split("-")[0].trim();
        populateStockNames(tradeOrderContainers.get(address));

        orderType.getItems().addAll(TradeOrder.Type.BUY_ORDER, TradeOrder.Type.SELL_ORDER);
    }

    private String populateMarkets() {
        ObservableList<String> marketNames = FXCollections.observableArrayList(new ArrayList<String>());
        for (IFactory factory : markets.values()) {
            AddressInfo address = factory.getAddressInfo();
            marketNames.add(address.getAddress() + " - "+address.getProtocol());
        }
        stockMarket.setItems(marketNames);
        stockMarket.setValue(marketNames.get(0));
        return marketNames.get(0);
    }

    private void populateStockNames(TradeOrderContainer tradeOrderContainer) {
        // show all companies that currently sell stocks
        try {
            // using a HashMap to ensure distinct selection of company names
            stockName.getItems().clear();
            List<TradeOrder> availableTradeOrders = tradeOrderContainer.getAllOrders(null);
            HashMap<String, Company> availableCompanies = new HashMap<>();
            for (TradeOrder tradeOrder : availableTradeOrders) {
                if (!(tradeOrder.getTradeObjectType() == TradeOrder.TradeObjectType.FOND && investor.isFonds())) { //Fonds guys are not allowed to trade with em
                    availableCompanies.put(tradeOrder.getTradeObjectId(), new Company(tradeOrder.getTradeObjectId()));
                }
            }

            // extract stock names (company id)
            List<String> stockNames = new ArrayList<>(availableCompanies.keySet());
            stockName.getItems().addAll(stockNames);
        } catch (ConnectionErrorException connectionErrorException) {
            connectionErrorException.printStackTrace();
        }
    }

    public void stockMarketChanged() {
        populateStockNames(tradeOrderContainers.get(stockMarket.getValue().split(" - ")[0].trim()));
    }

    public void orderTypeSelected() {
        if (orderType.getValue() != null) {
            if (orderType.getValue().equals(TradeOrder.Type.BUY_ORDER)) {
                txtPriceLimit.setText("Max. purchasing price:");
            } else {
                txtPriceLimit.setText("Min. selling price:");
            }
        }
    }

    public void addTradeOrderClicked() {
        if (isValidInput()) {
            TradeOrder tradeOrder = new TradeOrder();
            tradeOrder.setId(UUID.randomUUID().toString());
            tradeOrder.setInvestor(investor);
            tradeOrder.setTradeObjectId(stockName.getValue());
            tradeOrder.setTotalAmount(stockAmount);
            tradeOrder.setCompletedAmount(0);
            tradeOrder.setInvestorType(TradeOrder.InvestorType.INVESTOR);
            tradeOrder.setPriceLimit(orderLimit);
            tradeOrder.setType(orderType.getValue());
            tradeOrder.setStatus(TradeOrder.Status.OPEN);
            tradeOrder.setPrioritized(isPrioritized.isSelected());
            tradeOrder.setJustChanged(true);

            String address = stockMarket.getValue().split(" - ")[0].trim();
            StockPricesContainer stockPricesContainer = stockpricesContainers.get(address);
            TradeOrderContainer tradeOrderContainer = tradeOrderContainers.get(address);

            try {

                if (stockPricesContainer.getMarketValue(stockName.getValue(),null).isCompany()) {
                    tradeOrder.setTradeObjectType(TradeOrder.TradeObjectType.STOCK);
                } else {
                    tradeOrder.setTradeObjectType(TradeOrder.TradeObjectType.FOND);
                }

                tradeOrderContainer.addOrUpdateOrder(tradeOrder, null);

                statusLabel.textFillProperty().setValue(Color.GREEN);
                statusLabel.setText("Trade order added");
                performFaceOut(statusLabel);

                System.out.println("Trade order added: " + tradeOrder);
            } catch (ConnectionErrorException connectionErrorException) {
                connectionErrorException.printStackTrace();
            }
        } else {
            statusLabel.textFillProperty().setValue(Color.RED);
            statusLabel.setText("Missing/invalid input");
            performFaceOut(statusLabel);
        }
    }

    private void performFaceOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    private boolean isValidInput() {
        if (stockName.getValue() == null) {
            return false;
        }

        if (orderType.getValue() == null) {
            return false;
        }

        try {
            stockAmount = Integer.parseInt(txtStockAmount.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            orderLimit = Double.parseDouble(txtOrderLimit.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
