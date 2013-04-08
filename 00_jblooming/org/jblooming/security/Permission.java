package org.jblooming.security;

import org.jblooming.PlatformRuntimeException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

public class Permission implements Comparable, Serializable {

  public String name;

  private Permission() {
  }

  public Permission(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }

  public int compareTo(Object o) {
    if (o == null)
      return 0;
    else
      return name.compareTo(((Permission) o).getName());
  }

  public boolean equals(Object o) {
    return this.compareTo(o) == 0;
  }

  /**
   * This method solves collection'identity
   */
  public int hashCode() {
    return getName().hashCode();
  }

  public static void addPermissions(Object permissionClassInstance, Map<String, Permission> permissions) {
    Field[] field = permissionClassInstance.getClass().getDeclaredFields();
    for (int i = 0; i < field.length; i++) {
      Field field1 = field[i];
      if (field1.getType().equals(Permission.class))
        try {
          Permission perm = (Permission) field1.get(permissionClassInstance);
          permissions.put(perm.getName(), perm);
        } catch (IllegalAccessException e) {
          throw new PlatformRuntimeException(e);
        }
    }
  }

  public String toString() {
    return name;
  }
}

