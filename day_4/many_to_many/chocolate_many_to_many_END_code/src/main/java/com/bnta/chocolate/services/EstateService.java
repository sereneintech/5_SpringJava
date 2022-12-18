package com.bnta.chocolate.services;

import com.bnta.chocolate.models.Chocolate;
import com.bnta.chocolate.models.Estate;
import com.bnta.chocolate.repositories.EstateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstateService {

    @Autowired
    EstateRepository estateRepository;

    public List<Estate> findAllEstates(){
        return estateRepository.findAll();
    }

    public Estate findEstate(Long id){
        return estateRepository.findById(id).get();
    }

    public void saveEstate(Estate estate){
        estateRepository.save(estate);
    }

    public void removeEstatesFromChocolates(Long id){
        Estate foundEstate = estateRepository.getById(id);
        for (Chocolate chocolate : foundEstate.getChocolates()) {
            chocolate.removeEstate(foundEstate);
        }
    }

    public void deleteEstate(Long id){
        estateRepository.deleteById(id);
    }

    public void updateEstate(Estate estate, Long id){
        Estate estateToUpdate = estateRepository.findById(id).get();
        estateToUpdate.setName(estate.getName());
        estateToUpdate.setCountry(estate.getCountry());
        estateToUpdate.setChocolates(estate.getChocolates());
        estateRepository.save(estateToUpdate);
    }

}
