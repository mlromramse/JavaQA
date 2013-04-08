<%@ page
        import="com.QA.QAOperator, com.QA.waf.QALoginAction, org.jblooming.persistence.exceptions.FindException" %>
<%
  QALoginAction.badgeCheckers.clear();

  QALoginAction.badgeCheckers.add(new QALoginAction.BadgeChecker() {

    String badge = "GOLD";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        try {
          if (operator.getAcceptedAnswers().size()>10) {
            operator.getBadges().add(badge);
            ret = badge;
          }
        } catch (FindException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
      }
      return ret;
    }

    public long scoreGainedOrLost() {
      return 30;
    }


  });

  QALoginAction.badgeCheckers.add(new QALoginAction.BadgeChecker() {

    String badge = "SILVER";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        try {
          if (operator.getAcceptedAnswers().size()>5) {
            operator.getBadges().add(badge);
            ret = badge;
          }
        } catch (FindException e) {
          e.printStackTrace();
        }
      }
      return ret;
    }

    public long scoreGainedOrLost() {
      return 20;
    }


  });

  QALoginAction.badgeCheckers.add(new QALoginAction.BadgeChecker() {

    String badge = "BRONZE";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        try {
          if (operator.getAcceptedAnswers().size()>2) {
            operator.getBadges().add(badge);
            ret = badge;
          }
        } catch (FindException e) {
          e.printStackTrace();
        }
      }
      return ret;
    }

    public long scoreGainedOrLost() {
      return 10;
    }

  });

  %><%--
  // filled name, surname and Skype: Autobiographer
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {
    String badge = "AUTOBIOGRAPHER";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;
      if (!operator.getBadges().contains(badge)) {
        if (JSP.ex(operator.getName(), operator.getEmail(), operator.getOption("SKYPE_NAME"))) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }
      return ret;
    }

    public long scoreGainedOrLost() {
      return 10;
    }


  });


  // Manic logger: fully worklogged last 30 working days
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "MANIC_LOGGER";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        Query query = new OqlQuery("select count(s) from " + Strip.class.getName() + " as s where s.type='SYSTEM_WORKLOG' and s.owner=:ow").getQuery();
        query.setEntity("ow", operator);
        long c = (Long) query.uniqueResult();

        query = new OqlQuery("select count(s) from " + Strip.class.getName() + " as s where s.type='WORKLOG' and s.owner=:ow").getQuery();
        query.setEntity("ow", operator);
        query.setMaxResults(1);
        long w = (Long) query.uniqueResult();

        if (c == 0 && w > 0) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 20;
    }

  });



  //did import from Delicious: 
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "PRISON_ESCAPER";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;
      if (!operator.getBadges().contains(badge)) {
        if (JSP.ex(operator.getOption("MAX_IMPORTED_FROM_DEL"))) {
          int maxImported = Integer.parseInt(operator.getOption("MAX_IMPORTED_FROM_DEL"));
          if (maxImported > 10) {
            operator.getBadges().add(badge);
            ret = badge;
          }
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 10;
    }


  });

  //badge: taxonomist: you created more than 20 tags each used > than 3 times
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "TAXONOMIST";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        String hql = "select distinct tag.tag from " + Strip.class.getName() + " as strip join strip.tags as tag where tag.owner = :ow group by tag.tag having count(tag.tag)>=3 order by count(tag.tag) desc";

        Query query = new OqlQuery(hql).getQuery();
        query.setEntity("ow", operator);
        long w = query.list().size();

        if (w > 20) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 20;
    }


  });

  //community founder: invited more than 10 people
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "COMMUNITY_FOUNDER";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        if (operator.getInvitedUsersIds() != null && operator.getInvitedUsersIds().size() > 10) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 50;
    }

  });

  //chaos generator: less than 2 strips per team || more tags than strips
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "CHAOS_GENERATOR";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        long totStrips = operator.getNumberOfStrips(true);

        String hql = "select count(team) from " + Team.class.getName() + " as team where team.owner=:op";
        Query query = new OqlQuery(hql).getQuery();
        query.setEntity("op", operator);
        long totTeams = (Long) query.uniqueResult();

        hql = "select count(tag) from " + Tag.class.getName() + " as tag where tag.owner=:op";
        query = new OqlQuery(hql).getQuery();
        query.setEntity("op", operator);
        long totTags = (Long) query.uniqueResult();

        if (totStrips > 10 && (totTags > (totStrips * 2) || totTeams > (totStrips * 2))) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return -10;
    }


  });

  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "MASTER_OF_CONTROL";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        long totStrips = operator.getNumberOfStrips(true);

        String hql = "select count(team) from " + Team.class.getName() + " as team where team.owner=:op";
        Query query = new OqlQuery(hql).getQuery();
        query.setEntity("op", operator);
        long totTeams = (Long) query.uniqueResult();

        hql = "select count(tag) from " + Tag.class.getName() + " as tag where tag.owner=:op";
        query = new OqlQuery(hql).getQuery();
        query.setEntity("op", operator);
        long totTags = (Long) query.uniqueResult();

        if (totStrips > 1000 && (totTags < 80 || totTeams < (totStrips / 15))) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 20;
    }


  });


  //hyperactive
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "HYPERACTIVE";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        long totStripsDone = operator.getTotalTodoClosed(false);
        long totStripsToBeDone = operator.getTotalTodoOpen(false);

        if (totStripsToBeDone > 20 && totStripsToBeDone > (10 * totStripsDone)) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 20;
    }


  });



  // brat: (finger) or (mooning) in > than one strip
  // Manic logger: fully worklogged last 30 working days
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "BRAT";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        String oql = "select count(st) from " + Strip.class.getName() + " as st where" +
                " st.owner=:ow and ( (st.title like :finger or st.content like :finger) or (st.title like :mooning   or st.content like :mooning) )";
        OqlQuery query = new OqlQuery(oql);

        query.getQuery().setEntity("ow", operator);
        query.getQuery().setString("finger", "%(finger)%");
        query.getQuery().setString("mooning", "%(mooning)%");

        long c = (Long) query.getQuery().uniqueResult();

        if (c > 1) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return -20;
    }


  });

  //first usage of read it later
  MpLoginAction.badgeCheckers.add(new MpLoginAction.BadgeChecker() {

    String badge = "KEEPFOCUS";

    public String badgeName() {
      return badge;
    }

    public String check(QAOperator operator) {
      String ret = null;

      if (!operator.getBadges().contains(badge)) {

        String hql = "select count(tag) from " + Tag.class.getName() + " as tag where tag.owner=:op and tag.tag = 'remindMeLater'";
        Query query = new OqlQuery(hql).getQuery();
        query.setEntity("op", operator);
        long c = (Long) query.uniqueResult();

        if (c > 0) {
          operator.getBadges().add(badge);
          ret = badge;
        }
      }

      return ret;
    }

    public long scoreGainedOrLost() {
      return 2;
    }


  });



--%>