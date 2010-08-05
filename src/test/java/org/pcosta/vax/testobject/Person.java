package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;

public class Person {

    private String firstName;

    private String lastName;

    @Value(required = false)
    private Integer age;

    @Value
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Value
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.age == null) ? 0 : this.age.hashCode());
        result = prime * result + ((this.firstName == null) ? 0 : this.firstName.hashCode());
        result = prime * result + ((this.lastName == null) ? 0 : this.lastName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.age == null) {
            if (other.age != null) {
                return false;
            }
        } else if (!this.age.equals(other.age)) {
            return false;
        }
        if (this.firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!this.firstName.equals(other.firstName)) {
            return false;
        }
        if (this.lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!this.lastName.equals(other.lastName)) {
            return false;
        }
        return true;
    }

}
