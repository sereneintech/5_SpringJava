package com.example.airline_api.controllers;

import com.example.airline_api.models.Passenger;
import com.example.airline_api.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    @Autowired
    PassengerService passengerService;

    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers(){
        List<Passenger> passengers = passengerService.getAllPassengers();
        return new ResponseEntity<>(passengers, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable long id){
        Passenger passenger = passengerService.getPassengerById(id);
        return new ResponseEntity<>(passenger, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Passenger> addNewPassenger(@RequestBody Passenger passenger){
        Passenger savedPassenger = passengerService.addNewPassenger(passenger);
        return new ResponseEntity<>(savedPassenger, HttpStatus.CREATED);
    }

}
