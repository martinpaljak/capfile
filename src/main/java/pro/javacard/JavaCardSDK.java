/**
 * Copyright (c) 2015-2018 Martin Paljak
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class JavaCardSDK {

    public static JavaCardSDK detectSDK(String path) {
        if (path == null || path.trim().length() == 0) {
            return null;
        }

        File root = new File(path);
        if (!root.isDirectory()) {
            return null;
        }

        Version version = detectSDKVersion(root);
        if (version == null) {
            return null;
        }

        return new JavaCardSDK(root, version);
    }

    private static Version detectSDKVersion(File root) {
        Version version = null;
        File libDir = new File(root, "lib");
        if (new File(libDir, "tools.jar").exists()) {
            File api = new File(libDir, "api_classic.jar");
            try (ZipFile apiZip = new ZipFile(api)) {
                if (apiZip.getEntry("javacard/framework/SensitiveArrays.class") != null) {
                    return Version.V305;
                }
                if (apiZip.getEntry("javacardx/framework/string/StringUtil.class") != null) {
                    return Version.V304;
                }
                return Version.V301;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (new File(libDir, "api21.jar").exists()) {
            version = JavaCardSDK.Version.V21;
        } else if (new File(libDir, "converter.jar").exists()) {
            // assume 2.2.1 first
            version = Version.V221;
            // test for 2.2.2 by testing api.jar
            File api = new File(libDir, "api.jar");
            try (ZipFile apiZip = new ZipFile(api)) {
                ZipEntry testEntry = apiZip.getEntry("javacardx/apdu/ExtendedLength.class");
                if (testEntry != null) {
                    version = JavaCardSDK.Version.V222;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return version;
    }

    private Version version = Version.NONE;
    private File path = null;

    private JavaCardSDK(File root, Version version) {
        this.path = root;
        this.version = version;
    }

    public File getRoot() {
        return path;
    }

    public Version getVersion() {
        return version;
    }

    public boolean isVersion(Version v) {
        return version.equals(v);
    }

    public String getJavaVersion() {
        switch (version) {
            case V301:
            case V304:
            case V305:
                return "1.6";
            case V222:
                return "1.5";
            case V221:
                return "1.2";
            default:
                return "1.1";
        }
    }

    public ClassLoader getClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
            public URLClassLoader run() {
                try {
                    if (version.isV3()) {
                        return new URLClassLoader(new URL[]{getJar("tools.jar").toURI().toURL()}, this.getClass().getClassLoader());
                    } else {
                        return new URLClassLoader(new URL[]{getJar("offcardverifier.jar").toURI().toURL()}, this.getClass().getClassLoader());
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Could not load classes: " + e.getMessage());
                }
            }
        });
    }

    public String getRelease() {
        if (version == Version.V305) {
            try {
                // Get verifier class
                Class verifier = Class.forName("com.sun.javacard.offcardverifier.Verifier", false, getClassLoader());

                // Check if 3.0.5u3 (or, hopefully, later)
                try {
                    verifier.getDeclaredMethod("verifyTargetPlatform", String.class);
                    return "3.0.5u3";
                } catch (NoSuchMethodException e) {
                    // Do nothing
                }

                // Check if 3.0.5u1
                try {
                    verifier.getDeclaredMethod("verifyCap", FileInputStream.class, String.class, Vector.class);
                    return "3.0.5u1";
                } catch (NoSuchMethodException e) {
                    // Do nothing
                }
                // Assume 3.0.5u2 otherwise
                return "3.0.5u2";
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Could not figure out SDK release: " + e.getMessage());
            }
        } else {
            // No updates with older SDK-s
            return version.toString();
        }
    }

    public File getJar(String name) {
        File libDir = new File(path, "lib");
        return new File(libDir, name);
    }

    public List<File> getApiJars() {
        List<File> jars = new ArrayList<>();
        switch (version) {
            case V21:
                jars.add(getJar("api21.jar"));
                break;
            case V301:
            case V304:
            case V305:
                jars.add(getJar("api_classic.jar"));
                break;
            default:
                jars.add(getJar("api.jar"));
        }
        // Add annotations
        if (version == Version.V304 || version == Version.V305) {
            jars.add(getJar("api_classic_annotations.jar"));
        }
        return jars;
    }

    public File getExportDir() {
        switch (version) {
            case V21:
                return new File(path, "api21_export_files");
            default:
                return new File(path, "api_export_files");
        }
    }

    public List<File> getToolJars() {
        List<File> jars = new ArrayList<>();
        if (version.isV3()) {
            jars.add(getJar("tools.jar"));
        } else {
            jars.add(getJar("converter.jar"));
            jars.add(getJar("offcardverifier.jar"));
        }
        return jars;
    }

    public List<File> getCompilerJars() {
        List<File> jars = new ArrayList<>();
        if (version == Version.V304) {
            jars.add(getJar("tools.jar"));
            jars.add(getJar("api_classic_annotations.jar"));
        }
        return jars;
    }

    public enum Version {
        NONE, V21, V221, V222, V301, V304, V305;

        @Override
        public String toString() {
            if (this.equals(V305))
                return "3.0.5";
            if (this.equals(V304))
                return "3.0.4";
            if (this.equals(V301))
                return "3.0.1";
            if (this.equals(V222))
                return "2.2.2";
            if (this.equals(V221))
                return "2.2.1";
            if (this.equals(V21))
                return "2.1.1";
            return "unknown";
        }

        public boolean isV3() {
            switch (this) {
                case V301:
                case V304:
                case V305:
                    return true;
                default:
                    return false;
            }
        }
    }

}
