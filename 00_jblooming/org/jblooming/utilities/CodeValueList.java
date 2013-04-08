package org.jblooming.utilities;

import org.jblooming.ontology.Identifiable;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import java.util.*;

/**
 *
 */
public class CodeValueList {

  public LinkedList<CodeValue> codeValues = new LinkedList<CodeValue>();

  public static CodeValueList getI18nInstance(Collection<String> i18nCodes, PageState pageState) {
    CodeValueList cvl = new CodeValueList();
    for (String i18nCode : i18nCodes) {
      cvl.add(i18nCode,I18n.get(i18nCode));
    }
    return cvl;
  }

  public static CodeValueList getI18nInstance(PageState pageState, String... i18nCodes) {
    CodeValueList cvl = new CodeValueList();
    for (String i18nCode : i18nCodes) {
      cvl.add(i18nCode, I18n.get(i18nCode));
    }
    return cvl;
  }


  public static CodeValueList getI18nInstanceForIdentifiables(Collection<? extends Identifiable> entities, PageState pageState) {
    CodeValueList cvl = new CodeValueList();
    for (Identifiable i : entities) {
      cvl.add(i.getId().toString(),I18n.get(i.getName()));
    }
    return cvl;
  }

  public static CodeValueList getInstanceForIdentifiables(Collection<? extends Identifiable> entities) {
    CodeValueList cvl = new CodeValueList();
    for (Identifiable i : entities) {
      cvl.add(i.getId().toString(),i.getName());
    }
    return cvl;
  }

  public CodeValueList() {

  }

  public CodeValueList(Collection strings) {
    for (Iterator iterator = strings.iterator(); iterator.hasNext();) {
      String s = (String) iterator.next();
      add(s);
    }
  }

   public CodeValueList(String... strings) {
     for (String string : strings) {
       add(string);
     }
   }

  public LinkedList<CodeValue> getList(){
    return codeValues;
  }

  public void add(CodeValue cv) {

    codeValues.add(cv);
  }

  public void add(String code, String value) {

    codeValues.add(new CodeValue(code, value));
  }

  public void add(String code) {
    add(code, code);
  }

    public void addIfNew(String code) {
      boolean cont = false;
      for (CodeValue cv : codeValues) {
        if (cv.code.equals(code)) {
          cont = true;
          break;
        }
      }
      if (!cont)
        add(code, code);
    }

  public void addAsFirst(String code, String value) {
    codeValues.addFirst(new CodeValue(code, value));
  }

  public Iterator<CodeValue> iterator() {
    return codeValues.iterator();
  }

  public void clear() {
    codeValues.clear();
  }

  public int size() {
    return codeValues.size();
  }

  public String get(String value) {
    for (int i = 0; i < codeValues.size(); i++) {
      CodeValue codeValue = (CodeValue) codeValues.get(i);
      if (codeValue.code.equals(value))
        return codeValue.value;
    }
    return "";
  }

  public Set keySet() {
    Set keys = new HashSet();
    Iterator i = iterator();
    while (i.hasNext()) {
      CodeValue codeValue = (CodeValue) i.next();
      keys.add(codeValue.code);
    }
    return keys;
  }

  public void addChoose(PageState pageState) {
    this.addAsFirst("","- "+I18n.get("EDITOR_CHOOSE")+" -");
  }

  public void addAll(CodeValueList additionalComboEntries) {
    codeValues.addAll(additionalComboEntries.codeValues);
  }


  public void sort(){
    Collections.sort(codeValues,new Comparator<CodeValue>(){ public int compare(CodeValue a,CodeValue b){
      return a.value.compareTo(b.value);
  }});
  }

  
}
