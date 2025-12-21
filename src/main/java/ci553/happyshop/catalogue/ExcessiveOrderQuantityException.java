package ci553.happyshop.catalogue;

/**
 * Thrown when a product in the trolley has a quantity greater than the allowed maximum.
 */
public class ExcessiveOrderQuantityException extends Exception {

    private final String productId;
    private final String description;
    private final int requested;
    private final int maxAllowed;

    public ExcessiveOrderQuantityException(String productId,
                                           String description,
                                           int requested,
                                           int maxAllowed) {
        super(String.format(
                "Product %s (%s) has quantity %d, which is more than the maximum allowed (%d).",
                productId, description, requested, maxAllowed));
        this.productId = productId;
        this.description = description;
        this.requested = requested;
        this.maxAllowed = maxAllowed;
    }

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public int getRequested() {
        return requested;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}
