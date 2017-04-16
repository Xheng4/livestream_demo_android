package cn.ucai.live;

import android.content.Context;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.db.GiftDao;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.utils.PreferenceManager;


public class LiveModel {
    protected Context context = null;

    GiftDao dao;

    public LiveModel(Context ctx) {
        context = ctx;
        PreferenceManager.init(context);
        dao = new GiftDao();
    }

    public Map<Integer,Gift> getGiftList() {
        return dao.getAppGiftList();
    }

    public void setGiftList(List<Gift> list) {
        dao.saveAppGifList(list);
    }

    /**
     * save current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return PreferenceManager.getInstance().getCurrentUsername();
    }

}
