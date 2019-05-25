package imat;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingCart;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.util.List;


public class ProductItem extends AnchorPane {

    @FXML private Text productName;
    @FXML private ImageView productImage;
    @FXML private ImageView productFavorite;
    @FXML private Text productPrice;
    @FXML private Text productAmount;


    private MainController parentController;
    private IMatDataHandler iMatDataHandler;
    private ShoppingItem shoppingItem;
    private Product product;

    public ProductItem(MainController controller, Product p) {
        this.product = p;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.parentController = controller;
        iMatDataHandler = this.parentController.getIMatDataHandler();

        productName.textProperty().set(product.getName());

        this.productImage.imageProperty().setValue(iMatDataHandler.getFXImage(product));

        boolean isFavorite = parentController.getIMatDataHandler().isFavorite(product);
        String starImagePath = isFavorite ? "resources/favorite.png" : "resources/notfavorite.png";
        Image starImage = new Image(getClass().getResource(starImagePath).toString(), true);
        productFavorite.imageProperty().set(starImage);

        String priceAndUnit = Double.toString(product.getPrice()) + product.getUnit();
        productPrice.textProperty().set(priceAndUnit);

        productAmount.textProperty().set(Integer.toString((int)getAmount()));
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

        productAmount.textProperty().set(Integer.toString((int)getAmount()));
    }

    @FXML
    protected void removeOneItem() {
        ShoppingItem item = getItem(this.product);
        if(item == null || item.getAmount() < 1) {
            return;
        }
        else {
            double newAmount = item.getAmount() - 1;
            if(newAmount <= 0)
                iMatDataHandler.getShoppingCart().removeItem(this.shoppingItem);
            else
                item.setAmount(item.getAmount() - 1);
            iMatDataHandler.getShoppingCart().fireShoppingCartChanged(item, false);
        }

        productAmount.textProperty().set(Integer.toString((int)getAmount()));
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
