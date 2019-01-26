/*
 * Copyright (c) 2018 Martin Paljak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pro.javacard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WellKnownAID {

    private static Map<AID, String> javaCardRegistry = new HashMap<>();
    private static Map<AID, String> wellKnownRegistry = new HashMap<>();

    static {
        // Copied from https://stackoverflow.com/questions/25031338/how-to-get-javacard-version-on-card/25063015#25063015
        // Extended and verified against JC SDK exp files
        javaCardRegistry.put(AID.fromString("A0000000620001"), "java.lang");
        javaCardRegistry.put(AID.fromString("A0000000620002"), "java.io");
        javaCardRegistry.put(AID.fromString("A0000000620003"), "java.rmi");

        javaCardRegistry.put(AID.fromString("A0000000620101"), "javacard.framework");
        javaCardRegistry.put(AID.fromString("A000000062010101"), "javacard.framework.service");
        javaCardRegistry.put(AID.fromString("A0000000620102"), "javacard.security");

        javaCardRegistry.put(AID.fromString("A0000000620201"), "javacardx.crypto");
        javaCardRegistry.put(AID.fromString("A0000000620202"), "javacardx.biometry");
        javaCardRegistry.put(AID.fromString("A0000000620203"), "javacardx.external");
        javaCardRegistry.put(AID.fromString("A0000000620204"), "javacardx.biometry1toN");
        javaCardRegistry.put(AID.fromString("A0000000620205"), "javacardx.security");

        javaCardRegistry.put(AID.fromString("A000000062020801"), "javacardx.framework.util");
        javaCardRegistry.put(AID.fromString("A00000006202080101"), "javacardx.framework.util.intx");
        javaCardRegistry.put(AID.fromString("A000000062020802"), "javacardx.framework.math");
        javaCardRegistry.put(AID.fromString("A000000062020803"), "javacardx.framework.tlv");
        javaCardRegistry.put(AID.fromString("A000000062020804"), "javacardx.framework.string");

        javaCardRegistry.put(AID.fromString("A0000000620209"), "javacardx.apdu");
        javaCardRegistry.put(AID.fromString("A000000062020901"), "javacardx.apdu.util");

        // Other well-known AID-s
        wellKnownRegistry.put(AID.fromString("A00000015100"), "org.globalplatform");
        wellKnownRegistry.put(AID.fromString("A0000000030000"), "visa.openplatform");

        // Well known card vendor packages
        wellKnownRegistry.put(AID.fromString("D276000085494A434F5058"), "com.nxp.id.jcopx");
    }

    public static String getJavaCardName(AID aid) {
        return javaCardRegistry.get(aid);
    }

    public static Optional<String> getName(AID aid) {
        return Optional.ofNullable(wellKnownRegistry.getOrDefault(aid, javaCardRegistry.get(aid)));
    }
}
