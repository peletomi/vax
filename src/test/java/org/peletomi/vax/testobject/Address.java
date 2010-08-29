package org.peletomi.vax.testobject;

import org.peletomi.vax.annotation.Value;
import org.peletomi.vax.annotation.ValueJavaAdapter;

public class Address {

    private String street;

    private String city;

    private int zip;

    private CountryCode country;

    @Value(name = "streetWithNumber")
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @Value
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @Value
    public int getZip() {
        return zip;
    }

    public void setZip(final int zip) {
        this.zip = zip;
    }

    @Value(name = "countryCode")
    @ValueJavaAdapter(CountryCodeAdapter.class)
    public CountryCode getCountry() {
        return country;
    }

    public void setCountry(final CountryCode country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + zip;
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
        final Address other = (Address) obj;
        if (city == null) {
            if (other.city != null) {
                return false;
            }
        } else if (!city.equals(other.city)) {
            return false;
        }
        if (country != other.country) {
            return false;
        }
        if (street == null) {
            if (other.street != null) {
                return false;
            }
        } else if (!street.equals(other.street)) {
            return false;
        }
        if (zip != other.zip) {
            return false;
        }
        return true;
    }
}
