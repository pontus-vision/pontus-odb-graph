package com.pontusvision.gdpr;

import org.eclipse.jetty.server.Server;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GlobalSetupTeardownListener implements LauncherSessionListener {

  private Fixture fixture;

  @Override
  public void launcherSessionOpened(LauncherSession session) {
    // Avoid setup for test discovery by delaying it until tests are about to be executed
    session.getLauncher().registerTestExecutionListeners(new TestExecutionListener() {
      @Override
      public void testPlanExecutionStarted(TestPlan testPlan) {
        if (fixture == null) {
          fixture = new Fixture();
          try {
            fixture.setUp();
          } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Failed to start the graphdb service: " + e.getMessage());
          }
        }
      }
    });
  }

  @Override
  public void launcherSessionClosed(LauncherSession session) {
    if (fixture != null) {
      try {
        fixture.tearDown();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to stop the graphdb service: " + e.getMessage());
      }
      fixture = null;
    }
  }

  static class Fixture {

    public static Server server;

    public static boolean deleteDirectory(File directoryToBeDeleted) {
      File[] allContents = directoryToBeDeleted.listFiles();
      if (allContents != null) {
        for (File file : allContents) {
          deleteDirectory(file);
        }
      }
      return directoryToBeDeleted.delete();
    }

    void setUp() throws Exception {
      Path resourceDirectory = Paths.get(".");
      String absolutePath = resourceDirectory.toFile().getAbsolutePath();

      String databaseDir = Paths.get(absolutePath, "databases").toString();
      deleteDirectory(new File(databaseDir));

      String jpostalDataDir = Paths.get(absolutePath, "jpostal", "libpostal").toString();
      System.setProperty("user.dir", absolutePath);
      System.setProperty("ORIENTDB_ROOT_PASSWORD", "pa55word");
      System.setProperty("ORIENTDB_HOME", absolutePath);
      System.setProperty("pg.jpostal.datadir", jpostalDataDir);

      server = App.createJettyServer();

      server.start();
      App.init(Paths.get(absolutePath, "config", "gremlin-server.yaml").toString());

      System.out.println("finished Init");

    }

    void tearDown() throws Exception {
      server.stop();
      App.oServer.dropDatabase("demodb");
      App.oServer.shutdown();
      App.oServer.waitForShutdown();
      Path resourceDirectory = Paths.get(".");
      String absolutePath = resourceDirectory.toFile().getAbsolutePath();

      String databaseDir = Paths.get(absolutePath, "databases").toString();
      deleteDirectory(new File(databaseDir));
    }
  }

}
