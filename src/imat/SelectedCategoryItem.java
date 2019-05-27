package imat;

public class SelectedCategoryItem { // Make singleton
    static private ICategoryItem categoryItem;

//    static public ICategoryItem getCategoryItem() {
//        return categoryItem;
//    }

    static public void setCategoryItem(ICategoryItem ci) {
        if(categoryItem != null) {
            categoryItem.setStatus(false);
        }
        categoryItem = ci;
        categoryItem.setStatus(true);
    }

    static public void clearSelected() {
        if(categoryItem != null) {
            categoryItem.setStatus(false);
        }
    }
}
