package com.wiseach.teamlog.web.actions;

import com.wiseach.teamlog.db.UserAuthDBHelper;
import com.wiseach.teamlog.utils.EmailSender;
import com.wiseach.teamlog.web.WebUtils;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

/**
 * User: Arlen Tan
 * 12-8-9 下午2:58
 */
@UrlBinding("/send-reset-password-email")
public class SendRestPasswordEmailActionBean extends BaseActionBean {

    @DefaultHandler
    @DontValidate
    public Resolution view() {
        return new ForwardResolution(ViewHelper.SEND_RESET_PASSWORD_EMAIL_PAGE);
    }

    public Resolution send() {
        String uuidStr = UserAuthDBHelper.updateResetUUID(resetEmail);

        String activateUrl = WebUtils.RESET_URL+uuidStr;
        EmailSender.send(resetEmail,resetEmail,getMessage("email.activate.reset.user.subject"),getMessage("email.activate.reset.user",resetEmail,activateUrl,activateUrl));

        //System.out.println("activateUrl is:"+activateUrl);
        setRequestAttribute(SUCCESSFUL_TTILE_KEY, "send.title");
        setRequestAttribute(SUCCESSFUL_DESCRIPTION_KEY,"send.description");
        setRequestAttribute(SUCCESSFUL_INFO_KEY,getMessage("send.info",getMessage("successful.box.go.login")));

        return ViewHelper.getStandardSuccessfulBoxResolution();

    }

    @Validate(required = true)
    public String resetEmail;

    @ValidationMethod
    public void validatePassword(ValidationErrors errors) {
        // validate the email exists...
        if (!UserAuthDBHelper.emailHasRegistered(resetEmail)) {
            errors.add("resetEmail",new LocalizableError("reset.email.not.exists"));
        }
    }

    public void setResetEmail(String resetEmail) {
        this.resetEmail = resetEmail;
    }

    public String getResetEmail() {
        return resetEmail;
    }
}
