package org.peletomi.vax.impl.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.peletomi.vax.impl.util.BeanUtils.addPrefix;

import org.junit.Test;

public class BeanUtilsTest {

    @Test
    public void testAddPrefix() throws Exception {
        assertThat(addPrefix("bla", "bla"), equalTo("blaBla")) ;
    }
}
