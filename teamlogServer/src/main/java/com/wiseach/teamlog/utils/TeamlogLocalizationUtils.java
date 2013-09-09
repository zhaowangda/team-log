package com.wiseach.teamlog.utils;

import com.wiseach.teamlog.Constants;
import com.wiseach.teamlog.web.extensions.TeamlogLocalizationBundleFactory;
import net.sourceforge.stripes.controller.StripesFilter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Arlen Tan
 * 12-8-10 下午5:18
 */
public class TeamlogLocalizationUtils {
    /** The configuration parameter for changing the TeamLog message resource bundle. */
    public static final String TEAMLOG_MESSAGE_BUNDLE = "LocalizationBundleFactory.TeamLogMessageBundle";
    public static final String PARAMS_NAME = "params";
    private static ResourceBundle resourceBundle,paramsResourceBundle;

    public static String getResourceMessage(String key, Locale locale, Object... params) {
        if (resourceBundle==null) {
            resourceBundle = ((TeamlogLocalizationBundleFactory)StripesFilter.getConfiguration().getLocalizationBundleFactory()).getTeamLogMessageBundle(TEAMLOG_MESSAGE_BUNDLE, locale);
        }
        return loadMessage(resourceBundle,key, params);
    }

    public static String getSystemParams(String key, Object... params) {
        if (paramsResourceBundle==null) {
            refreshParamBundle();
        }
        return loadMessage(paramsResourceBundle,key,params);
    }

    public static void refreshParamBundle() {
        paramsResourceBundle = ResourceBundle.getBundle(PARAMS_NAME);
    }

    private static String loadMessage(ResourceBundle bundle, String key, Object[] params) {
        String message=bundle.getString(key);
        return message!=null? MessageFormat.format(message, params): Constants.EMPTY_STRING;
    }

}
