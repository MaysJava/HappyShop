package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
public class CustomerModel {
    public CustomerView cusView;
    public DatabaseRW databaseRW; // Interface type, not specific implementation

    private Product theProduct = null; // product found from search
    private ArrayList<Product> trolley = new ArrayList<>(); // a list of products in trolley

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                        // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet";// Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    // SELECT productID, description, image, unitPrice, inStock quantity
    void search() throws SQLException {
        String productId = cusView.tfId.getText().trim();
        if (!productId.isEmpty()) {
            theProduct = databaseRW.searchByProductId(productId); // search database
            if (theProduct != null && theProduct.getStockQuantity() > 0) {
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();

                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println(displayLaSearchResult);
            } else {
                theProduct = null;
                displayLaSearchResult = "No Product was found with ID " + productId;
                System.out.println("No Product was found with ID " + productId);
            }
        } else {
            theProduct = null;
            displayLaSearchResult = "Please type ProductID";
            System.out.println("Please type ProductID.");
        }
        updateView();
    }

    void addToTrolley() {
        if (theProduct != null) {
            // Add/merge the searched product into the trolley (quantity +1), then sort by productId.
            addOrMergeToTrolley(theProduct);
            sortTrolleyById();

            displayTaTrolley = ProductListFormatter.buildString(trolley); // build a String for trolley so that we can show it
        } else {
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt = ""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void checkOut() throws IOException, SQLException {
        if (!trolley.isEmpty()) {
            // If trolley already holds merged items, grouping is cheap; otherwise it merges by id for DB check.
            ArrayList<Product> groupedTrolley = groupProductsById(trolley);
            ArrayList<Product> insufficientProducts = databaseRW.purchaseStocks(groupedTrolley);

            if (insufficientProducts.isEmpty()) { // stock is sufficient for all products
                OrderHub orderHub = OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);
                trolley.clear();
                displayTaTrolley = "";
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                System.out.println(displayTaReceipt);
            } else { // Some products have insufficient stock — build an error message
                StringBuilder errorMsg = new StringBuilder();
                for (Product p : insufficientProducts) {
                    errorMsg.append("\u2022 ").append(p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct = null;

                // TODO (later): remove insufficient items from trolley and show a notifier window.
                displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg;
                System.out.println("stock is not enough");
            }
        } else {
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
        }
        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given productId once, rather than repeatedly.
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                // Shallow copy + copy orderedQuantity so DB sees the intended amount
                Product copy = new Product(
                        p.getProductId(),
                        p.getProductDescription(),
                        p.getProductImageName(),
                        p.getUnitPrice(),
                        p.getStockQuantity()
                );
                copy.setOrderedQuantity(p.getOrderedQuantity());
                grouped.put(id, copy);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel() {
        trolley.clear();
        displayTaTrolley = "";
        updateView();
    }

    void closeReceipt() {
        displayTaReceipt = "";
    }

    void updateView() {
        if (theProduct != null) {
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder + imageName; // relative file path, eg images/0001.jpg
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); // get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging
        } else {
            imageName = "imageHolder.jpg";
        }
        cusView.update(imageName, displayLaSearchResult, displayTaTrolley, displayTaReceipt);
    }

    // for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }

    /**
     * Add a product to the trolley, merging with an existing line if the same productId
     * already exists. We keep exactly one Product per productId in the trolley, and we
     * track quantity via Product.orderedQuantity.
     */
    private void addOrMergeToTrolley(Product p) {
        final String id = p.getProductId();

        // If already in trolley, just bump its orderedQuantity
        for (Product line : trolley) {
            if (line.getProductId().equals(id)) {
                int newQty = Math.max(0, line.getOrderedQuantity()) + 1;
                line.setOrderedQuantity(newQty);
                return;
            }
        }

        // Otherwise add a fresh line for this productId with orderedQuantity = 1
        Product copy = new Product(
                p.getProductId(),
                p.getProductDescription(),
                p.getProductImageName(),
                p.getUnitPrice(),
                p.getStockQuantity()
        );
        copy.setOrderedQuantity(1);
        trolley.add(copy);
    }

    /** Sort the trolley by productId (IDs like "0003" sort correctly lexicographically). */
    private void sortTrolleyById() {
        trolley.sort(Comparator.comparing(Product::getProductId));
    }
}
