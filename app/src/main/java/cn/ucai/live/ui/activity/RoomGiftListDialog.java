package cn.ucai.live.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.LiveRoom;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomGiftListDialog extends DialogFragment {


    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.tv_my_bill)
    TextView tvMyBill;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;

    Unbinder unbinder;
    GridLayoutManager manager;
    GiftAdapter adapter;
    List<Gift> mList;

    private String username;
    private String chatroomId;

    public static RoomGiftListDialog newInstance() {
        RoomGiftListDialog dialog = new RoomGiftListDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        manager = new GridLayoutManager(getContext(), I.GIFT_COLUMN_COUNT);
        adapter = new GiftAdapter(getContext(), mList);
        rvGift.setLayoutManager(manager);
        rvGift.setHasFixedSize(true);
        customDialog();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList = LiveHelper.getInstance().getGiftList();
        if (mList.size() > 0) {
            if (adapter == null) {
                adapter = new GiftAdapter(getContext(), mList);
                rvGift.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void customDialog() {
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private RoomManageEventListener eventListener;

    public void setManageEventListener(RoomManageEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public interface RoomManageEventListener {
        void onKickMember(String username);

        void onAddBlacklist(String username);

    }


    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        Context context;
        List<Gift> list;

        public GiftAdapter(Context context, List<Gift> list) {
            this.context = context;
            this.list = list;
        }

        @Override

        public GiftAdapter.GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GiftViewHolder viewHolder = new GiftViewHolder(View.inflate(context, R.layout.item_gift, null));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(GiftAdapter.GiftViewHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list!=null?list.size():0;
        }

        class GiftViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.ivGiftThumb)
            ImageView ivGiftThumb;
            @BindView(R.id.tvGiftName)
            TextView tvGiftName;
            @BindView(R.id.tvGiftPrice)
            TextView tvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout layoutGift;

            GiftViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bind(Gift gift) {
                EaseUserUtils.setAvatar(context,gift.getGurl(),ivGiftThumb);
                tvGiftName.setText(gift.getGname());
                tvGiftPrice.setText(gift.getGprice()+"");
            }
        }
    }


}
