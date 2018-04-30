package io.redskap.java.aws.dynamodb.example.local.testing;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for initializing AWS DynamoDB to run with sqlite4java for local testing.
 *
 * Copied from: https://github.com/redskap/aws-dynamodb-java-example-local-testing
 */
public class AwsDynamoDbHelper {

  /**
   * Sets the sqlite4java library path system parameter if it is not set already.
   */
  public static void initSqLite() {
    if (System.getProperty("sqlite4java.library.path") == null) {
      System.setProperty("sqlite4java.library.path", getLibPath());
    }
  }

  /**
   * Search for the sqlite4java library path used by AWS DynamoDB Local implementation, based on the name and the architecture of the Operating System.
   *
   * @return Path of the Operating System specific sqlite4java library.
   */
  private static String getLibPath() {
    System.out.println(System.getProperty("java.class.path"));
    final List<String> classpath = Lists.newArrayList(Splitter.on(File.pathSeparator).split(System.getProperty("java.class.path")));
    final String libName = getLibName();

    for (final String classpathLib : classpath) {
      if (classpathLib.contains(libName)) {
        return new File(classpathLib).getParent();
      }
    }

    throw new IllegalStateException("SQLite library '" + libName + "' is missing from classpath");
  }

  /**
   * Calculates library Name used by AWS DynamoDB Local implementation.
   *
   * @return Library Name that could be find on the class path.
   */
  private static String getLibName() {
    final String os = getOs();
    final String arch = getArch(os);

    final String suffix = os + "-" + arch;

    final String libName;

    if (os.equals("win32")) {
      libName = "sqlite4java-" + suffix;
    } else {
      libName = "libsqlite4java-" + suffix;
    }

    return libName;
  }

  /**
   * Gets the Operating System Architecture for finding the proper sqlite4java native library.
   *
   * Based on the internal calculation of the sqlite4java wrapper <a href="https://bitbucket.org/almworks/sqlite4java/src/fa4bb0fe7319a5f1afe008284146ac83e027de60/java/com/almworks/sqlite4java/Internal.java?at=master&fileviewer=file-view-default#Internal.java-204">Internal class</a>.
   *
   * @param os Operating System Name used by sqlite4java to get native library.
   * @return Operating System Architecture used by sqlite4java to get native library.
   */
  private static String getArch(final String os) {
    String arch = System.getProperty("os.arch");
    if (arch == null) {
      arch = "x86";
    } else {
      arch = arch.toLowerCase(Locale.US);
      if ("win32".equals(os) && "amd64".equals(arch)) {
        arch = "x64";
      }
    }
    return arch;
  }

  /**
   * Gets the Operating System Name for finding the proper sqlite4java native library.
   *
   * Based on the internal calculation of the sqlite4java wrapper <a href="https://bitbucket.org/almworks/sqlite4java/src/fa4bb0fe7319a5f1afe008284146ac83e027de60/java/com/almworks/sqlite4java/Internal.java?at=master&fileviewer=file-view-default#Internal.java-219">Internal class</a>.*
   *
   * @return Operating System Name used by sqlite4java to get native library.
   */
  private static String getOs() {
    String osname = System.getProperty("os.name");
    String os;
    if (osname == null) {
      os = "linux";
    } else {
      osname = osname.toLowerCase(Locale.US);
      if (osname.startsWith("mac") || osname.startsWith("darwin") || osname.startsWith("os x")) {
        os = "osx";
      } else if (osname.startsWith("windows")) {
        os = "win32";
      } else {
        String runtimeName = System.getProperty("java.runtime.name");
        if (runtimeName != null && runtimeName.toLowerCase(Locale.US).contains("android")) {
          os = "android";
        } else {
          os = "linux";
        }
      }
    }
    return os;
  }

}
