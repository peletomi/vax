package org.pcosta.vax.testobject;

import org.pcosta.vax.ValueAdapter;

public class CountryCodeAdapter extends ValueAdapter<String, CountryCode> {

    @Override
    public CountryCode unmarshal(final String code) throws Exception {
        CountryCode result = null;
        if (code != null) {
            result = CountryCode.valueOf(code.toUpperCase());
        }
        return result;
    }

    @Override
    public String marshal(final CountryCode code) throws Exception {
        String result = null;
        if (code != null) {
            result = code.name().toLowerCase();
        }
        return result;
    }

}
