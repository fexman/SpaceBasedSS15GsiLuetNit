package GUI.stockmarket;

import Factory.IFactory;
import Factory.XvsmFactory;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Service.ConnectionError;
import Service.Subscribing.TradeOrders.ITradeOrderSub;
import javafx.collections.FXCollections;
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
    private TradeOrderContainer ordersContainer;
    private ObservableList<TradeOrder> orders;
    private final TradeOrder ORDER_FILTER;

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
        TableColumn idCol = new TableColumn("ID");
        idCol.setPrefWidth(125d);
        idCol.setCellValueFactory( new PropertyValueFactory<TradeOrder,String>("id"));
        TableColumn typeCol = new TableColumn("Type");
        typeCol.setPrefWidth(40d);
        typeCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, TradeOrder.Type>("type"));
        TableColumn investorIdCol = new TableColumn("Investor ID");
        investorIdCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, String>("investorId"));
        TableColumn companyIdCol = new TableColumn("Company ID");
        companyIdCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, String>("companyId"));
        TableColumn totalAmountCol = new TableColumn("Total");
        totalAmountCol.setPrefWidth(40d);
        totalAmountCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, Integer>("totalAmount"));
        TableColumn completedAmountCol = new TableColumn("Completed");
        completedAmountCol.setPrefWidth(80d);
        completedAmountCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, Integer>("completedAmount"));
        TableColumn priceLimitCol = new TableColumn("Price");
        priceLimitCol.setPrefWidth(50d);
        priceLimitCol.setCellValueFactory(new PropertyValueFactory<TradeOrder, Double>("priceLimit"));
        TableColumn statusCol = new TableColumn("Status");
        statusCol.setPrefWidth(50d);
        statusCol.setCellValueFactory( new PropertyValueFactory<TradeOrder, TradeOrder.Status>("status"));
        tableOrders.getColumns().setAll(idCol, typeCol, investorIdCol, companyIdCol, totalAmountCol, completedAmountCol, priceLimitCol, statusCol);
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


}
