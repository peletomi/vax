package org.peletomi.vax.testobject;

import org.peletomi.vax.annotation.Value;
import org.peletomi.vax.annotation.ValueJavaAdapter;

public class CountryInfo {

    private CountryCode countryCode;

    @Value
    @ValueJavaAdapter(CountryCodeAdapter.class)
    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final CountryCode countryCode) {
        this.countryCode = countryCode;
    }

    @Value
    @ValueJavaAdapter({CountryCodeAdapter.class, CountryCodeTranslationAdapter.class})
    public CountryCode getCountryName() {
        return countryCode;
    }

}
