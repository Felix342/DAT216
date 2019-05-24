package imat;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private IMatDataHandler iMatDataHandler;
    private List<Product> products;
    private int productIndex = 0;
    private final int productsPerPage = 12;

    private List<Order> orders;
    private int orderIndex = 0;
    private final int ordersPerPage = 10;

    private List<ShoppingItem> historyItems;
    private int itemIndex = 0;
    private final int itemsPerPage = 10;

    @FXML private TextField searchBar;

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

    @FXML private FlowPane categoryList;
    @FXML private FlowPane shoppingcartList;
    @FXML private Text shoppingcartTotalItems;
    @FXML private Text shoppingcartTotalPrice;

    @FXML private FlowPane historyOverall;
    @FXML private AnchorPane historyCartView;
    @FXML private AnchorPane historyItemView;
    @FXML private FlowPane historyView;

    @FXML private ImageView testImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        testFlowPane.getChildren().add(new ProductItem(this));
        iMatDataHandler = IMatDataHandler.getInstance();
        populateUserFields(iMatDataHandler.getCustomer());
        searchBar.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                    showShopPane();
                }
            }
        });
        iMatDataHandler.getShoppingCart().addShoppingCartListener(new ShoppingCartListener() {
            @Override
            public void shoppingCartChanged(CartEvent cartEvent) {
                populateCurrentShopPage();
                renderCart();
                shoppingcartTotalPrice.textProperty().set(iMatDataHandler.getShoppingCart().getTotal() + "kr");
            }
        });
        shoppingcartTotalPrice.textProperty().set(iMatDataHandler.getShoppingCart().getTotal() + "kr");
        renderCart();
        populateCategories();
    }

    public IMatDataHandler getIMatDataHandler() {
        return iMatDataHandler;
    }

    @FXML
    private void showShopPane() {
        shopPane.toFront();

        productList.getChildren().clear();
        products = iMatDataHandler.findProducts(searchBar.getText());
        productIndex = 0;
        populateCurrentShopPage();
    }

    @FXML
    protected void showShopPaneByCategory(ProductCategory pc) {
        shopPane.toFront();

        productList.getChildren().clear();
        products = iMatDataHandler.getProducts(pc);
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
        if(productIndex + productsPerPage >= products.size())
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
    private void showHistoryTab() {
        orders = iMatDataHandler.getOrders();
        orderIndex = 0;
        showHistoryPane();
    }

    @FXML
    private void showHistoryPane() {
        historyPane.toFront();
        historyCartView.toFront();
        populateCurrentHistoryCartPane();
    }

    @FXML
    private void populateCurrentHistoryCartPane() {
        ObservableList<Node> children = historyOverall.getChildren();
        children.clear();
        List<Order> currentPageOrders = orders.subList(orderIndex, Math.min(orderIndex + ordersPerPage, orders.size()));
        for(Order o : currentPageOrders) {
            children.add(new HistorycartItem(this, o));
        }
    }

    @FXML private void firstCartPage() {
        orderIndex = 0;
        populateCurrentHistoryCartPane();
    }

    @FXML private void previousCartPage() {
        if(orderIndex - ordersPerPage < 0) {
            orderIndex = 0;
        }
        else {
            orderIndex -= ordersPerPage;
        }

        populateCurrentHistoryCartPane();
    }


    @FXML private void nextCartPage() {
        if(orders.size() <= orderIndex + ordersPerPage)
            return;

        orderIndex += ordersPerPage;
        populateCurrentHistoryCartPane();
    }

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

    private void renderCart() {
        shoppingcartList.getChildren().clear();
        ShoppingCart cart = iMatDataHandler.getShoppingCart();
        for(ShoppingItem item : cart.getItems()) {
            ShoppingcartItem sci = new ShoppingcartItem(this, item);
            shoppingcartList.getChildren().add(sci);
        }
    }

    @FXML
    private void emptyShoppingCart() {
        iMatDataHandler.getShoppingCart().clear();
    }

    private void populateCategories() {
        for(ProductCategory pc : ProductCategory.values()) {
            categoryList.getChildren().add(new CategoryItem(this, pc));
        }
    }

    @FXML
    private void orderCart() {
        iMatDataHandler.placeOrder(false);
    }

    public void showOrdersCart(Order order) {
        historyItemView.toFront();
        historyItems = order.getItems();
        populateCurrentHistoryItemPane();
    }

    @FXML
    private void populateCurrentHistoryItemPane() {
        ObservableList<Node> children = historyView.getChildren();
        children.clear();
        List<ShoppingItem> currentPageItems = historyItems.subList(itemIndex, Math.min(itemIndex + itemsPerPage, historyItems.size()));
        for(ShoppingItem i : currentPageItems) {
            children.add(new HistoryItem(this, i));
        }
    }

    @FXML private void firstItemPage() {
        itemIndex = 0;
        populateCurrentHistoryItemPane();
    }

    @FXML private void previousItemPage() {
        if(itemIndex - itemsPerPage < 0) {
            itemIndex = 0;
        }
        else {
            itemIndex -= itemsPerPage;
        }

        populateCurrentHistoryItemPane();
    }


    @FXML private void nextItemPage() {
        if(historyItems.size() <= itemIndex + itemsPerPage)
            return;

        itemIndex += itemsPerPage;
        populateCurrentHistoryItemPane();
    }
}
