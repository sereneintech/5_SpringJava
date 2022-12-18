package com.example.airline_api.models;

public class BookingDTO {

    private long passengerId;

    public BookingDTO(long passengerId) {
        this.passengerId = passengerId;
    }

    public BookingDTO() {
    }

    public long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(long passengerId) {
        this.passengerId = passengerId;
    }
}
