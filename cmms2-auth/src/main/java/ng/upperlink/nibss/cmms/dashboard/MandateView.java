package ng.upperlink.nibss.cmms.dashboard;

public class MandateView {
    public interface Summary{}
    public interface BankDetail extends Summary{}
    public interface SubscriberDetail extends Summary{}
    public interface BillerDetail extends BankDetail{}

}
