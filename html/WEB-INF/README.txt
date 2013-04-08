Customize layout:



Customize labels:
If you are starting from "Pupunzi"'s model, search for "Pupunzi" in labels and replace with your links and texts.

--------------------------------------------------------------

 //String apikey = "b2b8fcbd0092cb5613242342342384d48b901f4-us6";
 //String listId = "d22323203ad6";
 //String mailingServerDomain = "us6.api.mailchimp.com";

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

