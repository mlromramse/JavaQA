package org.jblooming.security;

import org.jblooming.tracer.Tracer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * <pre>
 * </pre>
 *
 * @see PermissionProvider
 */

public class PermissionHome {
  protected static final String CLASS_EXTENSION = ".class";

  static Map permissions = new HashMap();

  Map getPermissionMap() throws IOException {
    Map map = new HashMap();
    String[] roots = System.getProperty("java.class.path").split(File.pathSeparator);
    for (int i = 0; i < roots.length; i++) {
      String root = roots[i];
      File file = new File(root).getCanonicalFile();
      String upperRoot = root.toUpperCase();
      if (!file.exists())
        continue;
      if (file.isFile() && upperRoot.endsWith(".JAR")) {
        processJar(file, map);
      } else if (file.isDirectory()) {
        processFolder(file.getCanonicalFile(), new Stack(), map);
      }
    }
    return map;
  }

  private void processFolder(File folder, Stack folders, Map map) {
    File[] content = folder.listFiles();
    for (int i = 0; i < content.length; i++) {
      File file = content[i];
      if (file.isFile()) {
        if (file.getName().endsWith(CLASS_EXTENSION)) {
          String fqn = composeFQN(file, folders);
          processClass(fqn, map);
        }
      } else if (file.isDirectory()) {
        folders.push(file.getName());
        processFolder(file, folders, map);
        folders.pop();
      }
    }
  }

  private String composeFQN(File file, Stack folders) {
    String className = file.getName();
    className = className.substring(0, className.length() - CLASS_EXTENSION.length());
    StringBuffer sb = new StringBuffer();
    for (Iterator iterator = folders.iterator(); iterator.hasNext();) {
      String folder = (String) iterator.next();
      sb.append(folder).append('.');
    }
    sb.append(className);
    return sb.toString();
  }

  private void processJar(File file, Map map)  {
    /*
    JarFile jar = new JarFile( file );
    Enumeration entries = jar.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = (JarEntry) entries.nextElement();
      String path = entry.getName();
      if( path.endsWith(CLASS_EXTENSION) ) {
        String fqn = composeFQN(path);
        processClass( fqn , map );
      }
    }
    */
  }

  protected void processClass(String fqn, Map map) {
    if (fqn.indexOf('$') < 0) {
      try {
        Class testingClass = getClass().getClassLoader().loadClass(fqn);
        processClass(testingClass, map);
      } catch (Throwable throwable) {
        Tracer.platformLogger.warn("PermissionHome.processClass Class " + fqn + " not found");
      }
    }
  }

  private void processClass(Class testingClass, Map map) {
    if (PermissionProvider.class.isAssignableFrom(testingClass)) {
      Field[] fields = testingClass.getFields();
      for (int j = 0; j < fields.length; j++) {
        Field field = fields[j];
        if (Modifier.isStatic(field.getModifiers()) &&
                Permission.class.isAssignableFrom(field.getType())) {
          field.setAccessible(true);
          try {
            Permission permission = (Permission) field.get(null);
            try {
              storePermission(permission, map);
            } catch (IllegalStateException e) {
              throw new IllegalStateException("Class " + testingClass.getName()
                      + " contains a duplicated permission in field "
                      + field.getName() + " : " + e.getMessage());
            }
          } catch (Throwable throwable) {
            Tracer.platformLogger.warn("PermissionHome.processClass Field \" + field + \" not obtained");
          }
        }
      }
    }
    Class[] declaredClasses = testingClass.getDeclaredClasses();
    for (int i = 0; i < declaredClasses.length; i++) {
      processClass(declaredClasses[i], map);
    }
  }

  private void storePermission(Permission permission, Map map) throws IllegalStateException {

  }

  private String composeFQN(String path) {
    return path.substring(0, path.length() - CLASS_EXTENSION.length())
            .replace('\\', '.')
            .replace('/', '.');
  }

  public static void main(String[] args) throws Throwable {
    Map permissionMap = new PermissionHome().getPermissionMap();
  }
}
