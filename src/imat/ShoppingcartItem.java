package imat;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.util.List;

public class ShoppingcartItem extends AnchorPane {

    @FXML private Text nameShoppingcartItem;
    @FXML private Text priceShoppingCartItem;
    @FXML private Text amountShoppingcartItem;

    private IMatDataHandler iMatDataHandler;
    private Product product;
    private ShoppingItem shoppingItem;

    public ShoppingcartItem(MainController controller, ShoppingItem item) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("shoppingcart_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.iMatDataHandler = controller.getIMatDataHandler();
        this.product = item.getProduct();

        nameShoppingcartItem.textProperty().set(product.getName());
        priceShoppingCartItem.textProperty().set(Double.toString(product.getPrice()) + "kr");
        amountShoppingcartItem.textProperty().set(Double.toString(item.getAmount()));
    }

    @FXML
    protected void addOneItem(Event event) {
        ShoppingItem item = getItem(this.product);
        if(item == null) {
            iMatDataHandler.getShoppingCart().addProduct(this.product);
        }
        else {
            item.setAmount(item.getAmount() + 1);
        }

        iMatDataHandler.getShoppingCart().fireShoppingCartChanged(item, true);

        amountShoppingcartItem.textProperty().set(Double.toString(getAmount()));
    }

    @FXML
    protected void removeOneItem() {
        ShoppingItem item = getItem(this.product);
        if(item == null || item.getAmount() < 1) {
            return;
        }
        else {
            item.setAmount(item.getAmount() - 1);
            iMatDataHandler.getShoppingCart().fireShoppingCartChanged(item, false);
        }

        amountShoppingcartItem.textProperty().set(Double.toString(getAmount()));
    }

    private ShoppingItem getItem(Product p) {
        if(this.shoppingItem != null)
            return this.shoppingItem;

        List<ShoppingItem> items = iMatDataHandler.getShoppingCart().getItems();
        for(ShoppingItem item : items) {
            if(item.getProduct() == product) {
                this.shoppingItem = item;
                return item;
            }
        }

        return null;
    }

    private double getAmount() {
        ShoppingItem si = getItem(this.product);
        if(si == null) {
            return 0.0;
        }
        else {
            return si.getAmount();
        }
    }
}
