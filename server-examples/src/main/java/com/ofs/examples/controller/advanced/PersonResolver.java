package com.ofs.examples.controller.advanced;

import com.ofs.examples.model.Person;
import com.ofs.server.filter.Filter;
import com.ofs.server.filter.views.Public;
import com.ofs.server.filter.views.SystemAdmin;
import com.ofs.server.security.Subject;
import org.springframework.stereotype.Component;

@Component(value = "PersonResolver")
public class PersonResolver implements Filter<Person> {

    @Override
    public Class<? extends Public> filterView(Person person, Subject subject) {
        if(person.getName().equalsIgnoreCase("Matt")) {
            return SystemAdmin.class;
        }
        else {
            return Public.class;
        }
    }
}
