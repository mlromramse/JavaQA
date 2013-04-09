This is a Q&A app in Java.
Examples:
http://jquery.pupunzi.com/

Setup:
This is a standard Java web app running on Tomcat6+, JDK6+ and MySQL.
Actually if you know your way around JDBC drivers, this is a platform on Hibernate,
so the Q&A runs on almost any relational database (PostgreSQL, Oracle, SQL Server, ...).

At setup end, the default operator is "administrator" with empty password.

Customize labels:

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
- Customize labels:
-
#ALWAYS,NEVER,MAYBE
RECAPTCHA_QUESTION=MAYBE

#yes, no
QUESTION_GRACE=no

#TAGS USAGE CUTOFF
QUESTION_TAG_CUTOFF=1



