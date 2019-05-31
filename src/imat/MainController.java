package imat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class MainController implements Initializable {

    private IMatDataHandler iMatDataHandler;
    private List<Product> products;
    private int productIndex = 0;
    private final int productsPerPage = 12;

    private List<Order> orders;
    private int orderIndex = 0;
    private final int ordersPerPage = 10;

    private List<ShoppingItem> historyItems;
    private int historyitemIndex = 0;
    private final int historyItemsPerPage = 10;

    private List<ShoppingItem> paymentItems;
    private int paymentItemIndex = 0;
    private final int paymentItemsPerPage = 3;
    private Order currentHistoryOrder;

    // Navbar
    @FXML private TextField searchBar;

    // Main panes
    @FXML private Button toShopButton;
    @FXML private Button earlierRecieptsButton;
    @FXML private SplitPane shopPane;
    @FXML private AnchorPane userPane;
    @FXML private StackPane paymentPane;
    @FXML private StackPane historyPane;
    @FXML private AnchorPane startPane;
    @FXML private FlowPane testFlowPane;
    @FXML private FlowPane productList;

    // User info
    @FXML private TextField firstnameEdit;
    @FXML private TextField lastnameEdit;
    @FXML private TextField addressEdit;
    @FXML private TextField emailEdit;
    @FXML private Text confirmationUserText;

    // Shopping pane
    @FXML private FlowPane categoryList;
    @FXML private FlowPane shoppingcartList;
    @FXML private Text shoppingcartTotalItems;
    @FXML private Text shoppingcartTotalPrice;
    @FXML private Text resultKeyword;
    @FXML private ComboBox sortChoice;

    // History
    @FXML private FlowPane historyOverall;
    @FXML private AnchorPane historyCartView;
    @FXML private AnchorPane historyItemView;
    @FXML private FlowPane historyView;

    // Payment
    @FXML private AnchorPane paymentStage1;
    @FXML private AnchorPane paymentStage2;
    @FXML private AnchorPane paymentStage3;
    @FXML private AnchorPane paymentStage4;

    @FXML private TextField firstnamePayment;
    @FXML private TextField lastnamePayment;
    @FXML private TextField addressPayment;
    @FXML private TextField emailPayment;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox deliveryTime;
    @FXML private Text paymentUserHint1;

    @FXML private TextField cardnumberPayment;
    @FXML private TextField cardOwnerPayment;
    @FXML private TextField cardExpiryDatePayment;
    @FXML private TextField cardCVCPayment;
    @FXML private Text paymentUserHint2;

    @FXML private FlowPane confirmItemList;
    @FXML private Text confirmTotalPrice;
    @FXML private Text paymentDeliveryFinal;
    @FXML private Text paymentTotalAmount;
    @FXML private Text paymentTotalCost;

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
                    SelectedCategoryItem.clearSelected();
                    showShopPane();
                }
            }
        });
        iMatDataHandler.getShoppingCart().addShoppingCartListener(new ShoppingCartListener() {
            @Override
            public void shoppingCartChanged(CartEvent cartEvent) {
                populateCurrentShopPage();
                renderCart();
                double totalPrice = round(iMatDataHandler.getShoppingCart().getTotal(), 2);
                shoppingcartTotalPrice.textProperty().set(totalPrice + "kr");
                int numberOfItems = 0;
                for(ShoppingItem item : iMatDataHandler.getShoppingCart().getItems()) {
                    if(item.getProduct().getUnitSuffix().equals("kg")) {
                        numberOfItems++;
                    }
                    else {
                        numberOfItems += item.getAmount();
                    }
                }
                shoppingcartTotalItems.textProperty().set(Integer.toString(numberOfItems));
            }
        });
        double totalPrice = round(iMatDataHandler.getShoppingCart().getTotal(), 2);
        shoppingcartTotalPrice.textProperty().set(totalPrice + "kr");
        int numberOfItems = 0;
        for(ShoppingItem item : iMatDataHandler.getShoppingCart().getItems()) {
            if(item.getProduct().getUnitSuffix().equals("kg")) {
                numberOfItems++;
            }
            else {
                numberOfItems += item.getAmount();
            }
        }
        shoppingcartTotalItems.textProperty().set(Integer.toString(numberOfItems));
        renderCart();
        populateCategories();
        preparePaymentStep1();
        sortChoice.getItems().addAll("Namn", "Billigast", "Dyrast");
        sortChoice.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                sortProducts();
                populateCurrentShopPage();
            }
        });
        for(int i = 8; i <= 20; i += 2) {
            deliveryTime.getItems().add(i + ":00 - " + (i + 2) + ":00");
        }
    }

    public IMatDataHandler getIMatDataHandler() {
        return iMatDataHandler;
    }

    @FXML
    private void shopPaneToFront() {
        shopPane.toFront();
    }

    @FXML
    private void stage1ToFront() {
        paymentStage1.toFront();
    }

    @FXML
    private void stage2ToFront() {
        paymentStage2.toFront();
    }

    @FXML
    private void showShopPane() {
        shopPane.toFront();

        productList.getChildren().clear();
        String searchString = searchBar.getText();
        products = iMatDataHandler.findProducts(searchString);
        resultKeyword.textProperty().set(searchString);
        productIndex = 0;

        sortProducts();
        populateCurrentShopPage();
    }

    @FXML
    protected void showShopPaneByCategory(ProductCategory pc) {
        searchBar.setText("");

        productList.getChildren().clear();
        products = iMatDataHandler.getProducts(pc);
        String category = pc.name().toLowerCase().replace('_', ' ');
        resultKeyword.textProperty().set(category);
        productIndex = 0;
        sortProducts();
        populateCurrentShopPage();
    }

    public void showShopPaneAll() {
        searchBar.setText("");

        productList.getChildren().clear();
        products = iMatDataHandler.getProducts();
        resultKeyword.textProperty().set("Alla");
        productIndex = 0;
        sortProducts();
        populateCurrentShopPage();
    }

    public void showShopPaneFavorite() {
        searchBar.setText("");

        productList.getChildren().clear();
        products = iMatDataHandler.favorites();
        resultKeyword.textProperty().set("Favoriter");
        productIndex = 0;
        sortProducts();
        populateCurrentShopPage();
    }

    private void sortProducts() {
        if(sortChoice.getValue() != null && !sortChoice.getValue().toString().isEmpty()) {
            Comparator c = null;
            switch(sortChoice.getValue().toString()) {
                case "Namn":
                    c = Comparator.comparing(Product::getName);
                    break;
                case "Billigast":
                    c = Comparator.comparingDouble(Product::getPrice);
                    break;
                case "Dyrast":
                    c = Comparator.comparingDouble(Product::getPrice).reversed();
                    break;
            }
            if(c != null) {
                products.sort(c);
            }

        }
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
    private void showUserPane() {
        confirmationUserText.visibleProperty().set(false);
        userPane.toFront();
    }

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

        Collections.sort(orders, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));

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
        String firstName = customer.getFirstName();
        if(firstName != "") {
            firstnameEdit.setText(firstName);
        }
        String lastName = customer.getLastName();
        if(lastName != "") {
            lastnameEdit.setText(lastName);
        }
        String adress = customer.getAddress();
        if(adress != "") {
            addressEdit.setText(adress);
        }
        String email = customer.getEmail();
        if(email != "") {
            emailEdit.setText(email);
        }
    }

    @FXML
    private void saveUserFields() {
        Customer customer = iMatDataHandler.getCustomer();
        customer.setFirstName(firstnameEdit.getText());
        customer.setLastName(lastnameEdit.getText());
        customer.setAddress(addressEdit.getText());
        customer.setEmail(emailEdit.getText());
        confirmationUserText.visibleProperty().set(true);
    }

    private void renderCart() {
        shoppingcartList.getChildren().clear();
        ShoppingCart cart = iMatDataHandler.getShoppingCart();
        List<ShoppingItem> items = cart.getItems();
        for(int i = items.size() - 1; i >= 0; i--) {
            ShoppingcartItem sci = new ShoppingcartItem(this, items.get(i));
            shoppingcartList.getChildren().add(sci);
        }
    }

    @FXML
    private void emptyShoppingCart() {
        iMatDataHandler.getShoppingCart().clear();
    }

    private void populateCategories() {
        categoryList.getChildren().add(new CategoryItemAll(this));
        categoryList.getChildren().add(new CategoryItemFavorite(this));
        ProductCategory[] productCategories = ProductCategory.values().clone();
        Arrays.sort(productCategories, Comparator.comparing(Enum::name));
        for(ProductCategory pc : productCategories) {
            categoryList.getChildren().add(new CategoryItem(this, pc));
        }
    }

    public void showOrdersCart(Order order) {
        historyItemView.toFront();
        currentHistoryOrder = order;
        historyItems = order.getItems();
        populateCurrentHistoryItemPane();
    }

    @FXML
    private void loadCurrentCart() {
        iMatDataHandler.getShoppingCart().clear();
        for(ShoppingItem item : historyItems) {
            iMatDataHandler.getShoppingCart().addItem(item);
        }
        showShopPane();
    }

    @FXML
    private void populateCurrentHistoryItemPane() {
        ObservableList<Node> children = historyView.getChildren();
        children.clear();
        List<ShoppingItem> currentPageItems = historyItems.subList(historyitemIndex, Math.min(historyitemIndex + historyItemsPerPage, historyItems.size()));
        for(ShoppingItem i : currentPageItems) {
            children.add(new HistoryItem(this, i));
        }
    }

    @FXML private void firstItemPage() {
        historyitemIndex = 0;
        populateCurrentHistoryItemPane();
    }

    @FXML private void previousItemPage() {
        if(historyitemIndex - historyItemsPerPage < 0) {
            historyitemIndex = 0;
        }
        else {
            historyitemIndex -= historyItemsPerPage;
        }

        populateCurrentHistoryItemPane();
    }


    @FXML private void nextItemPage() {
        if(historyItems.size() <= historyitemIndex + historyItemsPerPage)
            return;

        historyitemIndex += historyItemsPerPage;
        populateCurrentHistoryItemPane();
    }

    @FXML
    private void showPaymentPane() {
        paymentStage1.toFront();
        paymentPane.toFront();
    }

    private void preparePaymentStep1() {
        paymentStage1.toFront();
        Customer customer = iMatDataHandler.getCustomer();
        firstnamePayment.setText(customer.getFirstName());
        lastnamePayment.setText(customer.getLastName());
        addressPayment.setText(customer.getAddress());
        emailPayment.setText(customer.getEmail());
        paymentUserHint1.visibleProperty().set(false);
    }

    private boolean isInvoice = false;

    @FXML private void paymentStepDone1() {
        Customer customer = iMatDataHandler.getCustomer();
        String firstName = firstnamePayment.getText();
        String lastName = lastnamePayment.getText();
        String adress = addressPayment.getText();
        String email = emailPayment.getText();
        LocalDate date = datePicker.valueProperty().get();
        Object time = deliveryTime.getValue();
        if(!firstName.equals("") && !lastName.equals("") && !adress.equals("") && !email.equals("") && date != null && time != null) {
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddress(adress);
            customer.setEmail(email);
            isInvoice = false;
            preparePaymentStep2();
            paymentStage2.toFront();
        }
        else {
            paymentUserHint1.visibleProperty().set(true);
        }
    }

    private void preparePaymentStep2() {
        CreditCard card = iMatDataHandler.getCreditCard();
        cardnumberPayment.setText(card.getCardNumber());
        cardOwnerPayment.setText(card.getHoldersName());
        if(card.getValidMonth() != 0 && card.getValidYear() != 0) {
            String month = String.format("%02d", card.getValidMonth());
            String year = String.format("%02d", card.getValidYear());
            cardExpiryDatePayment.setText(card.getValidMonth() + "/" + card.getValidYear());
        }
        paymentUserHint2.visibleProperty().set(false);
    }

    @FXML private void invoicePayment() {
        Customer customer = iMatDataHandler.getCustomer();
        String firstName = firstnamePayment.getText();
        String lastName = lastnamePayment.getText();
        String adress = addressPayment.getText();
        String email = emailPayment.getText();
        LocalDate date = datePicker.valueProperty().get();
        Object time = deliveryTime.getValue();
        if(!firstName.equals("") && !lastName.equals("") && !adress.equals("") && !email.equals("") && date != null && time != null) {
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddress(adress);
            customer.setEmail(email);
            isInvoice = true;
            preparePaymentStep3();
            paymentStage3.toFront();
        }
        else {
            paymentUserHint1.visibleProperty().set(true);
        }
    }

    @FXML private void goBackFromStep3() {
        if(isInvoice) {
            stage1ToFront();
        }
        else {
            stage2ToFront();
        }
    }

    @FXML private void paymentStepDone2() {
        CreditCard card = iMatDataHandler.getCreditCard();
        String cardNumber = cardnumberPayment.getText();
        String ownerName = cardOwnerPayment.getText();
        String cvcCode = cardCVCPayment.getText();
        String expiryDate = cardExpiryDatePayment.getText();
        if(!cardNumber.equals("") && !ownerName.equals("") && !cvcCode.equals("") && !expiryDate.equals("")) {
            card.setCardNumber(cardNumber);
            card.setHoldersName(ownerName);
            String[] valid = expiryDate.split("/");
            if(valid.length == 2) {
                card.setValidMonth(Integer.parseInt(valid[0]));
                card.setValidYear(Integer.parseInt(valid[1]));
            }
            preparePaymentStep3();
            paymentStage3.toFront();
        }
        else {
            paymentUserHint2.visibleProperty().set(true);
        }
    }

    @FXML private void preparePaymentStep3() {
        DateFormat weekDayFormat = new SimpleDateFormat("EEEE d MMMM", new Locale("sv", "SE"));
        String day = weekDayFormat.format(java.sql.Date.valueOf(datePicker.getValue()));
        String time = deliveryTime.getValue().toString();
        paymentDeliveryFinal.textProperty().set(day + " " + time);

        int numberOfItems = 0;
        for(ShoppingItem item : iMatDataHandler.getShoppingCart().getItems()) {
            if(item.getProduct().getUnitSuffix().equals("kg")) {
                numberOfItems++;
            }
            else {
                numberOfItems += item.getAmount();
            }
        }
        paymentTotalAmount.textProperty().set(Integer.toString(numberOfItems));

        double totalPrice = round(iMatDataHandler.getShoppingCart().getTotal(), 2);
        paymentTotalCost.setText(totalPrice + "kr");
    }

    @FXML private void paymentStepDone3() {
        iMatDataHandler.placeOrder();
        paymentStage4.toFront();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}


