package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class CategoryItemAll extends AnchorPane implements ICategoryItem {

    @FXML private AnchorPane categoryLink;
    @FXML private Text categoryName;

    private MainController parentController;

    public CategoryItemAll(MainController controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        parentController = controller;
        categoryName.textProperty().set("Alla produkter");
    }

    @FXML
    public void onClick() {
        SelectedCategoryItem.setCategoryItem(this);
        parentController.showShopPaneAll();
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
