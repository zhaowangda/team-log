package com.wiseach.teamlog.utils;

import com.wiseach.teamlog.Constants;
import com.wiseach.teamlog.db.UserAuthDBHelper;
import com.wiseach.teamlog.web.security.UserAuthProcessor;
import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.stripes.action.FileBean;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * User: Arlen Tan
 * 12-8-17 上午10:30
 */
public class FileUtils {


    public static final String TEMP_AVATAR_FOLDER = "avatarTemp";
    public static final String AVATAR_FOLDER = "avatar";
    public static final String DEFAULT_AVATAR = UserAuthProcessor.ROOT_URI+"res/imgs/default-avatar.png";

    public static void saveBigAvatar(FileBean avatar, String realPath, String filename) {
        try {
            Thumbnails.of(avatar.getInputStream()).size(540,540).toFile(realPath+File.separator+ TEMP_AVATAR_FOLDER +File.separator+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(Constants.DOT_STRING));
    }

    public static void saveAvatar(String realPath, Integer x, Integer y, Integer width, String userAvatar) {
        if (userAvatar == null) return;
        try {

            String pathTemplate = realPath+"{0}"+File.separator+userAvatar;
            Thumbnails.of(MessageFormat.format(pathTemplate, TEMP_AVATAR_FOLDER)).sourceRegion(x, y, width, width).size(120,120).toFile(MessageFormat.format(pathTemplate, AVATAR_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUserAvatarURL(Long userId) {
        String userAvatar = UserAuthDBHelper.getUserAvatar(userId);
        return getUserAvatarURL(userAvatar);
    }

    public static String getUserAvatarURL(String avatar) {
        return (avatar !=null?UserAuthProcessor.ROOT_URI+FileUtils.AVATAR_FOLDER +Constants.ROOT_STRING+ avatar:DEFAULT_AVATAR);
    }

    public static String getUserBigAvatarURL(Long userId) {
        String userAvatar = UserAuthDBHelper.getUserAvatar(userId);
        return getUserBigAvatarURL(userAvatar);
    }

    public static String getUserBigAvatarURL(String avatar) {
        return (avatar !=null?UserAuthProcessor.ROOT_URI+FileUtils.TEMP_AVATAR_FOLDER +Constants.ROOT_STRING+ avatar: DEFAULT_AVATAR);
    }
}
