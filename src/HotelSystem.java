import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HotelSystem extends JFrame {

    private Map<Integer, Room> roomMap;
    private HotelRepository<Room> roomRepo;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName;
    private JTextField txtDays;
    private JComboBox<String> cmbType;

    // Rapor Etiketleri
    private JLabel lblTotalRooms;
    private JLabel lblOccupiedRooms;
    private JLabel lblTotalIncome;

    public HotelSystem() {
        roomRepo = new HotelRepository<>();
        roomMap = new HashMap<>();

        loadHotelData();
        createUI();
    }

    private void loadHotelData() {
        Room[] rooms = {
                new StandardRoom(101), new StandardRoom(102), new StandardRoom(103),
                new SuiteRoom(201), new SuiteRoom(202),
                new KingRoom(301), new KingRoom(302)
        };

        for (Room r : rooms) {
            roomRepo.add(r);
            roomMap.put(r.getRoomNumber(), r);
        }
        
        Data.loadData(roomMap);
    }

    private void createUI() {
        setTitle("Hotel Reservation System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. ÜST PANEL (HEADER) ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(50, 50, 80));
        JLabel lblHeader = new JLabel("HOTEL MANAGEMENT SYSTEM");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        pnlHeader.add(lblHeader);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. SOL PANEL (GİRİŞ İŞLEMLERİ) ---
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new GridLayout(10, 1, 5, 5));
        pnlLeft.setBorder(new TitledBorder("Operations"));
        pnlLeft.setPreferredSize(new Dimension(250, 0));

        pnlLeft.add(new JLabel("Customer Name:"));
        txtName = new JTextField();
        pnlLeft.add(txtName);

        pnlLeft.add(new JLabel("Days:"));
        txtDays = new JTextField();
        pnlLeft.add(txtDays);

        pnlLeft.add(new JLabel("Room Type:"));
        String[] types = {"All", "Standard", "Suite", "King"};
        cmbType = new JComboBox<>(types);
        pnlLeft.add(cmbType);

        JButton btnBook = new JButton("Book Room");
        JButton btnCheckout = new JButton("Checkout Selected");
        JButton btnRefresh = new JButton("Refresh List");

        pnlLeft.add(btnBook);
        pnlLeft.add(new JLabel("")); // Boşluk
        pnlLeft.add(btnCheckout);
        pnlLeft.add(btnRefresh);

        add(pnlLeft, BorderLayout.WEST);

        // --- 3. ORTA PANEL (TABLO) ---
        String[] columns = {"Room No", "Type", "Status", "Customer", "Days", "Total (TL)"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- 4. ALT PANEL (RAPOR / FOOTER) ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlBottom.setBorder(new TitledBorder("Instant Report"));

        // Etiketleri oluştur
        lblTotalRooms = new JLabel("Total Rooms: 0");
        lblOccupiedRooms = new JLabel("Occupied: 0");
        lblTotalIncome = new JLabel("Current Income: 0.0 TL");

        // Font ayarları
        Font fontStats = new Font("Arial", Font.BOLD, 14);
        lblTotalRooms.setFont(fontStats);
        lblOccupiedRooms.setFont(fontStats);
        lblTotalIncome.setFont(fontStats);
        lblTotalIncome.setForeground(new Color(0, 100, 0)); // Gelir yeşil renk

        // Panele ekle
        pnlBottom.add(lblTotalRooms);
        pnlBottom.add(lblOccupiedRooms);
        pnlBottom.add(lblTotalIncome);

        // Ana ekrana ekle
        add(pnlBottom, BorderLayout.SOUTH);

        // --- BUTON OLAYLARI ---
        btnBook.addActionListener(e -> makeReservation());
        btnCheckout.addActionListener(e -> performCheckout());
        btnRefresh.addActionListener(e -> updateTable());

        updateTable(); // İlk açılışta tabloyu ve raporu doldur
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Tabloyu temizle

        for (Room r : roomRepo.getAll()) {
            String status = r.isAvailable() ? "VACANT" : "OCCUPIED";
            String customerName = "-";
            String days = "-";
            String price = r.price + " TL (Daily)";

            if (!r.isAvailable() && r.getGuest() != null) {
                Customer c = r.getGuest();
                customerName = c.getFullName();
                days = String.valueOf(c.getDays());
                double totalPrice = c.getDays() * r.price;
                price = totalPrice + " TL";
            }

            Object[] row = {
                    r.getRoomNumber(),
                    r.getTypeName(),
                    status,
                    customerName,
                    days,
                    price
            };
            tableModel.addRow(row);
        }

        // Tablo dolduktan sonra raporu güncelle
        updateReport();
    }

    private void updateReport() {
        int total = roomRepo.getAll().size();
        int occupied = 0;
        double income = 0.0;

        for (Room r : roomRepo.getAll()) {
            if (!r.isAvailable()) { // Oda doluysa
                occupied++;
                // Gelir hesapla
                if (r.getGuest() != null) {
                    income += r.price * r.getGuest().getDays();
                }
            }
        }

        // Etiketleri güncelle
        lblTotalRooms.setText("Total Rooms: " + total);
        lblOccupiedRooms.setText("Occupied: " + occupied);
        lblTotalIncome.setText("Current Income: " + income + " TL");
    }

    private void makeReservation() {
        String name = txtName.getText();
        String daysStr = txtDays.getText();
        String selectedType = (String) cmbType.getSelectedItem();

        if (name.isEmpty() || daysStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name and days!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int days = Integer.parseInt(daysStr);

            Room availableRoom = roomRepo.getAll().stream()
                    .filter(r -> selectedType.equals("All") || r.getTypeName().equals(selectedType))
                    .filter(r -> r.isAvailable())
                    .findFirst()
                    .orElse(null);

            if (availableRoom != null) {
                Customer customer = new Customer(name, days);
                availableRoom.checkIn(customer);

                txtName.setText("");
                txtDays.setText("");

                // Tabloyu ve raporu güncelle, veriyi kaydet
                updateTable();
                Data.saveData(roomRepo.getAll());

                JOptionPane.showMessageDialog(this, "Room booked: " + availableRoom.getRoomNumber());
            } else {
                JOptionPane.showMessageDialog(this, "No empty room found for selected criteria!", "Sorry", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter only numbers for Days!");
        }
    }

    private void performCheckout() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room from the table.");
            return;
        }

        int roomNo = (int) tableModel.getValueAt(selectedRow, 0);
        Room r = roomMap.get(roomNo);

        if (r.isAvailable()) {
            JOptionPane.showMessageDialog(this, "This room is already vacant.");
        } else {
            r.checkOut();

            // Tabloyu ve raporu güncelle, veriyi kaydet
            updateTable();
            Data.saveData(roomRepo.getAll());

            JOptionPane.showMessageDialog(this, "Checkout successful.");
        }
    }

    public static void main(String[] args) {
        new HotelSystem().setVisible(true);
    }
}