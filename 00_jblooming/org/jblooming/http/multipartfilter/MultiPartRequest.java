package org.jblooming.http.multipartfilter;

import org.jblooming.http.ZipServe;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.Fields;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MultiPartRequest {

  public static final String __ContentType = "Content-Type";

  HttpServletRequest _request;
  LineInput _in;
  String _boundary;
  byte[] _byteBoundary;
  int boundaryLength;
  Hashtable _partMap = new Hashtable(10);
  int _char = -2;
  boolean _lastPart = false;
  boolean store_as_file = true;
  long upload_max_size = Long.MAX_VALUE;

  int peekOffset = 0;
  int peekLength = 0;
  byte[] peek;

  private SessionState sm = null;
  boolean debug = false;
  long startTime;

  protected void assertMaxValueNotReached(int bytes) throws MultipartFormSizeException {
    upload_max_size -= bytes;
    if (upload_max_size <= 0)
      throw new MultipartFormSizeException("Form length limit reached");
  }


  /**
   * @param request The request containing a multipart/form-data
   *                request
   * @throws IOException IOException
   */
  public MultiPartRequest(HttpServletRequest request, boolean store_as_file, boolean limit_size) throws IOException, ServletException {

    this.store_as_file = store_as_file;
    _request = request;
    String content_type = request.getHeader(__ContentType);
    if (!content_type.startsWith("multipart/form-data"))
      throw new IOException("Not multipart/form-data request");
    long max_size = upload_max_size;
    //if (Settings.getApplicationSetting("uploadMax")!=null)  max_size = Long.parseLong((String)Settings.getApplicationSetting("uploadMax"));

    long size = request.getContentLength();

    if (debug) Tracer.addTrace("size: " + Math.ceil(size / 1024));


    if (limit_size && size >= 0 && size > max_size)
      throw new MultipartFormSizeException("Form size (" + request.getContentLength() + " bytes)" +
              " exceeds configured 'uploadMax' length (" + max_size + ')');
    else if (limit_size)
      upload_max_size = max_size;
    else
      upload_max_size = Long.MAX_VALUE;

    if (debug) Tracer.addTrace("Multipart content type = " + content_type);

    _in = new LineInput(request.getInputStream());

    // Extract boundary string
    _boundary = "--" +
            value(content_type.substring(content_type.indexOf("boundary=")));

    if (debug) Tracer.addTrace("Boundary=" + _boundary);

    _byteBoundary = (_boundary + "--").getBytes();

    boundaryLength = _boundary.length();

    peekOffset = 0;
    peekLength = 0;

    peek = new byte[_byteBoundary.length * 2 + 10];//original size was _byteBoundary.length+5

    loadAllParts(request, size);
  }

  /* ------------------------------------------------------------ */
  /**
   * Get the part names.
   *
   * @return an array of part names
   */
  public String[] getPartNames() {
    return (String[]) _partMap.keySet().toArray(new String[0]);
  }

  /* ------------------------------------------------------------ */
  /**
   * Check if a named part is present
   *
   * @param name The part
   * @return true if it was included
   */
  public boolean contains(String name) {
    Part part = (Part) _partMap.get(name);
    return (part != null);
  }

  /* ------------------------------------------------------------ */
  /**
   * Get the data of a part as a string.
   *
   * @param name The part name
   * @return The part data
   */
  public String getString(String name) throws IOException {
    Part part = (Part) _partMap.get(name);
    if (part == null)
      return null;
    return part.getData();
  }

  public Iterator getValues(String name) {
    Part part = (Part) _partMap.get(name);
    if (part == null)
      return Collections.EMPTY_LIST.iterator();
    else
      return part.iterator();
  }

  /* ------------------------------------------------------------ */
  /**
   * Get the data of a part as a stream.
   *
   * @param name The part name
   * @return Stream providing the part data
   */
  public InputStream getInputStream(String name) throws IOException {
    Part part = (Part) _partMap.get(name);
    if (part == null)
      return null;
    return part.getStream();//new ByteArrayInputStream(part._data);
  }
  /* ------------------------------------------------------------ */

  /* ------------------------------------------------------------ */

  /**
   * Get the MIME parameters associated with a part.
   *
   * @param name The part name
   * @return Hashtable of parameters
   */
  public Hashtable getParams(String name) {
    Part part = (Part) _partMap.get(name);
    if (part == null)
      return null;
    return part._headers;
  }

  /* ------------------------------------------------------------ */
  /**
   * Get any file name associated with a part.
   *
   * @param name The part name
   * @return The filename
   */
  public String getFilename(String name) {
    Part part = (Part) _partMap.get(name);
    if (part == null)
      return null;
    return part._filename;
  }

  protected String readLine() throws IOException, ServletException {
    String line = _in.readLine();
    if (line != null)
      assertMaxValueNotReached(line.length());
    return line;
  }


  /* ------------------------------------------------------------ */
  private void loadAllParts(HttpServletRequest request, long size)
          throws IOException, ServletException {
    // Get first boundary
    String line = readLine();
    if (!line.equals(_boundary)) {
      if (debug) Tracer.addTrace(line);
      throw new IOException("Missing initial multi part boundary");
    }

    // Read each part
    while (!_lastPart) {
      // Read Part headers
      Part part = new Part();

      String content_disposition = null;
      while ((line = readLine()) != null) {
        // If blank line, end of part headers
        if (line.length() == 0)
          break;

        if (debug) Tracer.addTrace("LINE=" + line);

        // place part header key and value in map
        int c = line.indexOf(':', 0);
        if (c > 0) {
          String key = line.substring(0, c).trim().toLowerCase();
          String value = line.substring(c + 1, line.length()).trim();
          String ev = (String) part._headers.get(key);
          part._headers.put(key, (ev != null) ? (ev + ';' + value) : value);
          if (debug) Tracer.addTrace(key + ": " + value);
          if (key.equals("content-disposition"))
            content_disposition = value;
        }
      }

      // Extract content-disposition
      boolean form_data = false;
      if (content_disposition == null) {
        throw new IOException("Missing content-disposition");
      }

      StringTokenizer tok =
              new StringTokenizer(content_disposition, ";");
      while (tok.hasMoreTokens()) {
        String t = tok.nextToken().trim();
        String tl = t.toLowerCase();
        if (t.startsWith("form-data"))
          form_data = true;
        else if (tl.startsWith("name="))
          part._name = value(t);
        else if (tl.startsWith("filename="))
          part._filename = value(t);
      }

      // Check disposition
      if (!form_data) {
        if (debug) Tracer.addTrace("Non form-data part in multipart/form-data");
        continue;
      }
      if (part._name == null || part._name.length() == 0) {
        if (debug) Tracer.addTrace("Part with no name in multipart/form-data");
        continue;
      }
      if (debug) Tracer.addTrace("name=" + part._name);
      if (debug) Tracer.addTrace("filename=" + part._filename);
      Part oldValue = (Part) _partMap.get(part._name);
      if (oldValue == null) {
        _partMap.put(part._name, part);
      } else {
        while (oldValue._next != null)
          oldValue = oldValue._next;
        oldValue._next = part;
      }
      readBytes(part, request, size);
    }
  }

  protected File createTempFileObject() throws IOException {
    return File.createTempFile("formTW_TEMP_FILE", ".tmp", ZipServe.getTempFolder(_request));
  }
  /*--------------------------------------------------------------*/

  protected void resinDump(OutputStream output, HttpServletRequest request, long size) throws IOException, ServletException {

    int b = -1;

    //boolean retval = false;

    //int i = 0;
    // Need the last peek or would miss the initial '\n'
    while (peekOffset + 1 < peekLength) {
      output.write(peek[peekOffset++]);
      updateCounter(request, size);

    }

    while ((b = peekByte()) >= 0) {
      boolean hasCr = false;

      if (b == '\r') {
        hasCr = true;
        b = peekByte();

        // XXX: Macintosh?
        if (b != '\n') {
          output.write((byte) '\r');
          updateCounter(request, size);
          peek[0] = (byte) b;
          peekOffset = 0;
          peekLength = 1;
          continue;
        }
      } else if (b != '\n') {
        output.write((byte) b);
        updateCounter(request, size);
        continue;
      }

      int j;
      for (j = 0;
           j < boundaryLength && (b = peekByte()) >= 0 && _byteBoundary[j] == b;
           j++) {
      }

      if (j == boundaryLength) {
        _lastPart = false;
        if ((b = peekByte()) == '-')
          if ((b = peekByte()) == '-')
            _lastPart = true;
        for (; b > 0 && b != '\r' && b != '\n'; b = peekByte()) {
        }
        if (b == '\r' && (b = peekByte()) != '\n') {
          peek[0] = (byte) b;
          peekOffset = 0;
          peekLength = 1;
        }


        long endTime = System.currentTimeMillis();
        if (debug) Tracer.addTrace("MultiPartRequest::end:" + endTime);
        if (debug) Tracer.addTrace("MultiPartRequest::elapsed:" + ((endTime - startTime) / 1000));
        return;
      }

      peekLength = 0;
      if (hasCr) {
        output.write((byte) '\r');
        updateCounter(request, size);
        output.write((byte) '\n');
        updateCounter(request, size);
      } else if (hasCr) {
        output.write((byte) '\r');
        updateCounter(request, size);
        peek[peekLength++] = (byte) '\n';
      } else {
        output.write((byte) '\n');
        updateCounter(request, size);
      }

      int k = 0;
      while (k < j) {
        output.write(_byteBoundary[k++]);
        updateCounter(request, size);
      }

      while (k < j)
        peek[peekLength++] = _byteBoundary[k++];


      peek[peekLength++] = (byte) b;
      peekOffset = 0;
    }

    _lastPart = (b < 0);

  }

  /**
   * Read the next byte from the peek or from the underlying stream.
   */
  private int peekByte() throws IOException, ServletException {
    if (peekOffset < peekLength) {
      return peek[peekOffset++] & 0xff;
    } else {
      int b = _in.read();
      if (b >= 0)
        assertMaxValueNotReached(1);
      return b;
    }
  }

  private void readBytes(Part part, HttpServletRequest request, long size) throws IOException, ServletException {
    OutputStream outputStream;
    boolean isFile = store_as_file && part._filename != null;
    if (isFile) {
      File temporaryFile = createTempFileObject();
      outputStream = new FileOutputStream(temporaryFile);
      part.temporaryFile = temporaryFile;
      part._data = null;
    } else {
      outputStream = new ByteArrayOutputStream();
      part._data = null;
      part.temporaryFile = null;
    }

    resinDump(outputStream, request, size);

    if (!isFile)
      part._data = ((ByteArrayOutputStream) outputStream).toByteArray();
    outputStream.flush();
    outputStream.close();
  }


  protected void deleteTemporaryFiles() {
    // try to delete temporary files , on error mark them as Deletable on VM exit
    Iterator fi = _partMap.values().iterator();
    while (fi.hasNext()) {
      for (Part part = (Part) fi.next(); part != null; part = part._next) {
        File file = part.temporaryFile;
        if (file != null && file.exists() && !FileUtilities.tryHardToDeleteFile(file))
          file.deleteOnExit();
      }
    }
  }

  /* ------------------------------------------------------------ */
  private String value(String nameEqualsValue) {
    String value =
            nameEqualsValue.substring(nameEqualsValue.indexOf('=') + 1).trim();

    int i = value.indexOf(';');
    if (i > 0)
      value = value.substring(0, i);
    if (value.startsWith("\"")) {
      value = value.substring(1, value.indexOf('"', 1));
    } else {
      i = value.indexOf(' ');
      if (i > 0)
        value = value.substring(0, i);
    }
    return value;
  }

  /* ------------------------------------------------------------ */
  protected String fileToString(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    //PENDING long to int conversion can cause problems with multi-giga files
    ByteArrayOutputStream output = new ByteArrayOutputStream((int) file.length());
    for (int b = input.read(); b > 0; b = input.read())
      output.write(b);
    byte[] data = output.toByteArray();
    return new String(data);
  }

  protected static final InputStream EMPTY_STREAM = new InputStream() {
    public int read() {
      return -1;
    }
  };

  public class Part {
    String _name = null;

    public String getName() {
      return _name;
    }

    public String getFilename() {
      return _filename;
    }

    public Hashtable getHeaders() {
      return _headers;
    }

    public String getTemporaryFile() {
      if (temporaryFile == null || !temporaryFile.exists() || !temporaryFile.isFile())
        return null;
      return temporaryFile.getAbsolutePath();
    }

    String _filename = null;
    Hashtable _headers = new Hashtable(10);
    byte[] _data = null;
    File temporaryFile = null;
    Part _next;

    public String getData() throws IOException {
      if (_data != null)
        return new String(_data);
      else if (temporaryFile != null && temporaryFile.exists() && temporaryFile.isFile())
        return fileToString(temporaryFile);
      else
        return null;
    }

    public InputStream getStream() throws IOException {
      if (_data != null)
        return new ByteArrayInputStream(_data);
      else if (temporaryFile != null && temporaryFile.exists() && temporaryFile.isFile())
        return new FileInputStream(temporaryFile);
      else
        return EMPTY_STREAM;
    }

    class PartIterator implements Iterator {
      private Part part;

      private PartIterator(Part part) {
        this.part = part;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public boolean hasNext() {
        return part != null;
      }

      public Object next() {
        Part nextPart = part;
        part = part._next;
        return nextPart;
      }
    }

    Iterator iterator() {
      return new PartIterator(this);
    }
  }

  public void finalize() throws Throwable {
    try {
      deleteTemporaryFiles();
    } finally {
      super.finalize();
    }
  }

  private void updateCounter(HttpServletRequest request, long size) {

    if (sm == null) {
      sm = (SessionState) request.getSession(true).getAttribute(Fields.SESSION);
      startTime = System.currentTimeMillis();
      if (debug) Tracer.addTrace("start:" + startTime);
    }
  }

}
