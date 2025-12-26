package ci553.happyshop.payment;

public interface PaymentService {
    void pay(double total, PaymentMethod method) throws PaymentException;
}
