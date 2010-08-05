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
package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class Employee extends Person {

    private Person boss;

    @Value
    private int salary;

    @Value
    public Person getBoss() {
        return this.boss;
    }

    public void setBoss(final Person boss) {
        this.boss = boss;
    }

    public int getSalary() {
        return this.salary;
    }

    public void setSalary(final int salary) {
        this.salary = salary;
    }
}
