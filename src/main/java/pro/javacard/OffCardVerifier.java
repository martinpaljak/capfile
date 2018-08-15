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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;

public class OffCardVerifier {

    private final JavaCardSDK sdk;

    public static OffCardVerifier forSDK(JavaCardSDK sdk) {
        // Only main method in 2.1 SDK
        if (sdk.getVersion().equals(JavaCardSDK.Version.V21))
            throw new RuntimeException("Verification is supported with JavaCard SDK 2.2.1 or later");
        return new OffCardVerifier(sdk);
    }

    private OffCardVerifier(JavaCardSDK sdk) {
        this.sdk = sdk;
        System.out.println("Verifying with " + sdk.getRelease());
        // Warn about recommended usage
        if (!sdk.getRelease().equals("3.0.5u3")) {
            System.err.println("NB! Please use at least JavaCard SDK 3.0.5u3 when verifying!");
        }
    }

    public void verify(File f) throws VerifierError {
        try {
            CAPFile cap = CAPFile.fromStream(new FileInputStream(f));
            // Check for unknown imports
            for (CAPPackage p : cap.getImports()) {
                if (WellKnownAID.getJavaCardName(p.aid) == null) {
                    throw new VerifierError("Can only verify plain JavaCard CAP files at the moment: import " + p.name);
                }
            }
        } catch (IOException e) {
            throw new VerifierError("Could not open CAP: " + e.getMessage(), e);
        }
        Vector<File> exps = new Vector<>();
        verify(f, exps);
    }

    public void verify(File f, Vector<File> exps) throws VerifierError {
        try {
            CAPFile cap = CAPFile.fromStream(new FileInputStream(f));

            // Get verifier class
            Class verifier = Class.forName("com.sun.javacard.offcardverifier.Verifier", true, sdk.getClassLoader());

            // SDK
            exps.add(sdk.getExportDir());

            final Vector<File> expfiles = new Vector<>();
            for (File e : exps) {
                // collect all export files.
                // TODO: Also look into jar files for embedded .exp-s
                if (e.isDirectory()) {
                    Files.walkFileTree(e.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs)
                                throws IOException {
                            if (file.toString().endsWith(".exp")) {
                                expfiles.add(file.toFile());
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } else if (e.isFile()) {
                    expfiles.add(e);
                }
            }

            String packagename = cap.getPackageName();

            try (FileInputStream input = new FileInputStream(f)) {
                // 3.0.5u1 still uses old signature
                if (sdk.getRelease().equals("v3.0.5u3") || sdk.getRelease().equals("v3.0.5u2")) {
                    Method m = verifier.getMethod("verifyCap", File.class, String.class, Vector.class);
                    m.invoke(null, f, packagename, expfiles);
                } else {
                    Method m = verifier.getMethod("verifyCap", FileInputStream.class, String.class, Vector.class);
                    m.invoke(null, input, packagename, expfiles);
                }
            } catch (InvocationTargetException e) {
                throw new VerifierError(e.getTargetException().getMessage(), e.getTargetException());
            }
        } catch (ReflectiveOperationException | IOException e) {
            throw new RuntimeException("Could not run verifier: " + e.getMessage());
        }
    }
}
