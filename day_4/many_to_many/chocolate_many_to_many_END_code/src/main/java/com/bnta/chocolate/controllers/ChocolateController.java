package com.bnta.chocolate.controllers;

import com.bnta.chocolate.models.Chocolate;
import com.bnta.chocolate.repositories.ChocolateRepository;
import com.bnta.chocolate.services.ChocolateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("chocolates")
public class ChocolateController {

    @Autowired
    ChocolateService chocolateService;

//    Handles following:
//    * GET /chocolates
//    * GET /chocolates?cocoaPercentage=69
//    INDEX
    @GetMapping
    public ResponseEntity<List<Chocolate>> getAllChocolatesAndFilters(
            @RequestParam(required = false, name = "cocoaPercentage") Integer cocoaPercentage
    ){
//        GET /chocolates?cocoaPercentage=69
        if(cocoaPercentage != null){
            return new ResponseEntity<>(chocolateService.findAllChocolatesOverCocoaPercentage(cocoaPercentage), HttpStatus.OK);
        }
//        GET /chocolates
        return new ResponseEntity<>(chocolateService.findAllChocolates(), HttpStatus.OK);
    }

//    SHOW
    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<Chocolate>> getChocolate(@PathVariable Long id){
        Chocolate foundChocolate = chocolateService.findChocolate(id);
        return new ResponseEntity(foundChocolate, HttpStatus.OK);
    }

//    CREATE (chocolate can be created with or without estates)
    @PostMapping
    public ResponseEntity<List<Chocolate>> postChocolate(@RequestBody Chocolate chocolate){
        chocolateService.saveChocolate(chocolate);
        return new ResponseEntity<>(chocolateService.findAllChocolates(), HttpStatus.CREATED);
    }

//    UPDATE
    @PutMapping(value = "/{id}")
    public ResponseEntity<Chocolate> updateChocolate(@RequestBody Chocolate chocolate, @PathVariable Long id){
        chocolateService.updateChocolate(chocolate, id);
        return new ResponseEntity<>(chocolate, HttpStatus.OK);
    }

//    DELETE
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Long> deleteChocolate(@PathVariable Long id){
        chocolateService.deleteChocolate(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
