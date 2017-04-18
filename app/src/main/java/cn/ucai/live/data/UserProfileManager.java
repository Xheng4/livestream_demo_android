package cn.ucai.live.data;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.io.IOException;

import cn.ucai.live.I;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.data.restapi.ApiManager;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;


public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;


	private boolean isSyncingContactInfosWithServer = false;

	private EaseUser currentUser;
	private User mUser;
	IUserModel mModel;

	boolean success;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		mModel = new UserModel();
		sdkInited = true;
		appContext = context;
		return true;
	}


	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	/**
	 * 充值方法，清空内存和SP中数据
	 */
	public synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}

	public synchronized EaseUser getCurrentUserInfo() {
		if (currentUser == null) {
			String username = EMClient.getInstance().getCurrentUser();
			currentUser = new EaseUser(username);
			String nick = getCurrentUserNick();
			currentUser.setNick((nick != null) ? nick : username);
			currentUser.setAvatar(getCurrentUserAvatar());
		}
		return currentUser;
	}

	public synchronized User getCurrentAppUserInfo() {
		if (mUser == null || mUser.getMUserName() == null) {
			String username = EMClient.getInstance().getCurrentUser();
			mUser = new User(username);
			String nick = getCurrentUserNick();
			mUser.setMUserNick((nick != null) ? nick : username);
			mUser.setAvatar(getCurrentUserAvatar());
		}
		return mUser;
	}


	public boolean updateCurrentUserNickName(final String nickname) {
		mModel.upDateNick(appContext, EMClient.getInstance().getCurrentUser(), nickname,
				new OnCompleteListener<String>() {
					Boolean b = false;
			@Override
			public void onSuccess(String res) {

				if (res != null) {
					Result result = ResultUtils.getResultFromJson(res, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							b = true;
							setCurrentWeChatUserNick(user.getMUserNick());
						}
					}
				}
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK).putExtra(I.User.NICK,b));
			}

			@Override
			public void onError(String error) {
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK).putExtra(I.User.NICK,b));
			}
		});

		return false;
	}

	public void uploadUserAvatar(File file) {
		success = false;
		L.e("avatar","uploadUserAvatar："+file);
		mModel.updateAvatar(appContext, EMClient.getInstance().getCurrentUser(), file, new OnCompleteListener<String>() {
			@Override
			public void onSuccess(String result) {
				L.e("avatar","uploadUserAvatar-result："+result);
				if (result != null) {
					Result json = ResultUtils.getResultFromJson(result, User.class);
					if (json != null && json.isRetMsg()) {
						User user = (User) json.getRetData();
						L.e("avatar","json.getRetData():"+user.toString());
						if (user != null) {
							setCurrentWeChatUserAvatar(user.getAvatar());
							success = true;
						}
					}
				}
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
						.putExtra(I.Avatar.AVATAR_ID,success));
			}

			@Override
			public void onError(String error) {
				L.e("avatar","onError():"+error);
				success = false;
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
						.putExtra(I.Avatar.AVATAR_ID,success));
			}
		});

	}


	public void asyncGetCurrentAppUserInfo() {
		L.e("asyncGetCurrentWeChatUserInfo");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					User user = ApiManager.get().loadUserInfo(EMClient.getInstance().getCurrentUser());
					if (user != null) {
						setWeChatUserInfo(user);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setWeChatUserInfo(User user) {
		mUser = user;
		setCurrentWeChatUserNick(user.getMUserNick());
		setCurrentWeChatUserAvatar(user.getAvatar());
	}

	private void setCurrentUserNick(String nickname) {
		getCurrentUserInfo().setNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}



	private void setCurrentWeChatUserNick(String nickname) {
		getCurrentAppUserInfo().setMUserNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentWeChatUserAvatar(String avatar) {
		L.e("loadUserInfo","avatar:"+avatar);
		getCurrentAppUserInfo().setAvatar(avatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
	}


	private String getCurrentUserNick() {
		return PreferenceManager.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return PreferenceManager.getInstance().getCurrentUserAvatar();
	}

}
