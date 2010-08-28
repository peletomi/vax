package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;
import org.pcosta.vax.annotation.ValueJavaAdapter;

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
