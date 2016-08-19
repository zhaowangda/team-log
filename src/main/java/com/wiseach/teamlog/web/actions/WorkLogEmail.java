package com.wiseach.teamlog.web.actions;


import com.mysql.jdbc.StringUtils;
import com.wiseach.teamlog.Constants;
import com.wiseach.teamlog.db.CommonDBHelper;
import com.wiseach.teamlog.db.UserAuthDBHelper;
import com.wiseach.teamlog.db.WorkLogDBHelper;
import com.wiseach.teamlog.model.User;
import com.wiseach.teamlog.utils.DateUtils;
import com.wiseach.teamlog.utils.EmailSender;
import com.wiseach.teamlog.utils.TeamlogUtils;
import com.wiseach.teamlog.web.resolutions.JsonResolution;
import com.wiseach.teamlog.web.security.UserAuthProcessor;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.util.StringUtil;
import org.joda.time.DateTime;

import javax.mail.internet.InternetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wilson on 16/6/28.
 */

@UrlBinding("/worklog/email/{period}/{people}/{email}")
public class WorkLogEmail  extends BaseActionBean {
    public String period, people,email;

    @DefaultHandler
    public Resolution view() {
        String message = TeamlogUtils.checkWorklogConditions(period, people);
        int i =1;
        if (StringUtils.isNullOrEmpty(message)) {
            final Map<String, DateTime> datePeriod = TeamlogUtils.getSplitDate(period);
            //final List<Long> userIdList = TeamlogUtils.minusList(TeamlogUtils.getSplitId(people), WorkLogDBHelper.getSharedWithMe(getUserId()));

            final List<Map<String, Object>> worklogs = WorkLogDBHelper.getWorkLogEmailData(datePeriod.get("start").toDate(), datePeriod.get("end").toDate());
            if (worklogs.size() > 0) {
                StringBuilder EmailMSG=new StringBuilder("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                        "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> <title></title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> </head><body>"+
                        "<table border='1px' cellpadding='0' cellspacing='0'>");
                String Temp = "";
                for (Map<String, Object> worklog : worklogs) {
                    if (Temp.equals("")){ //刚开始添加
                        Temp = worklog.get("staff").toString().trim();
                        EmailMSG.append("\t<tr>\n" +
                                "\t\t<td colspan=\"7\" align=\"center\" valign=\"middle\">" + Temp + "工作日报</td>\n" +
                                "\t</tr>\n"
                        );
                        EmailMSG.append("\t<tr>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">序列</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">分类</td>\n" +
                                "\t\t<td>工作内容</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">完成度</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">工时</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">评分</td>\n" +
                                "\t\t<td>备注</td>\n" +
                                "\t</tr>");
                    }

                    if ( !worklog.get("staff").toString().trim().equals(Temp) ){
                        //System.out.println(" NOT Temp is:"+ Temp +"; Staff is:"+ worklog.get("staff").toString().trim());
                        EmailMSG.append("</table><br><br><table border='1px' cellpadding='0' cellspacing='0'>\n" +
                                "\t<tr>\n" +
                                "\t\t<td colspan=\"7\" align=\"center\" valign=\"middle\" >" + worklog.get("staff").toString().trim() + "工作日报</td>\n" +
                                "\t</tr>"
                        );
                        EmailMSG.append("\t<tr>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">序列</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">分类</td>\n" +
                                "\t\t<td>工作内容</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">完成度</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">工时</td>\n" +
                                "\t\t<td align=\"center\" valign=\"middle\">评分</td>\n" +
                                "\t\t<td>备注</td>\n" +
                                "\t</tr>");
                        i=1;
                    }

                    EmailMSG.append("\t<tr>\n" +
                            "\t\t<td align=\"center\" valign=\"middle\">"+ i +"</td>\n" +
                            "\t\t<td align=\"center\" valign=\"middle\">"+worklog.get("tag")+"</td>\n" +
                            "\t\t<td>"+worklog.get("description")+"</td>\n" +
                            "\t\t<td align=\"center\" valign=\"middle\">"+ worklog.get("completion")+"%</td>\n" +
                            "\t\t<td align=\"center\" valign=\"middle\">"+ worklog.get("hours")+ "h</td>\n" +
                            "\t\t<td align=\"center\" valign=\"middle\">"+ worklog.get("nice")+ "</td>\n" +
                            "\t\t<td>"+ worklog.get("comments")+ "</td>\n" +
                            "\t</tr>");
                    i++;
                    Temp=worklog.get("staff").toString().trim();

                }
                EmailMSG.append("</table></body></html>");

                final List<InternetAddress> Emails = TeamlogUtils.getSplitEmail(email);
                EmailSender.send(Emails,"信息安全部日报 ("+period+")",EmailMSG.toString());

                //System.out.print(EmailMSG);
                setRequestAttribute(ERROR_DESCRIPTION_KEY, EmailMSG);
                return ViewHelper.getStandardErrorBoxResolution();
            } else {
                setRequestAttribute(ERROR_DESCRIPTION_KEY, message);
                return ViewHelper.getStandardErrorBoxResolution();
            }

        }
        setRequestAttribute(ERROR_DESCRIPTION_KEY, message);
        return ViewHelper.getStandardErrorBoxResolution();
    }
}
