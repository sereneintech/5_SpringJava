package com.bnta.chocolate.services;

import com.bnta.chocolate.models.Chocolate;
import com.bnta.chocolate.repositories.ChocolateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChocolateService {

    @Autowired
    ChocolateRepository chocolateRepository;

    public void updateChocolate(Chocolate chocolate, Long id){
        Chocolate chocolateToUpdate = chocolateRepository.findById(id).get();
        chocolateToUpdate.setName(chocolate.getName());
        chocolateToUpdate.setCocoaPercentage(chocolate.getCocoaPercentage());
        chocolateToUpdate.setEstates(chocolate.getEstates());
        chocolateRepository.save(chocolateToUpdate);
    }

    public void saveChocolate(Chocolate chocolate){
        chocolateRepository.save(chocolate);
    }

    public Chocolate findChocolate(Long id){
       return chocolateRepository.findById(id).get();
    }

    public List<Chocolate> findAllChocolates(){
        return chocolateRepository.findAll();
    }

    public List<Chocolate> findAllChocolatesOverCocoaPercentage(int percentage){
        return chocolateRepository.findByCocoaPercentageGreaterThan(percentage);
    }

    public void deleteChocolate(Long id){
        chocolateRepository.deleteById(id);
    }


}
