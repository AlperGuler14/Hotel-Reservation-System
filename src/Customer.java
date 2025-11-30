public class Customer {
    private String fullName;
    private int days;

    public Customer(String fullName, int days) {
        this.fullName = fullName;
        this.days = days;
    }

    public String getFullName() {
        return fullName;
    }

    public int getDays() {
        return days;
    }

    @Override
    public String toString() {
        return fullName + " (" + days + " Days)";
    }
}