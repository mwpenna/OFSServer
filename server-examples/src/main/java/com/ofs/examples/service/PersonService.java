package com.ofs.examples.service;

import com.ofs.examples.model.Person;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonService {

    private List<Person> personArrayList = new ArrayList<> ();

    public Optional<Person> getPersonById(String id) {
        for (Person person : personArrayList) {
            if(person.getId().toString().equals(id)) {
                return Optional.of(person);
            }
        }

        return Optional.empty();
    }

    public void createPerson(Person person) {
        personArrayList.add(person);
    }

    public void updatePerson(Person person) {
        List<Person> personList = personArrayList.stream().filter(
                p -> !p.getId().equals(person.getId())).collect(Collectors.toList()
        );

        personList.add(person);
        personArrayList = personList;
    }
}
