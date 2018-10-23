package io.redskap.java.aws.dynamodb.example.local.testing;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Helper class for initializing AWS DynamoDB to run with sqlite4java for local testing.
 *
 * Copied from: https://github.com/redskap/aws-dynamodb-java-example-local-testing
 */
public class AwsDynamoDbLocalTestUtils {

  private static final String BASE_LIBRARY_NAME = "sqlite4java";

  /**
   * Static helper class.
   */
  private AwsDynamoDbLocalTestUtils() {
  }

  /**
   * Sets the sqlite4java library path system parameter if it is not set already.
   */
  public static void initSqLite() {
    initSqLite(() -> {
      final List<String> classPath = getClassPathList(System.getProperty("java.class.path"), File.pathSeparator);

      final String libPath = getLibPath(System.getProperty("os.name"), System.getProperty("java.runtime.name"),
              System.getProperty("os.arch"), classPath);

      return libPath;
    });
  }

  /**
   * Sets the sqlite4java library path system parameter if it is not set already.
   *
   * @param libPathSupplier Calculates lib path for sqlite4java.
   */
  public static void initSqLite(Supplier<String> libPathSupplier) {
    if (System.getProperty("sqlite4java.library.path") == null) {
      System.setProperty("sqlite4java.library.path", libPathSupplier.get());
    }
  }

  /**
   * Calculates the possible Library Names for finding the proper sqlite4j native library and returns the directory with the most specific matching library.
   *
   * @param osName      The value of <code>"os.name"</code> system property (<code>System.getProperty("os.name")</code>).
   * @param runtimeName The value of <code>"java.runtime.name"</code> system property (<code>System.getProperty("java.runtime.name")</code>).
   * @param osArch      The value of <code>"os.arch"</code> system property (<code>System.getProperty("os.arch")</code>).
   * @param osArch      The classpath split into strings by path separator. Value of <code>"java.class.path"</code> system property
   *                    (<code>System.getProperty("os.arch")</code>) split by <code>File.pathSeparator</code>.
   * @return
   */
  public static String getLibPath(final String osName, final String runtimeName, final String osArch, final List<String> classPath) {
    final String os = getOs(osName, runtimeName);
    final List<String> libNames = getLibNames(os, getArch(os, osArch));

    for (final String libName : libNames) {
      for (final String classPathLib : classPath) {
        if (classPathLib.contains(libName)) {
          return new File(classPathLib).getParent();
        }
      }
    }

    throw new IllegalStateException("SQLite library \"" + libNames + "\" is missing from classpath");
  }

  /**
   * Calculates the possible Library Names for finding the proper sqlite4java native library.
   *
   * Based on the internal calculation of the sqlite4java wrapper <a href="https://bitbucket
   * .org/almworks/sqlite4java/src/fa4bb0fe7319a5f1afe008284146ac83e027de60/java/com/almworks/sqlite4java/Internal
   * .java?at=master&fileviewer=file-view-default#Internal.java-160">Internal
   * class</a>.
   *
   * @param os   Operating System Name used by sqlite4java to get native library.
   * @param arch Operating System Architecture used by sqlite4java to get native library.
   * @return Possible Library Names used by sqlite4java to get native library.
   */
  public static List<String> getLibNames(final String os, final String arch) {
    List<String> result = new ArrayList<>();

    final String base = BASE_LIBRARY_NAME + "-" + os;

    result.add(base + "-" + arch);

    if (arch.equals("x86_64") || arch.equals("x64")) {
      result.add(base + "-amd64");
    } else if (arch.equals("x86")) {
      result.add(base + "-i386");
    } else if (arch.equals("i386")) {
      result.add(base + "-x86");
    } else if (arch.startsWith("arm") && arch.length() > 3) {
      if (arch.length() > 5 && arch.startsWith("armv") && Character.isDigit(arch.charAt(4))) {
        result.add(base + "-" + arch.substring(0, 5));
      }
      result.add(base + "-arm");
    }

    result.add(base);
    result.add(BASE_LIBRARY_NAME);

    return result;
  }

  /**
   * Calculates the Operating System Architecture for finding the proper sqlite4java native library.
   *
   * Based on the internal calculation of the sqlite4java wrapper <a href="https://bitbucket
   * .org/almworks/sqlite4java/src/fa4bb0fe7319a5f1afe008284146ac83e027de60/java/com/almworks/sqlite4java/Internal
   * .java?at=master&fileviewer=file-view-default#Internal.java-204">Internal
   * class</a>.
   *
   * @param osArch The value of <code>"os.arch"</code> system property (<code>System.getProperty("os.arch")</code>).
   * @param os     Operating System Name used by sqlite4java to get native library.
   * @return Operating System Architecture used by sqlite4java to get native library.
   */
  public static String getArch(final String os, final String osArch) {
    String result;

    if (osArch == null) {
      result = "x86";
    } else {
      final String loweCaseOsArch = osArch.toLowerCase(Locale.US);
      result = loweCaseOsArch;
      if ("win32".equals(os) && "amd64".equals(loweCaseOsArch)) {
        result = "x64";
      }
    }

    return result;
  }

  /**
   * Calculates the Operating System Name for finding the proper sqlite4java native library.
   *
   * Based on the internal calculation of the sqlite4java wrapper <a href="https://bitbucket
   * .org/almworks/sqlite4java/src/fa4bb0fe7319a5f1afe008284146ac83e027de60/java/com/almworks/sqlite4java/Internal
   * .java?at=master&fileviewer=file-view-default#Internal.java-219">Internal
   * class</a>.*
   *
   * @param osName      The value of <code>"os.name"</code> system property (<code>System.getProperty("os.name")</code>).
   * @param runtimeName The value of <code>"java.runtime.name"</code> system property (<code>System.getProperty("java.runtime.name")</code>).
   * @return Operating System Name used by sqlite4java to get native library.
   */
  public static String getOs(final String osName, final String runtimeName) {

    String result;
    if (osName == null) {
      result = "linux";
    } else {
      final String loweCaseOsName = osName.toLowerCase(Locale.US);
      if (loweCaseOsName.startsWith("mac") || loweCaseOsName.startsWith("darwin") || loweCaseOsName.startsWith("os x")) {
        result = "osx";
      } else if (loweCaseOsName.startsWith("windows")) {
        result = "win32";
      } else {
        if (runtimeName != null && runtimeName.toLowerCase(Locale.US).contains("android")) {
          result = "android";
        } else {
          result = "linux";
        }
      }
    }

    return result;
  }

  /**
   * Splits classpath string by path separator value.
   *
   * @param classPath     Value of <code>"java.class.path"</code> system property (<code>System.getProperty("os.arch")</code>).
   * @param pathSeparator Value of path separator (<code>File.pathSeparator</code>).
   * @return The list of each classpath elements.
   */
  public static List<String> getClassPathList(final String classPath, final String pathSeparator) {
    return Lists.newArrayList(Splitter.on(pathSeparator).split(classPath));
  }

}
