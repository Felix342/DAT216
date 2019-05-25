package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class CategoryItemFavorite extends AnchorPane {

    @FXML private Text categoryName;

    private MainController parentController;

    public CategoryItemFavorite(MainController controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        parentController = controller;
        categoryName.textProperty().set("Favoriter");
    }

    @FXML
    private void onClick() {
        parentController.showShopPaneFavorite();
    }
}
