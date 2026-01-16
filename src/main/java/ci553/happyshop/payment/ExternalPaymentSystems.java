package ci553.happyshop.payment;

public class ExternalPaymentSystems {
    public boolean makePayment(double amountInPounds) {
        System.out.println("External system paid £" + amountInPounds);
        return true;
    }
}
