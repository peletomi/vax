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
package org.pcosta.vax;

import java.util.Map;

import org.pcosta.vax.impl.StringArrayExtractorFrontEndFactory;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractor extends
        AbstractValueExtractor<Map<String, String[]>, FrontEndFactory<Map<String, String[]>>> {

    public StringArrayExtractor() {
        super(new StringArrayExtractorFrontEndFactory());
    }

}
