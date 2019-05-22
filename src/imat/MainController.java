package imat;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import se.chalmers.cse.dat216.project.Customer;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private IMatDataHandler iMatDataHandler;
    private List<Product> products;
    private int productIndex = 0;
    private final int productsPerPage = 9;

    @FXML private Button toShopButton;
    @FXML private Button earlierRecieptsButton;
    @FXML private SplitPane shopPane;
    @FXML private AnchorPane userPane;
    @FXML private StackPane paymentPane;
    @FXML private StackPane historyPane;
    @FXML private AnchorPane startPane;
    @FXML private FlowPane testFlowPane;
    @FXML private FlowPane productList;

    @FXML private TextField firstnameEdit;
    @FXML private TextField lastnameEdit;
    @FXML private TextField addressEdit;
    @FXML private TextField emailEdit;

    @FXML private ImageView testImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        testFlowPane.getChildren().add(new ProductItem(this));
        iMatDataHandler = IMatDataHandler.getInstance();
        populateUserFields(iMatDataHandler.getCustomer());
    }

    public IMatDataHandler getIMatDataHandler() {
        return iMatDataHandler;
    }

    @FXML
    private void showShopPane() {
        shopPane.toFront();

        productList.getChildren().clear();
        products = iMatDataHandler.getProducts();
        productIndex = 0;
        populateCurrentShopPage();
    }

    private void populateCurrentShopPage() {
        ObservableList<Node> children = productList.getChildren();
        children.clear();
        List<Product> currentPageProducts = products.subList(productIndex, Math.min(productIndex + productsPerPage, products.size()));
        for(Product p : currentPageProducts) {
            children.add(new ProductItem(this, p));
        }
    }

    @FXML
    private void nextShopPage() {
        if(productIndex + productsPerPage > products.size())
            return;
        productIndex += productsPerPage;
        populateCurrentShopPage();
    }

    @FXML
    private void previousShopPage() {
        if(productIndex - productsPerPage < 0) {
            productIndex = 0;
        }
        else {
            productIndex -= productsPerPage;
        }
        populateCurrentShopPage();
    }

    @FXML
    private void firstShopPage() {
        productIndex = 0;
        populateCurrentShopPage();
    }

    @FXML
    private void showUserPane() { userPane.toFront(); }

    @FXML
    private void showPaymentPane() { paymentPane.toFront(); }

    @FXML
    private void showHistoryPane() { historyPane.toFront(); }

    @FXML
    private void showStartPane() { startPane.toFront(); }

    @FXML
    private void test() {
        System.out.println("test");
    }

    private void populateUserFields(Customer customer) {
        firstnameEdit.setText(customer.getFirstName());
        lastnameEdit.setText(customer.getLastName());
        addressEdit.setText(customer.getAddress());
        emailEdit.setText(customer.getEmail());
    }

    @FXML
    private void saveUserFields() {
        Customer customer = iMatDataHandler.getCustomer();
        customer.setFirstName(firstnameEdit.getText());
        customer.setLastName(lastnameEdit.getText());
        customer.setAddress(addressEdit.getText());
        customer.setEmail(emailEdit.getText());
    }
}
