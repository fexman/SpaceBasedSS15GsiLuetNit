package GUI.investor;

import Factory.IFactory;
import MarketEntities.DepotInvestor;
import Model.Investor;
import Service.ConnectionError;
import Service.InvestorService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by j0h1 on 26.04.2015.
 */
public class BudgetController {

    private Investor investor;

    private IFactory factory;

    private Double budget;

    private InvestorService investorService;

    @FXML
    private TextField txtBudget;
    @FXML
    private Label statusLabel;
    @FXML
    private Button btnAddToBudget;

    public BudgetController() {
    }

    public BudgetController(IFactory factory, Investor investor) {
        this.factory = factory;
        this.investor = investor;
        investorService = new InvestorService(factory, investor);
    }

    @FXML
    private void initialize() {
        // nothing to do
    }

    public void addToBudgetClicked() {
        if (isValidInput()) {
            try {
                investorService.addToBudget(budget);

                Stage stage = (Stage) btnAddToBudget.getScene().getWindow();
                stage.close();
            } catch (ConnectionError connectionError) {
                statusLabel.setText("Increasing budget failed.");
                connectionError.printStackTrace();
            }
        }
    }

    private boolean isValidInput() {
        if (txtBudget.getText().toString().isEmpty()) {
            return false;
        }
        try {
            budget = Double.parseDouble(txtBudget.getText().toString());
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid budget.");
            return false;
        }
        return true;
    }
}
