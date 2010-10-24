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
package org.peletomi.vax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
@Target(value = {ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Value {

    /**
     * Returns the name of the field.
     */
    String name() default "";

    /**
     * Is this a required field?
     */
    boolean required() default true;

    /**
     * If this property is true, then the values from the attribute object are
     * extracted as well.
     */
    boolean recurse() default false;

    /**
     * If true and the value is a collection, then the items of the collection are extracted.
     */
    boolean collection() default false;

    /**
     * Class of the collection items.
     */
    Class<?> collectionClass() default DefaultClass.class;

}
