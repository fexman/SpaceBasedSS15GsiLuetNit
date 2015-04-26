package GUI.investor;

import Factory.IFactory;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.Company;
import Model.Investor;
import Model.TradeOrder;
import Service.ConnectionError;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.mozartspaces.core.TransactionReference;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by j0h1 on 26.04.2015.
 */
public class NewOrderController {

    private IFactory factory;

    private TradeOrderContainer tradeOrderContainer;

    private Investor investor;

    @FXML
    private ComboBox<Company> stockName;
    @FXML
    private ComboBox<TradeOrder.Type> orderType;
    @FXML
    private TextField txtStockAmount;
    @FXML
    private TextField txtOrderLimit;
    @FXML
    private Label statusLabel;

    private Integer stockAmount;
    private Double orderLimit;


    public NewOrderController() {
    }

    public NewOrderController(IFactory factory, Investor investor) {
        this.factory = factory;
        this.investor = investor;

        tradeOrderContainer = factory.newTradeOrdersContainer();
    }

    public void setFactory(IFactory factory) {
        this.factory = factory;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    @FXML
    private void initialize() {
        populateStockNames();

        orderType.getItems().addAll(TradeOrder.Type.BUY_ORDER, TradeOrder.Type.SELL_ORDER);
    }

    private void populateStockNames() {
        // show all companies that currently sell stocks
        try {
            String transactionId = factory.createTransaction();

            List<TradeOrder> availableTradeOrders = tradeOrderContainer.getAllOrders(transactionId);
            HashMap<String, Company> availableCompanies = new HashMap<>();
            for (TradeOrder tradeOrder : availableTradeOrders) {
                availableCompanies.put(tradeOrder.getCompanyId(), tradeOrder.getCompany());
            }

            stockName.getItems().addAll(availableCompanies.values());
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }

    public void addTradeOrderClicked() {
        if (isValidInput()) {
            try {
                String transactionId = factory.createTransaction();

                TradeOrder tradeOrder = new TradeOrder();
                tradeOrder.setId(UUID.randomUUID().toString());
                tradeOrder.setInvestor(investor);
                tradeOrder.setCompany(stockName.getValue());
                tradeOrder.setCompletedAmount(0);
                tradeOrder.setTotalAmount(stockAmount);
                tradeOrder.setOpenAmount(stockAmount);
                tradeOrder.setInvestorType(TradeOrder.InvestorType.INVESTOR);
                tradeOrder.setPriceLimit(orderLimit);
                tradeOrder.setType(orderType.getValue());
                tradeOrder.setStatus(TradeOrder.Status.OPEN);

                tradeOrderContainer.addOrUpdateOrder(tradeOrder, transactionId);

                System.out.println("Trade order added: " + tradeOrder);
            } catch (ConnectionError connectionError) {
                connectionError.printStackTrace();
            }
        }
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
