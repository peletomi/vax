package org.pcosta.vax.testobject;

import java.util.List;

import org.pcosta.vax.annotation.Value;

public class GuestList {

    private String[] vipNames;

    private List<String> guests;

    @Value(name = "vipName", collection = true)
    public String[] getVipNames() {
        return vipNames;
    }

    public void setVipNames(final String[] vipNames) {
        this.vipNames = vipNames;
    }

    @Value(name = "guest", collection = true)
    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(final List<String> guests) {
        this.guests = guests;
    }
}
