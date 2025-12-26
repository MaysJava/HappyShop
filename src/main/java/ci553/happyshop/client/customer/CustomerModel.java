package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;
import ci553.happyshop.catalogue.UnderMinimumPaymentException;
import ci553.happyshop.catalogue.ExcessiveOrderQuantityException;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

import ci553.happyshop.payment.BasicPaymentService;
import ci553.happyshop.payment.PaymentException;
import ci553.happyshop.payment.PaymentMethod;
import ci553.happyshop.payment.PaymentService;

import javafx.scene.control.ChoiceDialog;

import java.util.Optional;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
public class CustomerModel {
    public CustomerView cusView;
    public DatabaseRW databaseRW; // Interface type, not specific implementation

    private final PaymentService paymentService = new BasicPaymentService();

    private Product theProduct = null; // product found from search
    private ArrayList<Product> trolley = new ArrayList<>(); // a list of products in trolley
    private RemoveProductNotifier removeNotifier;

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                        // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet";// Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    // SELECT productID, description, image, unitPrice, inStock quantity
    void search() throws SQLException {
        String productId = cusView.tfId.getText().trim();
        String productName = cusView.tfName.getText().trim();

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
        }  else if (!productName.isEmpty()) {

        ArrayList<Product> results = databaseRW.searchProduct(productName);

        if (results != null && !results.isEmpty()) {
            theProduct = results.get(0); // show the first match for now

            double unitPrice = theProduct.getUnitPrice();
            String description = theProduct.getProductDescription();
            int stock = theProduct.getStockQuantity();

            String baseInfo = String.format(
                    "Showing 1 of %d match(es) for \"%s\"\nProduct_Id: %s\n%s,\nPrice: £%.2f",
                    results.size(),
                    productName,
                    theProduct.getProductId(),
                    description,
                    unitPrice
            );
            String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
            displayLaSearchResult = baseInfo + quantityInfo;

        } else {
            theProduct = null;
            displayLaSearchResult = "No Product was found for keyword: " + productName;
        }

    } else {
        theProduct = null;
        displayLaSearchResult = "Please type ProductID or a Name keyword";
        System.out.println("Please type ProductID or a Name keyword.");
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
        if (trolley.isEmpty()) {
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
            updateView();
            return;
        }
        // 🧪 First: validate payment and quantities using our new business rules
        try {
            validateTrolley(trolley);
        } catch (UnderMinimumPaymentException | ExcessiveOrderQuantityException e) {
            // Handle our own business rule failures here

            // For now we keep it simple: show the message in the UI and console
            displayLaSearchResult = e.getMessage();
            System.out.println(e.getMessage());

            // We don't proceed with checkout if validation fails
            displayTaReceipt = "";
            updateView();
            return;
        }

        double total = calculateTrolleyTotal();
        PaymentMethod method = askPaymentMethod(total);

        if (method == null) {
            // user cancelled payment dialog
            displayLaSearchResult = "Payment cancelled. Checkout aborted.";
            updateView();
            return;
        }

        try {
            paymentService.pay(total, method);
        } catch (PaymentException e) {
            displayLaSearchResult = e.getMessage();
            displayTaReceipt = "";
            updateView();
            return;
        }


        // 👉 If we reach here, the trolley passed the new business rules.
        // Existing stock-check and order-creation logic stays the same:

        // Group the products in the trolley by productId to optimise stock checking
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
        } else {
            // Your existing "stock not enough" code here (we already wrote it earlier)
            StringBuilder errorMsg = new StringBuilder();
            for (Product p : insufficientProducts) {
                errorMsg.append("\u2022 ").append(p.getProductId()).append(", ")
                        .append(p.getProductDescription()).append(" (Only ")
                        .append(p.getStockQuantity()).append(" available, ")
                        .append(p.getOrderedQuantity()).append(" requested)\n");
            }
            theProduct = null;

            displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg;
            System.out.println("stock is not enough");
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

    private String formatProductInfo(Product p) {
        double unitPrice = p.getUnitPrice();
        String description = p.getProductDescription();
        int stock = p.getStockQuantity();

        String baseInfo = String.format(
                "Product_Id: %s\n%s,\nPrice: £%.2f",
                p.getProductId(),
                description,
                unitPrice
        );

        String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
        return baseInfo + quantityInfo;
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

    /**
     * Sort the trolley by productId (IDs like "0003" sort correctly lexicographically).
     */
    private void sortTrolleyById() {
        trolley.sort(Comparator.comparing(Product::getProductId));
    }

    // To remove from trolley any product whose id appears in the insufficient list
    private void removeInsufficientFromTrolley(ArrayList<Product> insufficient) {
        java.util.HashSet<String> badIds = new java.util.HashSet<>();
        for (Product p : insufficient) badIds.add(p.getProductId());
        trolley.removeIf(p -> badIds.contains(p.getProductId()));
    }

    // To build a friendly message for the user
    private String buildInsufficientMessage(ArrayList<Product> insufficient) {
        StringBuilder sb = new StringBuilder("Checkout failed. Not enough stock for:\n");
        for (Product p : insufficient) {
            sb.append("• ")
                    .append(p.getProductId()).append(" – ").append(p.getProductDescription())
                    .append(" (available ").append(p.getStockQuantity())
                    .append(", requested ").append(p.getOrderedQuantity()).append(")\n");
        }
        return sb.toString();
    }


    // Minimum payment rule and maximum quantity rule
    private static final double MIN_TOTAL_PAYMENT = 5.0;
    private static final int MAX_ALLOWED_QUANTITY = 50;

    /**
     * Checks business rules on the trolley:
     * - total payment must be at least MIN_TOTAL_PAYMENT
     * - each product's quantity must not exceed MAX_ALLOWED_QUANTITY
     *
     * If a rule is broken, a custom exception is thrown.
     */
    private void validateTrolley(ArrayList<Product> trolley)
            throws UnderMinimumPaymentException, ExcessiveOrderQuantityException {

        double total = 0.0;

        for (Product p : trolley) {
            int qty = Math.max(0, p.getOrderedQuantity()); // safeguard

            // Rule 1: quantity per product must not exceed 50
            if (qty > MAX_ALLOWED_QUANTITY) {
                throw new ExcessiveOrderQuantityException(
                        p.getProductId(),
                        p.getProductDescription(),
                        qty,
                        MAX_ALLOWED_QUANTITY
                );
            }

            // Add to total
            total += p.getUnitPrice() * qty;
        }

        // Rule 2: total trolley value must be at least £5
        if (total < MIN_TOTAL_PAYMENT) {
            throw new UnderMinimumPaymentException(total, MIN_TOTAL_PAYMENT);
        }
    }

    private double calculateTrolleyTotal() {
        double total = 0.0;

        for (Product p : trolley) {
            int qty = p.getOrderedQuantity();
            if (qty <= 0) qty = 1; // safety: some versions store items as repeated lines
            total += p.getUnitPrice() * qty;
        }

        return total;
    }

    private PaymentMethod askPaymentMethod(double total) {
        ChoiceDialog<PaymentMethod> dialog =
                new ChoiceDialog<>(PaymentMethod.CARD, PaymentMethod.CASH, PaymentMethod.CARD);

        dialog.setTitle("Payment");
        dialog.setHeaderText(String.format("Your total is £%.2f", total));
        dialog.setContentText("Choose payment method:");

        Optional<PaymentMethod> result = dialog.showAndWait();
        return result.orElse(null); // null means user cancelled
    }


}
