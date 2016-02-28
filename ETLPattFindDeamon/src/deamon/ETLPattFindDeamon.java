package deamon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.Scanner;

public class ETLPattFindDeamon {
  public static final String SYSOUT_FILE = "data/deamon_sysout.txt";
  public static final String NEW_TASK_DIR = "data/newTaskInfo";
  public static final String JAR_PATH = "ETLPattFind.jar";

  public static void main(String[] args) {
    PrintStream output = redirectOutput(SYSOUT_FILE);
    WatchService watcher;
    try {
      sysOutTimestamp("ETLPattFind Deamon started");
      watcher = FileSystems.getDefault().newWatchService();
      Path dir = FileSystems.getDefault().getPath(NEW_TASK_DIR);
      dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
      
      while (true) {
        try {
          WatchKey key = watcher.take();
          for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == StandardWatchEventKinds.OVERFLOW) {
              continue;
            }

            // Process event
            @SuppressWarnings("unchecked")
            Path filename = ((WatchEvent<Path>) event).context().getFileName();
            sysOutTimestamp("New file detected: " + filename);
            launchTask(NEW_TASK_DIR + "/" + filename.toString());
            sysOutTimestamp("Moving on");
          }

          // Reset the key
          if (!key.reset()) {
            sysOutTimestamp("ERROR: Watch key became invalid.");
            return;
          }
        } catch (InterruptedException e) {
          sysOutTimestamp("ERROR: Waiting for event interrupted.");
          e.printStackTrace(System.out);
        }
      }
      
    } catch (IOException e) {
      sysOutTimestamp("ERROR: Unable to create watcher service or attach key.");
      e.printStackTrace(System.out);
    } catch (InvalidPathException e) {
      sysOutTimestamp("ERROR: Cannot find direcotry for new tasks.");
      e.printStackTrace(System.out);
    } finally {
      resetOutput(output);
    }
  }

  public static void launchTask(String infoFileName) {
    // Parse information from info file
    sysOutTimestamp("Parsing input information");
    Scanner in;
    try {
      in = new Scanner(new File(infoFileName));
    } catch (FileNotFoundException e) {
      sysOutTimestamp("ERROR: Cannot find input information file.");
      e.printStackTrace(System.out);
      return;
    }
    in.nextLine(); // Skip task name
    in.nextLine(); // Skip start time
    String taskDir = in.nextLine();
    String inputFile = in.nextLine();
    String paramFile = taskDir + "/parameters.params";
    // Make .param file
    try {
      PrintStream paramOut = new PrintStream(new File(paramFile));
      while (in.hasNext()) {
        paramOut.println(in.nextLine());
      }
      paramOut.close();
    } catch (FileNotFoundException e) {
      sysOutTimestamp("ERROR: Cannot create .params file.");
      e.printStackTrace(System.out);
    }    
    in.close();
    // Move info file to task directory
    try {
      Files.move(FileSystems.getDefault().getPath(infoFileName),
                 FileSystems.getDefault().getPath(taskDir + "/inputInfo.txt"));
    } catch (IOException e) {
      sysOutTimestamp("ERROR: Cannot move input info file to task directory.");
      e.printStackTrace(System.out);
    }
    
    // Launch the ETLPattFind
    try {
      sysOutTimestamp("Launching ETLPattFind");
      String[] command = new String[] {
          "java", "-jar", JAR_PATH, taskDir + "/" + inputFile,
          "-p", paramFile,
          "-s", taskDir + "/sysout.txt"
      };
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      sysOutTimestamp("ERROR: Running ETLPattFind - I/O error occured.");
      e.printStackTrace(System.out);
    } catch (SecurityException e) {
      sysOutTimestamp("ERROR: Running ETLPattFind - "
          + "security manager`s checkExec method doesn't allow creation of the subprocess.");
      e.printStackTrace(System.out);
    }
  }

  /**
   * Prints message to System.out (possibly redirected to file), with included timestamp of the
   * message.
   */
  public static void sysOutTimestamp(String msg) {
    System.out.println((new Date()) + ":\t" + msg);
  }
  
  /**
   * Redirects standard output (System.out) to specified file.
   * @param path path to file
   * @return {@link PrintStream} to redirected output (needed to be flushed and closed on exit)
   */
  public static PrintStream redirectOutput(String path) {
    PrintStream outPs = null;
    try {
      outPs = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)), true, "UTF-8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    System.setOut(outPs);
    return outPs;
  }

  /**
   * Resets standard output (System.out) to default value.
   * @param outPs {@link PrintStream} that was used up to now - it will be flushed and closed
   */
  public static void resetOutput(PrintStream outPs) {
    outPs.flush();
    outPs.close();
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

}