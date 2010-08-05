/**
 * Licensed under MPL / LGPL dual-license.
 *
 * Copyright (c) 2010 Tamas Eppel <Tamas.Eppel@gmail.com>
 *
 * You should have received a copy of the licenses
 * along with this program.
 * If not, see:
 *
 *    <http://www.gnu.org/licenses/>
 *    <http://www.mozilla.org/MPL/MPL-1.1.html>
 */
package org.pcosta.vax.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pcosta.vax.ExceptionHandler;
import org.pcosta.vax.ExtractorFrontEnd;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorFrontEnd implements ExtractorFrontEnd<Map<String, String[]>> {

    private Map<String[], String[]> values = Collections.emptyMap();

    private String keySeparator = ".";

    private boolean skipBlanks = true;

    @Override
    public void init() {
        this.values = new HashMap<String[], String[]>();
    }

    @Override
    public void addValue(final String[] key, final Object value) {
        List<String> valueList = new ArrayList<String>();
        if (value == null) {
            valueList.add(null);
        } else if (value.getClass().isArray()) {
            final Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
                valueList.add(values[i] == null ? null : values[i].toString());
            }
        } else if (value instanceof Collection) {
            @SuppressWarnings("rawtypes")
            final Collection values = (Collection) value;
            int i = 0;
            for (final Object v : values) {
                valueList.add(v == null ? null : v.toString());
                ++i;
            }
        } else {
            valueList.add(value.toString());
        }
        if (this.skipBlanks) {
            final List<String> filtered = new ArrayList<String>();
            for (final String string : valueList) {
                if (!this.isBlank(string)) {
                    filtered.add(string);
                }
            }
            valueList = filtered;
            if (!valueList.isEmpty()) {
            }
        }
        if ((this.skipBlanks && !valueList.isEmpty()) || !this.skipBlanks) {
            this.values.put(key, valueList.toArray(new String[valueList.size()]));
        }
    }

    @Override
    public boolean contains(final String[] key) {
        return this.values.containsKey(Util.join(key, this.keySeparator));
    }

    @Override
    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
    }

    @Override
    public Map<String, String[]> getExtracted() {
        final Map<String, String[]> result = new HashMap<String, String[]>(this.values.size());
        for (final Entry<String[], String[]> entry : this.values.entrySet()) {
            result.put(Util.join(entry.getKey(), this.keySeparator), entry.getValue());
        }
        return result;
    }

    public String getKeySeparator() {
        return this.keySeparator;
    }

    public void setKeySeparator(final String keySeparator) {
        this.keySeparator = keySeparator;
    }

    public boolean isSkipBlanks() {
        return this.skipBlanks;
    }

    public void setSkipBlanks(final boolean skipBlanks) {
        this.skipBlanks = skipBlanks;
    }

    protected boolean isBlank(final String value) {
        return value == null || "".equals(value);
    }
}
