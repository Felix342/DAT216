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

public class HistorycartItem extends AnchorPane {
    @FXML private Text dateHistoryCart;
    @FXML private Text totalPriceHistoryCart;


    public HistorycartItem(Order order) {
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

        totalPriceHistoryCart.textProperty().set(totalCost + "kr");
    }
}
