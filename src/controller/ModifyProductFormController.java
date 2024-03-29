package controller;

import model.Part;
import java.net.URL;
import model.Product;
import model.Inventory;
import javafx.fxml.FXML;
import java.util.Optional;
import java.io.IOException;
import javafx.scene.control.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Controls user input to modify product screen.*/
public class ModifyProductFormController implements Initializable {

    private int id;
    private int index;
    Navigation nav = new Navigation();
    private ObservableList<Part> partsTemp = FXCollections.observableArrayList();

    @FXML
    private TableView<Part> allPartsTableView;
    @FXML
    private TableView<Part> assocPartsTableView;
    @FXML
    private TableColumn<Part, Integer> allPartIdCol;
    @FXML
    private TableColumn<Part, Integer> allPartInvCol;
    @FXML
    private TableColumn<Part, String> allPartNameCol;
    @FXML
    private TableColumn<Part, Double> allPartPriceCol;
    @FXML
    private TableColumn<Part, Integer> assocPartIdCol;
    @FXML
    private TableColumn<Part, Integer> assocPartInvCol;
    @FXML
    private TableColumn<Part, String> assocPartNameCol;
    @FXML
    private TableColumn<Part, Double> assocPartPriceCol;
    @FXML
    private TextField productAddPartSearch;
    @FXML
    private TextField productIdTxt;
    @FXML
    private TextField productInvTxt;
    @FXML
    private TextField productMaxTxt;
    @FXML
    private TextField productMinTxt;
    @FXML
    private TextField productNameTxt;
    @FXML
    private TextField productPriceTxt;

    /** Searches all available parts for a name or id query.
     * Uses Search class method searchFor to display parts queried.
     * @param event ActionEvent object holding information on the button pressed
     */
    @FXML
    void onActionSearchParts(ActionEvent event) {
        Search.searchFor("Part", productAddPartSearch, allPartsTableView);
    }

    /** Adds part to temp associated parts list.
     * Updates associated parts table view.
     * @param event ActionEvent object holding information on the button pressed
     */
    @FXML
    void onActionAddAssocPart(ActionEvent event) {
        Part selectedPart = allPartsTableView.getSelectionModel().getSelectedItem();
        partsTemp.add(selectedPart);
        Populate.tableView(assocPartsTableView, partsTemp, assocPartIdCol, assocPartInvCol, assocPartNameCol, assocPartPriceCol);
    }

    /** Removes part from temp associated parts list.
     * Updates associated parts table view.
     * No permanent changes made until save is clicked.
     * @param event ActionEvent object holding information on the button pressed
     */
    @FXML
    void onActionRemoveAssocPart(ActionEvent event) {
        Optional<ButtonType> result = Alerts.alertConfirm("Click OK to confirm removal of part.");
        if(result.get() == ButtonType.OK) {
            Part selectedPart = assocPartsTableView.getSelectionModel().getSelectedItem();
            partsTemp.remove(selectedPart);
            Populate.tableView(assocPartsTableView, partsTemp, assocPartIdCol, assocPartInvCol, assocPartNameCol, assocPartPriceCol);
        }
    }

    /** Event handler for cancel button.
     * Cancel button will pass ActionEvent object that is created when the button is pressed.
     * Calls cancel method via Navigation object. Passes event and string, "MainMenu", for FXMLLoader to use.
     * Confirmation prompts user to cancel or return to modify product screen. Canceling returns user to main menu.
     * See Controller package > Navigation class > cancel method.
     * @param event ActionEvent object holding information on the button pressed
     * @throws IOException
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        nav.cancel(event);
    }

    /** Saves changes to product.
     * Parses data that has been input from user.
     * Creates new Product object with data and inserts into original Product index in allProducts.
     * @param event ActionEvent object holding information on the button pressed
     * @throws IOException
     * @throws ArithmeticException
     * @throws NumberFormatException
     * @throws Exception
     */
    @FXML
    void onActionSaveProduct(ActionEvent event) throws IOException {
        try {
            int stock = Integer.parseInt(productInvTxt.getText());
            int min = Integer.parseInt(productMinTxt.getText());
            int max = Integer.parseInt(productMaxTxt.getText());
            if (stock < min || stock > max || min > max) {
                throw new ArithmeticException();
            }

            String name = productNameTxt.getText();
            if (name.isBlank()) {
                throw new Exception();
            }

            double price = Double.parseDouble(productPriceTxt.getText());
            id = Integer.parseInt(productIdTxt.getText());

            Product newProduct = new Product(id, name, price, stock, min, max);
            for (Part associatedPart : partsTemp) {
                newProduct.addAssociatedPart(associatedPart);
            }
            Inventory.updateProduct(index, newProduct);
            nav.navigate(event, "MainMenu");

        } catch (NumberFormatException e) {
            Alerts.alertError("Numeric values in fields: Inv, Price, Max, and Min. Decimal value in price field.");
        } catch (ArithmeticException e) {
            Alerts.alertError("Min must be smaller than inv and inv must be smaller than max.");
        } catch (Exception e) {
            Alerts.alertError("Please enter a name for the product.");
        }
    }

    /** Parses through data of Product object to populate UI of modify product screen.
     * Populates temp part list with associated parts.
     * @param product Product object to be parsed for populating text fields
     */
    public void sendProduct(Product product) {
        productIdTxt.setText(String.valueOf(product.getId()));
        productNameTxt.setText(product.getName());
        productInvTxt.setText(String.valueOf(product.getStock()));
        productMinTxt.setText(String.valueOf(product.getMin()));
        productMaxTxt.setText(String.valueOf(product.getMax()));
        productPriceTxt.setText(String.valueOf(product.getPrice()));

        for (Part part : product.getAllAssociatedParts()) {
            partsTemp.add(part);
        }
        index = Inventory.getAllProducts().indexOf(product);
    }

    /** Initializes controller for use once root element has been set.
     * Populates table views.
     * Override for Initializable class initialize method.
     * First method called for controller when screen is loaded.
     * @param url location used for the root to find relative paths
     * @param resourceBundle resources to find root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Populate.tableView(allPartsTableView, Inventory.getAllParts(), allPartIdCol, allPartInvCol, allPartNameCol, allPartPriceCol);
        Populate.tableView(assocPartsTableView, partsTemp, assocPartIdCol, assocPartInvCol, assocPartNameCol, assocPartPriceCol);
    }


}
