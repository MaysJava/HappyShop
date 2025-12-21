package ci553.happyshop.catalogue;


public class UnderMinimumPaymentException extends Exception {

    private final double total;
    private final double minimum;

    public UnderMinimumPaymentException(double total, double minimum) {
        super(String.format(
                "Minimum payment is £%.2f, but your trolley total is only £%.2f.",
                minimum, total
        ));
        this.total = total;
        this.minimum = minimum;
    }

    public double getTotal() {
        return total;
    }

    public double getMinimum() {
        return minimum;
    }
}

