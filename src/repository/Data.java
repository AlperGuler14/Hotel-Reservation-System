package repository;

import model.Customer;
import model.Room;

import java.io.*;
import java.util.Map;

public class Data {
    private static final String File_name = "room_data.txt";

    public static void saveData(java.util.List<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(File_name))) {
            for (Room r : rooms) {

                if (!r.isAvailable() && r.getGuest() != null) {
                    Customer c = r.getGuest();

                    String line = r.getRoomNumber() + "," + c.getFullName() + "," + c.getDays();
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    public static void loadData(Map<Integer, Room> roomMap) {
        File file = new File(File_name);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(File_name))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    int roomNo = Integer.parseInt(data[0]);
                    String name = data[1];
                    int days = Integer.parseInt(data[2]);

                    Room room = roomMap.get(roomNo);
                    if (room != null) {
                        room.checkIn(new Customer(name, days));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }
}