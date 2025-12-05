package repository;

import java.util.ArrayList;
import java.util.List;

public class HotelRepository<T> {
    private final List<T> items;

    public HotelRepository() {
        this.items = new ArrayList<>();
    }

    public void add(T item) {
        items.add(item);
    }

    public List<T> getAll() {
        return items;
    }

    public <E> void printGenericInfo(E info) {
        System.out.println("Log: " + info.toString());
    }
}