package GUI.investor;

import Factory.IFactory;
import Model.Investor;
import Service.ConnectionErrorException;
import Service.InvestorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Felix on 02.06.2015.
 */
public class FondsController {

    private Investor investor;

    private IFactory factory;

    private Double budget;

    private Integer fondsAmount;

    private InvestorService investorService;

    @FXML
    private TextField txtFonds;
    @FXML
    private TextField txtBudget;
    @FXML
    private Label statusLabel;
    @FXML
    private Button okBtn;


    public FondsController(IFactory factory, Investor investor) {
        this.factory = factory;
        this.investor = investor;
        investorService = new InvestorService(factory, investor);
    }

    @FXML
    private void initialize() {
        // nothing to do
    }

    public void okBtnClicked() {
        if (isValidInput()) {
            try {
                investorService.addToBudget(budget);
                investorService.issueFonds(fondsAmount);
                Stage stage = (Stage) okBtn.getScene().getWindow();
                stage.close();
            } catch (ConnectionErrorException connectionErrorException) {
                statusLabel.setText("Init fondsmanager failed.");
                connectionErrorException.printStackTrace();
            }
        }
    }

    private boolean isValidInput() {
        if (txtBudget.getText().toString().isEmpty()) {
            return false;
        }
        try {
            budget = Double.parseDouble(txtBudget.getText());
            fondsAmount = Integer.parseInt(txtFonds.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid budget.");
            return false;
        }
        return true;
    }

}
