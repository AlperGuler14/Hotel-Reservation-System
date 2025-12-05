package service;

import model.Customer;

public interface IBookable {
    void checkIn(Customer customer);
    void checkOut();
    boolean isAvailable();
}