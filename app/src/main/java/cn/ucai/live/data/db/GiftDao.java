package cn.ucai.live.data.db;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.model.Gift;

/**
 * Created by maestro on 17-4-16.
 */

public class GiftDao {
    public static final String GIFT_TABLE_NAME = "t_live_app_gift";
    public static final String GIFT_COLUMN_ID = "m_gift_id";
    public static final String GIFT_COLUMN_NAME = "m_gift_name";
    public static final String GIFT_COLUMN_URL = "m_gift_url";
    public static final String GIFT_COLUMN_PRICE = "m_gift_price";

    public GiftDao() {
    }

    public void saveAppGifList(List<Gift> giftList) {
        DBManager.getInstance().saveAppGiftList(giftList);
    }

    public Map<Integer,Gift> getAppGiftList() {
        return DBManager.getInstance().getAppGiftList();
    }
}
