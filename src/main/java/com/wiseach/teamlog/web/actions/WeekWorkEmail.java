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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Created by wilson on 16/6/28.
 */

@UrlBinding("/weekwork/email/{period}/{people}/{email}")
public class WeekWorkEmail  extends BaseActionBean {
    public String period, people,email;

    @DefaultHandler
    public Resolution view() {
        String message = TeamlogUtils.checkWorklogConditions(period, people);
        int i =1;
        if (StringUtils.isNullOrEmpty(message)) {
            System.err.println("period11111 is:"+period);
            period = convertWeekByDate(period);
            System.err.println("22222 is:"+period);
            final Map<String, DateTime> datePeriod = TeamlogUtils.getSplitDate(period);
            //final List<Long> userIdList = TeamlogUtils.minusList(TeamlogUtils.getSplitId(people), WorkLogDBHelper.getSharedWithMe(getUserId()));

            final List<Map<String, Object>> worklogs = WorkLogDBHelper.getWorkLogEmailData(datePeriod.get("start").toDate(), datePeriod.get("end").toDate());
            if (worklogs.size() > 0) {
                StringBuilder EmailMSG=new StringBuilder("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                        "<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> <title></title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> </head><body>");

                String EmailTail = "    </tbody>\n" + "</table>\n" + "<br>\n" + "<br>";
                String tmpName="hhhhhhhhhh";
                String tmpnext="1";
                int  Noindex= 1 ;
                int  Nextindex= 0 ;
                for (Map<String, Object> worklog : worklogs) {

                    //邮件标题 title
                    if (!tmpName.equals(worklog.get("staff").toString().trim())){ //刚开始添加
                        if(tmpName != "hhhhhhhhhh"){
                            //结束Table,第一次不添加
                            EmailMSG.append(EmailTail);
                            Noindex= 1 ;
                        }

                        EmailMSG.append(EmailTitle(worklog.get("staff").toString().trim(),period));
                        EmailMSG.append(EmailCon());
                    }

                    //
                    if(!isWeekend(worklog.get("Time").toString().trim())){
                        //本周工作内容 body
                        String EmailworkBody = EmailBody(Noindex, 0, worklog.get("Time").toString().trim(), worklog.get("Tag").toString().trim(), worklog.get("description").toString().trim(),
                                worklog.get("completion").toString().trim(),worklog.get("hours").toString().trim(),worklog.get("nice").toString().trim(),
                                worklog.get("comments").toString().trim()
                        );
                        EmailMSG.append(EmailworkBody);
                    }else {
                        //下周工作计划
                        //如果姓名与之前相同,则添加过,不同则代表没有添加
                        if (!tmpnext.equals(worklog.get("staff").toString().trim())){
                            Nextindex= 0 ;
                            EmailMSG.append(EmailNextWorker());
                        }
                        tmpnext = worklog.get("staff").toString().trim();
                        Nextindex++;
                        EmailMSG.append(EmailNext(Nextindex,worklog.get("description").toString().trim()));
                    }

                    tmpName = worklog.get("staff").toString().trim();
                    Noindex++;
                }
                EmailMSG.append("</body></html>");

                final List<InternetAddress> Emails = TeamlogUtils.getSplitEmail(email);
                EmailSender.send(Emails,"信息安全部周报 ("+period+")",EmailMSG.toString());

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

    private String EmailTitle(String Name,String period){
        return String.format("<br><br><table cellspacing=\"0\" cellpadding=\"0\" style=\"border-collapse: collapse\">\n" + "    <tbody>" + "   <tr>\n" +
                "        <td colspan=\"8\" valign=\"middle\" style=\"width: 507.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #000000 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">" +
                "%s 工作周报（</font><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">）</font></p>\n" +
                "        </td>\n" +
                "    </tr>",Name,period);
    }

    private String EmailCon(){
        return String.format("    <tr>\n" +
                "        <td valign=\"middle\" style=\"width: 46.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">序列</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 70.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">日期</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 38.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">分类</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 177.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p style=\"margin: 0.0px 0.0px 0.0px 0.0px\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">工作内容</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 53.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">完成度</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 45.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">工时</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 42.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">评分</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 29.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">备注</font></p>\n" +
                "        </td>\n" +
                "    </tr>");
    }

    private String EmailBody(int index, int TimeRow, String time, String tag, String description, String completion, String hours, String nice, String comments){
        //序列号
        String EmailIndex = String.format(
                        "<tr>\n" +
                        "<td valign=\"middle\" style=\"width: 46.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                        "<p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">" +
                                "%d</font></p>\n" +
                        "</td>", index);

        String EmailTimeRow="" ;
        if(TimeRow != 0){
            //时间
            EmailTimeRow = String.format(
                    "        <td rowspan=\" %d \" valign=\"middle\" style=\"width: 70.0px; height: 35.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                            "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                            "        </td>\n" ,TimeRow,time);
        }else {
            EmailTimeRow = String.format(
                    "        <td  valign=\"middle\" style=\"width: 70.0px; height: 35.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                            "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                            "        </td>\n" ,time);
        }
        //周报内容 分类	工作内容	完成度	工时	评分	备注
        String EmailBody = String.format(
                "        <td valign=\"middle\" style=\"width: 38.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 177.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p style=\"margin: 0.0px 0.0px 0.0px 0.0px\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 53.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 45.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 42.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "        <td valign=\"middle\" style=\"width: 29.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "    </tr>",tag,description,completion,hours,nice,comments);

        return EmailIndex+EmailTimeRow+EmailBody;
    }
    private String EmailNextWorker(){
        return String.format("    <tr>\n" +
                "        <td colspan=\"8\" valign=\"middle\" style=\"width: 499.0px; height: 13.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #000000 #808080 #000000; padding: 4.0px 4.0px 4.0px 4.0px\">\n" +
                "            <p style=\"margin: 0px; font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Helvetica; min-height: 14px;\"><br></p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td colspan=\"8\" valign=\"middle\" style=\"width: 507.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">下周工作计划</font></p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td valign=\"middle\" style=\"width: 46.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">序列</font></p>\n" +
                "        </td>\n" +
                "        <td colspan=\"7\" valign=\"middle\" style=\"width: 460.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p style=\"margin: 0.0px 0.0px 0.0px 0.0px\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">工作内容</font></p>\n" +
                "        </td>\n" +
                "    </tr>");
    }
    private String EmailNext(int index,String todo){
        return String.format("<tr>\n" +
                "        <td valign=\"middle\" style=\"width: 46.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p align=\"center\" style=\"margin: 0.0px 0.0px 0.0px 0.0px; text-align: center\"><font face=\"Times\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: Times; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%d</font></p>\n" +
                "        </td>\n" +
                "        <td colspan=\"7\" valign=\"middle\" style=\"width: 460.0px; height: 17.0px; border-style: solid; border-width: 1.0px 1.0px 1.0px 1.0px; border-color: #808080 #808080 #808080 #808080\">\n" +
                "            <p style=\"margin: 0.0px 0.0px 0.0px 0.0px\"><font face=\"Songti SC\" color=\"#000000\" style=\"font-variant-numeric: normal; font-stretch: normal; font-size: 12px; line-height: normal; font-family: &quot;Songti SC&quot;; font-kerning: none; font-variant-ligatures: common-ligatures; -webkit-text-stroke-color: rgb(0, 0, 0);\">%s</font></p>\n" +
                "        </td>\n" +
                "    </tr> ",index,todo);
    }
    private boolean isWeekend(String  currentDate){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try{
            date=format.parse(currentDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week=cal.get(Calendar.DAY_OF_WEEK)-1;
        if(week ==6 || week==0){//0代表周日，6代表周六
            return true;
        }
        return false;
    }
    private String convertWeekByDate(String inDate) {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Date time = null;
        try{
            time = sdf.parse(inDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        //System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        String imptimeBegin = sdf.format(cal.getTime());
        //System.out.println("所在周星期一的日期："+imptimeBegin);

        cal.add(Calendar.DATE, 6);
        String imptimeEnd = sdf.format(cal.getTime());
        //System.out.println("所在周星期日的日期："+imptimeEnd);
        return imptimeBegin + "," + imptimeEnd;
    }


}
