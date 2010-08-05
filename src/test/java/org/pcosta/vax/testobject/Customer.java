package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;

public class Customer {

    private Person person;

    private Address address;

    @Value(recurse = true)
    public Person getPerson() {
        return this.person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    @Value(recurse = true)
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.address == null) ? 0 : this.address.hashCode());
        result = prime * result + ((this.person == null) ? 0 : this.person.hashCode());
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
        final Customer other = (Customer) obj;
        if (this.address == null) {
            if (other.address != null) {
                return false;
            }
        } else if (!this.address.equals(other.address)) {
            return false;
        }
        if (this.person == null) {
            if (other.person != null) {
                return false;
            }
        } else if (!this.person.equals(other.person)) {
            return false;
        }
        return true;
    }
}
