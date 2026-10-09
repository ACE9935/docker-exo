package com.ynov.crud.dog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dogs")
public class DogController {

    private final DogRepository dogRepository;

    public DogController(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @GetMapping
    public List<Dog> findAll() {
        return dogRepository.findAll();
    }

    @GetMapping("/{dogId}")
    public Dog findById(@PathVariable Long dogId) {
        return getDogOrThrow(dogId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dog create(@RequestBody Dog dog) {
        dog.setId(null);
        return dogRepository.save(dog);
    }

    @PutMapping("/{dogId}")
    public Dog update(@PathVariable Long dogId, @RequestBody Dog dog) {
        Dog existing = getDogOrThrow(dogId);
        existing.setName(dog.getName());
        existing.setBirthDate(dog.getBirthDate());
        existing.setBreed(dog.getBreed());
        existing.setSterilized(dog.isSterilized());
        return dogRepository.save(existing);
    }

    @DeleteMapping("/{dogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long dogId) {
        dogRepository.delete(getDogOrThrow(dogId));
    }

    private Dog getDogOrThrow(Long dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog " + dogId + " not found"));
    }
}
