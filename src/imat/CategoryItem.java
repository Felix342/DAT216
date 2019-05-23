package imat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.ProductCategory;

import java.io.IOException;

public class CategoryItem extends AnchorPane {

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
        categoryName.textProperty().set(productCategory.name());
    }

    @FXML
    private void onClick() {
        parentController.showShopPaneByCategory(productCategory);
    }
}
