package pro.javacard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;

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

        if (args.size() < 1 || has(args, "-h")) {
            System.err.println("Usage:");
            System.err.println("    dump:   capfile <capfile>");
            System.err.println("    verify: capfile -v <sdkpath> <capfile> [<expfiles...>]");
            System.err.println("    sign:   capfile -s <keyfile> <capfile>");
            System.exit(1);
        }

        try {
            if (has(args, "-s")) {
                if (args.size() < 2)
                    fail("Usage:\n    capfile -s <keyfile> <capfile>");
                String keyfile = args.remove(0);
                Path capfile = Paths.get(args.remove(0));
                CAPFile cap = CAPFile.fromBytes(Files.readAllBytes(capfile));
                cap.dump(System.out);
                try {
                    KeyPair kp = CAPFileSigner.pem2keypair(keyfile);
                    CAPFileSigner.addSignature(cap, kp.getPrivate());
                    Path where = capfile.getParent();
                    if (where == null)
                        where = Paths.get(".");
                    Path tmpfile = Files.createTempFile(where, "capfile", "unsigned");
                    cap.store(Files.newOutputStream(tmpfile));
                    Files.move(tmpfile, capfile, StandardCopyOption.ATOMIC_MOVE);
                    System.out.println("Signed " + capfile);
                } catch (GeneralSecurityException e) {
                    fail("Failed to sign: " + e.getMessage());
                }

            } else if (has(args, "-v")) {
                if (args.size() < 2)
                    fail("Usage:\n    capfile -v <sdkpath> <capfile> [<expfiles...>]");
                String sdkpath = args.remove(0);
                String capfile = args.remove(0);
                Vector<File> exps = new Vector<>(args.stream().map(i -> new File(i)).collect(Collectors.toList()));
                CAPFile cap = CAPFile.fromBytes(Files.readAllBytes(Paths.get(capfile)));
                cap.dump(System.out);
                try {
                    JavaCardSDK sdk = JavaCardSDK.detectSDK(sdkpath);
                    OffCardVerifier verifier = OffCardVerifier.forSDK(sdk);
                    verifier.verify(new File(capfile), exps);
                    System.out.println("Verified " + capfile);
                } catch (VerifierError e) {
                    fail("Verification failed: " + e.getMessage());
                }
            } else {
                String capfile = args.remove(0);
                CAPFile cap = CAPFile.fromBytes(Files.readAllBytes(Paths.get(capfile)));
                cap.dump(System.out);
            }
        } catch (IOException | IllegalArgumentException e) {
            fail(e.getMessage());
        }
    }

    private static void fail(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
