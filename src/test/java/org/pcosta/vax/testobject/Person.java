package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;

public class Person {

    private String firstName;

    private String lastName;

    @Value(required = false)
    private Integer age;

    @Value
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Value
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

}
