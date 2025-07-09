package com.alcohol.application.pet.service.PetRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.entity.PetAnniversary;

@Repository
public interface PetAnniversaryRepository extends JpaRepository<PetAnniversary, Long> {

    void deleteByPet(Pet pet);
 

}
