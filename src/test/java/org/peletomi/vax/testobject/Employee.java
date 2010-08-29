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
package org.peletomi.vax.testobject;

import org.peletomi.vax.annotation.Value;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.boss == null) ? 0 : this.boss.hashCode());
        result = prime * result + this.salary;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Employee other = (Employee) obj;
        if (this.boss == null) {
            if (other.boss != null) {
                return false;
            }
        } else if (!this.boss.equals(other.boss)) {
            return false;
        }
        if (this.salary != other.salary) {
            return false;
        }
        return true;
    }
}
