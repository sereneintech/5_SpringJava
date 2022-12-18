package com.bnta.chocolate.controllers;

import com.bnta.chocolate.models.Chocolate;
import com.bnta.chocolate.models.Estate;
import com.bnta.chocolate.repositories.EstateRepository;
import com.bnta.chocolate.services.EstateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("estates")
public class EstateController {

    @Autowired
    EstateService estateService;

//    INDEX
    @GetMapping
    public ResponseEntity<List<Estate>> getAllEstates(){
        return new ResponseEntity(estateService.findAllEstates(), HttpStatus.OK);
    }

//    SHOW
    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<Estate>> getEstate(@PathVariable Long id){
        return new ResponseEntity(estateService.findEstate(id), HttpStatus.OK);
    }

//    CREATE
    @PostMapping
    public ResponseEntity<Estate> postEstate(@RequestBody Estate estate){
        estateService.saveEstate(estate);
        return new ResponseEntity(estateService.findAllEstates(), HttpStatus.CREATED);
    }

//    DELETE
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Long> deleteEstate(@PathVariable Long id){
        estateService.removeEstatesFromChocolates(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

//    UPDATE
    @PutMapping(value = "/{id}")
    public ResponseEntity<Estate> updateEstate(@RequestBody Estate estate, @PathVariable Long id){
        estateService.updateEstate(estate, id);
        return new ResponseEntity<>(estate, HttpStatus.OK);
    }
}
