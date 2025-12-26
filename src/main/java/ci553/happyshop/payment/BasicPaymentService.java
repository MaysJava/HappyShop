package ci553.happyshop.payment;

public class BasicPaymentService implements PaymentService {

    @Override
    public void pay(double total, PaymentMethod method) throws PaymentException {

        if (total <= 0) {
            throw new PaymentException("Payment failed: total must be greater than £0.");
        }

        // Business rule: under £5 -> cash only
        if (total < 5.0 && method == PaymentMethod.CARD) {
            throw new PaymentException("Payment failed: orders under £5 must be paid by CASH.");
        }

        // Otherwise accepted (simulation)
    }
}
