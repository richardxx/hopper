package edu.colorado.thresher.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

public class Options {

  @Retention(RetentionPolicy.RUNTIME)
  @interface boolOpt {
    String description();

    boolean _default();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface intOpt {
    String description();

    int _default();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface stringOpt {
    String description();

    String _default();
  }

  @boolOpt(description = "Print debug info", _default = false)
  public static boolean DEBUG = false;

  @boolOpt(description = "Print reasons for refutations", _default = false)
  public static boolean PRINT_REFS = false;

  @boolOpt(description = "Give up after internal assertion failure/crash", _default = true)
  public static boolean EXIT_ON_FAIL = true;

  @boolOpt(description = "Perform flow-insensitive points-to analysis only; don't do symbolic execution", _default = false)
  public static boolean FLOW_INSENSITIVE_ONLY = false;

  @boolOpt(description = "Handle exceptions soundly", _default = false)
  public static boolean SOUND_EXCEPTIONS = false;

  @boolOpt(description = "Use jumping symbolic executor for better scalability", _default = false)
  public static boolean JUMPING_EXECUTION = false;

  @boolOpt(description = "Backtrack after failed jumps", _default = false)
  public static boolean BACKTRACK_JUMPING = false;

  @boolOpt(description = "Use control-feasibility information for more precise jumps", _default = false)
  public static boolean CONTROL_FEASIBILITY = false;
  
  @boolOpt(description = "(under development)", _default = false)
  public static boolean SYNTHESIS = false;
  
  @boolOpt(description = "Check for Android Activity leaks", _default = false)
  public static boolean CHECK_ANDROID_LEAKS = false;

  @boolOpt(description = "Check for null dereferences in Android apps", _default = false)
  public static boolean CHECK_ANDROID_DEREFS = false;
  
  @boolOpt(description = "Check user assertions", _default = false)
  public static boolean CHECK_ASSERTS = false;
  
  @boolOpt(description = "Check downcast safety", _default = false)
  public static boolean CHECK_CASTS = false;
  
  @boolOpt(description = "Check array bounds", _default = false)
  public static boolean CHECK_ARRAY_BOUNDS = false;

  @boolOpt(description = "Check null derefs", _default = false)
  public static boolean CHECK_NULLS = false;

  @boolOpt(description = "Check divison by zero", _default = false)
  public static boolean CHECK_DIV_BY_ZERO = false;

  // should we use Manu's demand cast checker to easily prove the safety of casts requiring context-sensitivity?
  @boolOpt(description = "Filter cast checking results using demand cast checker", _default = false)
  public static boolean USE_DEMAND_CAST_CHECKER = false;

  @boolOpt(description = "Prints end-to-end list of witnessed heap edges for witnessed error", _default = true)
  public static boolean DUMP_WITNESSED_ERR_PATHS = true;

  @boolOpt (description="If true, only report 'witnessed' if the query is true and we have reached the beginning of the program", _default = false)
  public static boolean FULL_WITNESSES = false;

  @intOpt(description = "If the call stack is larger than this, we drop constraints that can be produced in callees rather than exploring them",
          _default = 4)
  public static int MAX_CALLSTACK_DEPTH = 4;
  
  @boolOpt(description = "Do index-sensitive reasoning", _default = false)
  public static boolean INDEX_SENSITIVITY = false;
  
  @boolOpt(description = "Should the pointer analysis have object-sensitivity on arrays of primitive type?", _default = false)
  public static boolean PRIM_ARRAY_SENSITIVITY = false;
  
  @boolOpt(description = "Should the pointer analysis use pi nodes to handle instanceOf intelligently?", _default = false)
  public static boolean USE_PI_NODES = false;
  
  @boolOpt(description = "Should we use ptBy information and recursive simplification to further narrow from constraints?", _default = false)
  public static boolean AGGRESSIVE_FROM_NARROWING = false;

  @boolOpt(description = "Processs queries in parallel. Still an experimental feature", _default = false)
  public static boolean PARALLEL = false;

  @intOpt(description = "Time out and report a witness if we spend more time than this on a query", _default = 10)
  public static int TIMEOUT = 10;  

  @stringOpt(description = "Usage: -app <path to directory of .class files to analyze>", _default = "")
  public static String APP;
  
  @stringOpt(description = "JAR of library files to load", _default = "")
  public static String LIB = "";

  @stringOpt(description = "Java library file to use (e.g., rt.jar or classes.jar)", _default = "")
  public static String JAVA_LIB = "";

  @stringOpt(description = "Class containing entrypoint method for analysis", _default = "Main")
  public static String MAIN_CLASS = "Main";
  
  @stringOpt(description = "Entrypoint method for analysis", _default = "main")
  public static String MAIN_METHOD = "main";

  @intOpt(description = "Limit checking to specificed line in main_class (for debugging only)", _default = -2)
  public static int LINE = -2;

  @stringOpt(description = "Path to home directory of droidel", _default = "../droidel")
  public static String DROIDEL_HOME = "../droidel";

  @stringOpt(description = "Usage: -android_jar <path to jar file for version of android libraries>",
             _default = "lib/droidel_android-4.4.2_r1.jar")
  public static String ANDROID_JAR = "lib/droidel_android-4.4.2_r1.jar";

  public static final String DEFAULT_EXCLUSIONS = "exclusions.txt";

  @stringOpt(description = "List of classes to excluse from analysis", _default = DEFAULT_EXCLUSIONS)
  public static String EXCLUSIONS = DEFAULT_EXCLUSIONS;

  @stringOpt(description = "List of downcast queries to answer (by number)", _default = "")
  public static String CAST_QUERIES = "";

  @stringOpt(description = "Run a particular test", _default = "")
  public static String TEST;

  // consider paths that use weak references?
  public static boolean INCLUDE_WEAK_REFERENCES = false;
  
  public static void restoreDefaults() {
    try {
      for (Field field : Options.class.getFields()) {
        if (field.isAnnotationPresent(intOpt.class)) {
          intOpt opt = field.getAnnotation(intOpt.class);
          field.setInt(Options.class, opt._default());
        } else if (field.isAnnotationPresent(boolOpt.class)) {
          boolOpt opt = field.getAnnotation(boolOpt.class);
          field.setBoolean(Options.class, opt._default());
        } else if (field.isAnnotationPresent(stringOpt.class)) {
          stringOpt opt = field.getAnnotation(stringOpt.class);
          field.set(Options.class, opt._default());
        }
      }
    } catch (IllegalAccessException e) {
      System.out.println(e);
      System.exit(1);
    }
  }

  // return path to dir to perform analysis on or _REGRESSION if regressions
  public static String parseArgs(String[] args) {
    if (args.length == 0)
      dumpHelpInfo();
    int index = 0;
    while (index < args.length) {
      String arg = args[index];
      if (arg == "") {
        index++;
        continue;
      } else if (arg.startsWith("-")) {
        boolean negate = false;
        if (arg.endsWith("!")) {
          negate = true;
          arg = arg.substring(0, arg.length() - 1);
        }
        // strip off "-"
        arg = arg.substring(1);
        // special cases
        if (arg.equals("help"))
          dumpHelpInfo();
        else if (arg.equals("regressions")) {
          APP = "__regression";
          index++;
          continue;
        }
        // else, actually parsing options
        try {
          String upper = arg.toUpperCase();
          Field field = Options.class.getField(upper);
          if (field.getType().isAssignableFrom(Integer.TYPE)) {
            int newVal = Integer.parseInt(args[++index]);
            field.setInt(Options.class, newVal);
          } else if (field.getType().isAssignableFrom(Boolean.TYPE)) {
            field.setBoolean(Options.class, !negate);
          } else if (field.getType().isAssignableFrom(String.class)) {
            String newVal = args[++index];
            field.set(Options.class, newVal);
          }
        } catch (NoSuchFieldException e) {
          complain("Unrecognized option: " + arg);
        } catch (IllegalAccessException e) {
          complain("Can't write to field " + arg);
        } catch (Exception e) {
          complain("Bad or missing argument for option " + arg);
        }
      }
      index++;
    }
    return APP;
  }

  private static void dumpHelpInfo() {
    String optionStr = "USAGE: ./hopper.sh -app <app_name> <options>. Use ! to negate boolean flags (e.g. -check_asserts!) \n";
    for (Field field : Options.class.getDeclaredFields()) {
      Annotation[] annots = field.getAnnotations();
      if (annots.length > 0) {
        if (annots[0] instanceof stringOpt) {
          stringOpt opt = (stringOpt) annots[0];
          optionStr += "-" + field.getName().toLowerCase() + "\t[" + opt.description() + ".]";
          if (opt._default() != null)
            optionStr += "\tdefault: " + opt._default();
          optionStr += "\n";
        } else if (annots[0] instanceof boolOpt) {
          boolOpt opt = (boolOpt) annots[0];
          optionStr += "-" + field.getName().toLowerCase() + "\t[" + opt.description() + ".]\tdefault: " + opt._default() + "\n";
        } else if (annots[0] instanceof intOpt) {
          intOpt opt = (intOpt) annots[0];
          optionStr += "-" + field.getName().toLowerCase() + "\t[" + opt.description() + ".\t]default: " + opt._default() + "\n";
        }
      }
    }
    System.out.println(optionStr.toString());
    System.exit(1);
  }

  private static void complain(String complaint) {
    System.out.println(complaint);
    System.exit(1);
  }
}
