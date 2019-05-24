package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class HistoryItem extends AnchorPane {
    @FXML private Text nameHistoryItem;
    @FXML private Text amountHistoryItem;
    @FXML private Text priceHistoryItem;
    @FXML private ImageView imageHistoryItem;

    private IMatDataHandler iMatDataHandler;

    public HistoryItem(MainController c, ShoppingItem item) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("history_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        iMatDataHandler = c.getIMatDataHandler();
        Product product = item.getProduct();
        nameHistoryItem.textProperty().set(product.getName());
        amountHistoryItem.textProperty().set(Double.toString(item.getAmount()));
        priceHistoryItem.textProperty().set(product.getPrice() + "kr");
        imageHistoryItem.imageProperty().setValue(iMatDataHandler.getFXImage(product));
    }
}
