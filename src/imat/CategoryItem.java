package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.ProductCategory;

import java.io.IOException;

public class CategoryItem extends AnchorPane implements ICategoryItem {

    @FXML private AnchorPane categoryLink;
    @FXML private Text categoryName;

    private MainController parentController;
    private ProductCategory productCategory;

    public CategoryItem(MainController controller, ProductCategory pc) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("category_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        productCategory = pc;
        parentController = controller;
        String category = productCategory.name().toLowerCase().replace('_', ' ');
        categoryName.textProperty().set(category.substring(0, 1).toUpperCase() + category.substring(1));
    }

    @FXML
    public void onClick() {
        SelectedCategoryItem.setCategoryItem(this);
        parentController.showShopPaneByCategory(productCategory);
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
