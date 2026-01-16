package ci553.happyshop.payment;

public class ExternalPaymentAdapter implements PaymentService {

    private final ExternalPaymentSystems externalSystem;

    public ExternalPaymentAdapter() {
        this.externalSystem = new ExternalPaymentSystems();
    }

    @Override
    public void pay(double total, PaymentMethod method) throws PaymentException {

        if (total <= 0) {
            throw new PaymentException("Payment failed: total must be greater than £0.");
        }

        // keep your rule if you want it here too (optional)
        if (total < 5.0 && method == PaymentMethod.CARD) {
            throw new PaymentException("Payment failed: orders under £5 must be paid by CASH.");
        }

        if (method == PaymentMethod.CARD) {
            boolean success = externalSystem.makePayment(total);
            if (!success) {
                throw new PaymentException("External payment failed.");
            }
        } else {
            System.out.println("Cash payment accepted.");
        }
    }
}
