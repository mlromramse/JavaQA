package org.jblooming.tracer;

/**
 * @author pietro polsinelli info@twproject.com
 */
public class TracerConstants {

  private final String name;


  private TracerConstants(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  public static final TracerConstants SWING = new TracerConstants("SWING");
  public static final TracerConstants HTTP = new TracerConstants("HTTP");
  public static final TracerConstants SYSTEM = new TracerConstants("SYSTEM");
  public static final TracerConstants FILE = new TracerConstants("FILE");
}
