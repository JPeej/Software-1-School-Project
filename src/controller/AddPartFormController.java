package controller;

import model.Part;
import java.util.*;
import java.net.URL;
import model.InHouse;
import model.Inventory;
import model.Outsourced;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;


/** Controls user input to the add part screen.*/
public class AddPartFormController implements Initializable {

    Navigation nav = new Navigation();

    @FXML
    private RadioButton inHouseRadio;
    @FXML
    private RadioButton outsourcedRadio;
    @FXML
    private TextField partInvTxt;
    @FXML
    private TextField partMaxTxt;
    @FXML
    private TextField partMinTxt;
    @FXML
    private TextField partNameTxt;
    @FXML
    private TextField partPriceTxt;
    @FXML
    private Label partConstructLabel;
    @FXML
    private TextField partConstructTxt;

    /** Sets the final user input text field's label to "MachineID".
     * Upon selecting the In House radio the label will change to "MachineID".
     * @param event ActionEvent object holding information on the button pressed
     */
    @FXML
    void onActionInHouse(ActionEvent event) {
        partConstructLabel.setText("MachineID");
    }

    /** Sets the final user input text field's label to "Company Name".
     * Upon selecting the Outsourced radio the label will change to "Company Name".
     * @param event ActionEvent object holding information on the button pressed
     */
    @FXML
    void onActionOutsourced(ActionEvent event) {
        partConstructLabel.setText("Company Name");
    }

    /** Event handler for cancel button.
     * Cancel button will pass ActionEvent object that is created when the button is pressed.
     * Calls cancel method via Navigation object. Passes event and string, "MainMenu", for FXMLLoader to use.
     * Confirmation prompts user to cancel or return to add part screen. Canceling returns user to main menu.
     * See Controller package > Navigation class > cancel method.
     * @param event ActionEvent object holding information on the button pressed
     * @throws IOException
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        nav.cancel(event);
    }

    /** Event handler for save button on add part menu.
     * Parses data, checks data types,  creates part based on radio selection, and saves part to allParts list.
     * @param event ActionEvent object holding information on the button pressed
     * @throws ArithmeticException
     * @throws NumberFormatException
     */
    @FXML
    void onActionSavePart(ActionEvent event) throws  ArithmeticException, NumberFormatException {
        try {
            int stock = Integer.parseInt(partInvTxt.getText());
            int min = Integer.parseInt(partMinTxt.getText());
            int max = Integer.parseInt(partMaxTxt.getText());
            if (stock < min || stock > max || min > max) {
                throw new ArithmeticException();
            }

            String name = partNameTxt.getText();
            if (name.isBlank()) {
                throw new Exception();
            }

            double price = Double.parseDouble(partPriceTxt.getText());
            int id  = Inventory.populatePartId();

           if(inHouseRadio.isSelected()) {
                int machineId = Integer.parseInt(partConstructTxt.getText());
                Part newPart = new InHouse(id, name, price, stock, min, max, machineId);
                Inventory.addPart(newPart);
            } else if(outsourcedRadio.isSelected()) {
                String companyName = partConstructTxt.getText();
                if (companyName.isBlank()) {
                    throw new Exception();
                }

                Part newPart = new Outsourced(id, name, price, stock, min, max, companyName);
                Inventory.addPart(newPart);
            }
            nav.navigate(event, "MainMenu");

        } catch (NumberFormatException e) {
            Alerts.alertError("Numeric values in fields: Inv, Price, Max, Min, and MachineID (if prompted). Decimal value in price field.");
        } catch (ArithmeticException e) {
            Alerts.alertError("Min must be smaller than inv and inv must be smaller than max.");
        } catch (Exception e) {
            Alerts.alertError("Please enter a name and company name (if prompted) for the part.");
        }
    }

    /** Initializes controller for use once root element has been set.
     * Override for Initializable class initialize method.
     * First method called for controller when screen is loaded.
     * @param url location used for the root to find relative paths
     * @param resourceBundle resources to find root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
