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

    private final double increaseAmountKg = 0.1;

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

        String amount;
        if(product.getUnitSuffix().equals("kg")) {
            amount = controller.round(getAmount(), 1) + "kg";
        }
        else {
            amount = (int)getAmount() + "st";
        }
        productAmount.textProperty().set(amount);
    }

    @FXML
    protected void addOneItem(Event event) {
        ShoppingItem item = getItem(this.product);
        boolean isWeight = this.product.getUnitSuffix().equals("kg");
        if(item == null) {
            iMatDataHandler.getShoppingCart().addProduct(this.product);
            if(isWeight) {
                getItem(this.product).setAmount(increaseAmountKg);
            }
        }
        else {
            if(isWeight) {
                item.setAmount(item.getAmount() + increaseAmountKg);
            }
            else {
                item.setAmount(item.getAmount() + 1);
            }
        }

        iMatDataHandler.getShoppingCart().fireShoppingCartChanged(item, true);

//        productAmount.textProperty().set(Integer.toString((int)getAmount()));
    }

    @FXML
    protected void removeOneItem() {
        ShoppingItem item = getItem(this.product);
        boolean isWeight = this.product.getUnitSuffix().equals("kg");
        if(item == null || item.getAmount() <= 0) {
            return;
        }
        else {
            double newAmount = item.getAmount();
            if(isWeight)
                newAmount -= increaseAmountKg;
            else
                newAmount -= 1;

            if(newAmount <= 0)
                iMatDataHandler.getShoppingCart().removeItem(this.shoppingItem);
            else
                item.setAmount(newAmount);
            iMatDataHandler.getShoppingCart().fireShoppingCartChanged(item, false);
        }

//        productAmount.textProperty().set(Integer.toString((int)getAmount()));
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

    @FXML
    public void toggleFavorite() {
        String imagePath;
        if(iMatDataHandler.isFavorite(product)) {
            iMatDataHandler.removeFavorite(product);
            imagePath = "resources/notfavorite.png";
        }
        else {
            iMatDataHandler.addFavorite(product);
            imagePath = "resources/favorite.png";
        }

        Image starImage = new Image(getClass().getResource(imagePath).toString(), true);
        productFavorite.imageProperty().set(starImage);
    }
}
