package org.jblooming.utilities.file.fileStorage;

import java.io.File;
import java.util.Comparator;

/**
 * @author Pietro Polsinelli ppolsinelli@twproject.com
 */
public class FileComp implements Comparator {
  int mode;

  /**
   * mode sort by 1=Filename, 2=Size, 3=Date, 4=Type
   * The default sorting method is by Name
   */
  public FileComp() {
    this.mode = 1;
  }

  public FileComp(int mode) {
    this.mode = mode;
  }

  public int compare(Object o1, Object o2) {
    File f1 = (File) o1;
    File f2 = (File) o2;
    if (f1.isDirectory()) {
      if (f2.isDirectory()) {
        switch (mode) {
          //Filename
          case 1:
            return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
            //Filesize
          case 2:
            return new Long(f1.length()).compareTo(new Long(f2.length()));
            //Date
          case 3:
            return new Long(f1.lastModified()).compareTo(new Long(f2.lastModified()));
            //Type
          case 4:
            return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
          default:
            return 1;
        }
      } else
        return -1;
    } else if (f2.isDirectory())
      return 1;
    else {
      switch (mode) {
        case 1:
          return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
        case 2:
          return new Long(f1.length()).compareTo(new Long(f2.length()));
        case 3:
          return new Long(f1.lastModified()).compareTo(new Long(f2.lastModified()));
        case 4:
          { // Sort by extension
            int tempIndexf1 = f1.getAbsolutePath().lastIndexOf('.');
            int tempIndexf2 = f2.getAbsolutePath().lastIndexOf('.');
            if ((tempIndexf1 == -1) && (tempIndexf2 == -1)) { // Neither have an extension
              return f1.getAbsolutePath().toUpperCase().compareTo(f2.getAbsolutePath().toUpperCase());
            }
            // f1 has no extension
            else if (tempIndexf1 == -1)
              return -1;
            // f2 has no extension
            else if (tempIndexf2 == -1)
              return 1;
            // Both have an extension
            else {
              String tempEndf1 = f1.getAbsolutePath().toUpperCase().substring(tempIndexf1);
              String tempEndf2 = f2.getAbsolutePath().toUpperCase().substring(tempIndexf2);
              return tempEndf1.compareTo(tempEndf2);
            }
          }
        default:
          return 1;
      }
    }
  }
}

