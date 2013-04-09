JavaQA
======

This is a Q&A app in Java. The web interface is adaptive - try it from mobile!
Examples:
http://jquery.pupunzi.com/

#Setup
This is a standard Java web app running on Tomcat6+, JDK6+ and MySQL.
Actually if you know your way around JDBC drivers, this is a platform on Hibernate,
so the Q&A runs on almost any relational database (PostgreSQL, Oracle, SQL Server, ...).

1. Publish the webapp in Tomcat

2. Create an empty database

3. Setup database access data in WEB-INF/config.properties:
url=jdbc:mysql://YOUR_DB_SERVER/YOUR_EMPTY_DATABASE?useUnicode=true&characterEncoding=UTF-8
user=root
password=whatever

4. Rename the file admin.rename in commons/administration to admin.jsp

5. Start Tomcat

6. Go via browser to the url http://yourserver/commons/administration/admin.jsp

7. Put as password the adminPassword as from  WEB-INF/config.properties, and select "update db schema" -> database will be created

8. Restart Tomcat

At setup end, the default operator is "administrator" with empty password. Create at least one question with label "meta" to enable the "meta" section.

In production, change config.properties with:
1. development=no
2. logOnConsole=no
3. logOnFile=yes

Set error levels to ERROR.

#Customize labels:

Enter the web app ad administrator, go to settings -> label section.
If you are starting from "Pupunzi"'s model, search for "Pupunzi" in labels and replace with your links and texts.

Here are some labels that you should change:

#LEFT COLUMN BOXES

QA_BOX1_TITLE
QA_BOX1_BODYTEXT

QA_BOX2_TITLE
QA_BOX2_BODYTEXT

QA_PROMO_BOX

QA_REFERENCE
QA_REF_SITE

#GOOGLE TRACKING CODE
QA_GOOGLE_CODE

--------------------------------------------------------------
  String mailingServerDomain = I18n.g("QA_MAILING_DOMAIN");
  String apikey = I18n.g("QA_MAILING_APIKEY");
  String listId = I18n.g("QA_MAILING_LISTID");

--------------------------------------------------------------

- META: you need the first tag created to be "meta" tag.

#ENABLING RECAPTCHA: ALWAYS,NEVER,MAYBE
RECAPTCHA_QUESTION=MAYBE

#ENABLING TAG CREATION: yes, no
QUESTION_GRACE=no

#TAGS USAGE CUTOFF
QUESTION_TAG_CUTOFF=1



