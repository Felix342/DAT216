package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class CategoryItemFavorite extends AnchorPane implements ICategoryItem{

    @FXML private AnchorPane categoryLink;
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
    public void onClick() {
        SelectedCategoryItem.setCategoryItem(this);
        parentController.showShopPaneFavorite();
    }

    public void setStatus(boolean isSelected) {
        if (isSelected) {
            categoryLink.getStyleClass().add("catitem-selected");
        }
        else {
            categoryLink.getStyleClass().remove("catitem-selected");
        }
    }
}
