package com.wiseach.teamlog.web;

import com.wiseach.teamlog.utils.TeamlogLocalizationUtils;
import com.wiseach.teamlog.web.security.UserAuthProcessor;

/**
 * User: Arlen Tan
 * 12-8-12 下午2:21
 */
public class WebUtils {

//    public static Boolean firstUserCreated(ServletContext servletContext) {
//        return (Boolean) servletContext.getAttribute(TeamlogContextListener.FIRST_USER_CREATE_KEY);
//    }

    public static String SITE_URL= TeamlogLocalizationUtils.getSystemParams("site.url")+ UserAuthProcessor.ROOT_URI;
    public static final String ACTIVATE_URL=SITE_URL+"activate-user/";

    public static void updateSiteUrl(String url) {
        SITE_URL = url;
    }
}
