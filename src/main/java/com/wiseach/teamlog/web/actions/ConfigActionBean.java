package com.wiseach.teamlog.web.actions;

import com.wiseach.teamlog.Constants;
import com.wiseach.teamlog.utils.DateUtils;
import com.wiseach.teamlog.utils.EmailSender;
import com.wiseach.teamlog.utils.TeamlogLocalizationUtils;
import com.wiseach.teamlog.web.security.UserAuthProcessor;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.*;
import java.util.Date;
import java.util.Properties;

/**
 * User: Arlen Tan
 * 12-8-9 下午6:22
 */
@UrlBinding("/config")
public class ConfigActionBean extends BaseActionBean {

    @DontValidate
    @DefaultHandler
    public Resolution view() {
        if (UserAuthProcessor.isAdmin(getContext()) || !UserAuthProcessor.firstUserCreated()) {
            loadConfiguration();
            return new ForwardResolution(ViewHelper.CONFIG_PAGE);
        }
        setRequestAttribute(ERROR_DESCRIPTION_KEY,"config.error.description");
        return ViewHelper.getStandardErrorBoxResolution();
    }

    private void loadConfiguration() {
        siteUrl= TeamlogLocalizationUtils.getSystemParams("site.url");
        updateSiteUrl();
        smtpUsername = EmailSender.EMAIL_ACCOUNT;
        smtpPassword = EmailSender.EMAIL_PASSWORD;
        smtpHost = EmailSender.HOST_NAME;
        smtpPort = EmailSender.SMTP_PORT.toString();
        ssl = EmailSender.EMAIL_SSL_ENABLED;
        tls = EmailSender.EMAIL_TLS_ENABLED;
    }

    private void updateSiteUrl() {
        if (siteUrl==null || siteUrl.equals(Constants.EMPTY_STRING)) {
            HttpServletRequest request = getContext().getRequest();
            String hostAddress="localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int localPort = request.getLocalPort();
            siteUrl="http://"+ hostAddress +(localPort ==80?Constants.EMPTY_STRING:(":"+ localPort))+"/";
        }
    }

    public Resolution save() {
        Properties properties = new Properties();
        try {
            String paramsFile = getContext().getServletContext().getRealPath("/")+"WEB-INF"+ File.separator+"classes"
                    +File.separator+ TeamlogLocalizationUtils.PARAMS_NAME+".properties";
            FileInputStream fileInputStream = new FileInputStream(paramsFile);
            properties.load(fileInputStream);
            fileInputStream.close();
            updateSiteUrl();
            properties.setProperty("site.url", siteUrl);
            properties.setProperty("email.account", smtpUsername);
            properties.setProperty("email.password", smtpPassword);
            properties.setProperty("email.smtp.host", smtpHost);
            properties.setProperty("email.smtp.port", smtpPort);
            ssl = ssl==null?"false":"true";
            properties.setProperty("email.ssl.enabled", ssl);
            tls = tls==null?"false":"true";
            properties.setProperty("email.tls.enabled",tls);
            FileOutputStream fileOutputStream = new FileOutputStream(paramsFile);
            properties.store(fileOutputStream,"update at "+ DateUtils.formatDate(new Date()));
            fileOutputStream.close();
            TeamlogLocalizationUtils.refreshParamBundle();
            //refresh current configuration.
            EmailSender.EMAIL_ACCOUNT = smtpUsername;
            EmailSender.EMAIL_PASSWORD = smtpPassword;
            EmailSender.HOST_NAME=smtpHost;
            EmailSender.SMTP_PORT = Integer.valueOf(smtpPort);
            EmailSender.EMAIL_SSL_ENABLED = ssl;
            EmailSender.EMAIL_TLS_ENABLED= tls;
        } catch (Exception e) {
            setRequestAttribute(ERROR_DESCRIPTION_KEY,"config.error.save.description");
            return ViewHelper.getStandardErrorBoxResolution();
        }
        if (UserAuthProcessor.firstUserCreated()) {
            return view();
        } else {
            return new RedirectResolution(CreateFirstUserActionBean.class);
        }
    }

    @Validate(required = true)
    public String siteUrl, smtpUsername, smtpPassword,smtpHost,smtpPort;
    public String ssl,tls;

    @ValidationMethod
    public void checkData(ValidationErrors errors) {
        try {
            Integer.valueOf(smtpPort);
        } catch (NumberFormatException e) {
            errors.add("smtpPort",new LocalizableError("smtpPort.notANumber"));
        }
        try {
            new URL(siteUrl);
        } catch (Exception e) {
            errors.add("siteUrl",new LocalizableError("siteUrl.invalid"));
        }
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getTls() {
        return tls;
    }

    public void setTls(String tls) {
        this.tls = tls;
    }
}
