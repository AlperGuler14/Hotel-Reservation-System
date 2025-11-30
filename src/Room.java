public abstract class Room implements IBookable {
    protected int roomNumber;
    protected double price;
    protected boolean isOccupied;
    protected String typeName;

    protected Customer guest;

    public Room(int roomNumber, double price, String typeName) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.typeName = typeName;
        this.isOccupied = false;
        this.guest = null;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getTypeName() { return typeName; }

    @Override
    public boolean isAvailable() {
        return !isOccupied;
    }

    @Override
    public void checkIn(Customer customer) {
        this.isOccupied = true;
        this.guest = customer;
    }

    @Override
    public void checkOut() {
        this.isOccupied = false;
        this.guest = null;
    }

    @Override
    public String toString() {
        String statusStr = isOccupied ? "OCCUPIED (" + guest.getFullName() + ")" : "VACANT";
        return String.format("Room %d [%s] - Status: %s - Price: %.2f TL",
                roomNumber, typeName, statusStr, price);
    }

    public Customer getGuest() {
        return this.guest;
    }
}