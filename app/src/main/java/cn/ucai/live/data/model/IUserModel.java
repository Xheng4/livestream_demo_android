package cn.ucai.live.data.model;

import android.content.Context;

import java.io.File;

/**
 * Created by xheng on 2017/3/29.
 */

public interface IUserModel {
    void login(Context context, String userName, String password,
               OnCompleteListener<String> listener);

    void register(Context context, String userName, String nick, String password,
                  OnCompleteListener<String> listener);

    void unRegister(Context context, String userName,
                    OnCompleteListener<String> listener);

    void loadUserInfo(Context context, String userName, OnCompleteListener<String> listener);

    void upDateNick(Context context, String userName, String nick, OnCompleteListener<String> listener);

    void updateAvatar(Context context, String userName, File file, OnCompleteListener<String> listener);

    void addContact(Context context, String userName, String contactName, OnCompleteListener<String> listener);

    void loadContactList(Context context, String userName, OnCompleteListener<String> listener);
}
