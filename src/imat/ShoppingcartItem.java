package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;

public class ShoppingcartItem extends AnchorPane {

    @FXML private Text nameShoppingcartItem;
    @FXML private Text priceShoppingCartItem;
    @FXML private Text amountShoppingcartItem;

    public ShoppingcartItem(MainController controller, Product product) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("shoppingcart_item.fxml"));
        fxmlLoader.setController(this);

        nameShoppingcartItem.textProperty().set(product.getName());
        priceShoppingCartItem.textProperty().set(Double.toString(product.getPrice()));


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
