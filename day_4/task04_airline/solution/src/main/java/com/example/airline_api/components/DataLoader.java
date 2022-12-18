package com.example.airline_api.components;

import com.example.airline_api.models.Flight;
import com.example.airline_api.models.Passenger;
import com.example.airline_api.services.FlightService;
import com.example.airline_api.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    FlightService flightService;

    @Autowired
    PassengerService passengerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Flight flight1 = new Flight("LCY", 2, "2022-09-01", "15:00:00");
        flightService.addNewFlight(flight1);

        Flight flight2 = new Flight("EDI", 50, "2023-01-01", "07:00:00");
        flightService.addNewFlight(flight2);

        Passenger colin = new Passenger("Colin", "colin.farquhar@brightnetwork.co.uk");
        passengerService.addNewPassenger(colin);

        Passenger anna = new Passenger("Anna", "anna.henderson@brightnetwork.co.uk");
        passengerService.addNewPassenger(anna);

    }
}
