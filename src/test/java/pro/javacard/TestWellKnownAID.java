package pro.javacard;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Optional;

public class TestWellKnownAID {

    @Test
    public void testInternalList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("aid_list.yml")) {
            WellKnownAID.load(in);
            Assert.assertEquals(WellKnownAID.getName(AID.fromString("D276000085494A434F5058")), Optional.of("com.nxp.id.jcopx"));
        }
    }
}
