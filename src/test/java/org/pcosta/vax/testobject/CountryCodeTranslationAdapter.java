package org.pcosta.vax.testobject;

import org.pcosta.vax.ValueAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CountryCodeTranslationAdapter extends ValueAdapter<String, String> {

    private static final BiMap<String, String> TRANSLATIONS = HashBiMap.create(5);

    static {
        TRANSLATIONS.put("hu", "Magyarország");
        TRANSLATIONS.put("de", "Deutschland");
    }

    @Override
    public String unmarshal(final String name) throws Exception {
        String result = null;
        if (name != null) {
            result = TRANSLATIONS.inverse().get(name);
        }
        return result;
    }

    @Override
    public String marshal(final String code) throws Exception {
        String result = null;
        if (code != null) {
            result = TRANSLATIONS.get(code);
        }
        return result;
    }

}
