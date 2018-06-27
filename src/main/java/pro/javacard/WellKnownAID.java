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

public class WellKnownAID {

    private static Map<AID, String> registry = new HashMap<>();

    static {
        // Copied from https://stackoverflow.com/questions/25031338/how-to-get-javacard-version-on-card/25063015#25063015
        // NOT verified
        registry.put(AID.fromString("A0000000620001"), "java.lang");
        registry.put(AID.fromString("A0000000620002"), "java.io");
        registry.put(AID.fromString("A0000000620003"), "java.rmi");

        registry.put(AID.fromString("A0000000620101"), "javacard.framework");
        registry.put(AID.fromString("A0000000620102"), "javacard.security");
        registry.put(AID.fromString("A000000062010101"), "javacard.framework.service");

        registry.put(AID.fromString("A0000000620201"), "javacardx.crypto");
        registry.put(AID.fromString("A0000000620202"), "javacardx.biometry");
        registry.put(AID.fromString("A0000000620203"), "javacardx.external");
        registry.put(AID.fromString("A0000000620209"), "javacardx.apdu");

        registry.put(AID.fromString("A000000062020801"), "javacardx.framework.util");
        registry.put(AID.fromString("A00000006202080101"), "javacardx.framework.util.intx");
        registry.put(AID.fromString("A000000062020802"), "javacardx.framework.math");
        registry.put(AID.fromString("A000000062020803"), "javacardx.framework.tlv");

        registry.put(AID.fromString("A00000015100"), "org.globalplatform");
    }


    public static String getName(AID aid) {
        return registry.get(aid);
    }
}
