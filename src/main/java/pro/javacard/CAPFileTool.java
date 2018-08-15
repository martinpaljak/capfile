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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

public class CAPFileTool {

    private static boolean has(Vector<String> args, String v) {
        for (String s : args) {
            if (s.equalsIgnoreCase(v)) {
                args.remove(s);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] argv) {
        Vector<String> args = new Vector<>(Arrays.asList(argv));

        boolean verify = has(args, "-v");

        OffCardVerifier verifier = null;

        if (verify) {
            String sdkpath = System.getenv("JC_HOME");
            if (sdkpath == null)
                fail("You need to point $JC_HOME to a JavaCard SDK to verify CAP files!");

            JavaCardSDK sdk = JavaCardSDK.detectSDK(sdkpath);
            if (sdk == null)
                fail("Could not fetect a valid JavaCard SDK in $JC_HOME: " + sdkpath);

            verifier = OffCardVerifier.forSDK(sdk);
        }

        if (args.size() < 1) {
            fail("capfile [-v] <file ...>");
        }
        try {
            for (String f : args) {
                System.out.println("# " + f);
                CAPFile cap = CAPFile.fromBytes(Files.readAllBytes(Paths.get(f)));
                cap.dump(System.out);
                if (verify) {
                    try {
                        verifier.verify(new File(f));
                    } catch (VerifierError e) {
                        System.err.println("Verification failed: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            fail("Could not read CAP file: " + e.getMessage());
        }
    }

    private static void fail(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
