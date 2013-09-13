package com.wiseach.teamlog.web;

import com.wiseach.teamlog.utils.TeamlogLocalizationUtils;

/**
 * User: Arlen Tan
 * 12-8-12 下午2:21
 */
public class WebUtils {

//    public static Boolean firstUserCreated(ServletContext servletContext) {
//        return (Boolean) servletContext.getAttribute(TeamlogContextListener.FIRST_USER_CREATE_KEY);
//    }

    public static final String SITE_URL= TeamlogLocalizationUtils.getSystemParams("site.url");
    public static final String ACTIVATE_URL=SITE_URL+"activate-user/";
}
