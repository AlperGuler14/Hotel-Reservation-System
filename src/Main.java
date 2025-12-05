import ui.HotelSystem;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            HotelSystem app = new HotelSystem();
            app.setVisible(true);
        });
    }
}