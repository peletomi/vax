package org.pcosta.vax.testobject;

import java.util.Map;

import org.pcosta.vax.ValueAdapter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CountryCodeTranslationAdapter extends ValueAdapter<String, String> {

    private static final BiMap<String, String> TRANSLATIONS = HashBiMap.create(5);

    private BiMap<String, String> translations;

    static {
        TRANSLATIONS.put("hu", "Magyarország");
        TRANSLATIONS.put("de", "Deutschland");
    }

    @Override
    public String unmarshal(final String name) throws Exception {
        String result = null;
        if (name != null) {
            result = getTranslations().inverse().get(name);
        }
        return result;
    }

    @Override
    public String marshal(final String code) throws Exception {
        String result = null;
        if (code != null) {
            result = getTranslations().get(code);
        }
        return result;
    }

    public void setTranslations(final Map<String, String> translations2) {
        translations = HashBiMap.create(translations2);
    }

    private BiMap<String, String> getTranslations() {
        if (translations == null || translations.isEmpty()) {
            return TRANSLATIONS;
        }
        return translations;
    }

}
