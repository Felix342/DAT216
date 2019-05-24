package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static imat.MainController.round;

public class HistorycartItem extends AnchorPane {
    @FXML private Text dateHistoryCart;
    @FXML private Text totalPriceHistoryCart;

    private MainController controller;
    private Order order;


    public HistorycartItem(MainController c, Order o) {
        controller = c;
        order = o;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("historycart_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern);

        dateHistoryCart.textProperty().set(df.format(order.getDate()));

        List<ShoppingItem> items = order.getItems();
        double totalCost = 0;
        for(ShoppingItem item : items) {
            totalCost += item.getTotal();
        }

        totalCost = round(totalCost, 2);

        totalPriceHistoryCart.textProperty().set(totalCost + "kr");
    }

    @FXML
    public void openCart() {
        controller.showOrdersCart(order);
    }
}
