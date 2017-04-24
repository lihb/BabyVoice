package com.lihb.babyvoice.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.DividerLine;
import com.lihb.babyvoice.model.VaccineInfo;
import com.lihb.babyvoice.utils.CommonDialog;
import com.lihb.babyvoice.utils.SimpleDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lhb on 2017/3/6.
 */

public class VaccineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VaccineInfo> mData;
    private SimpleDatePickerDialog datePickerDialog;

    public VaccineAdapter(Context mContext, List<VaccineInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.vaccine_info_item, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VaccineViewHolder) {
            VaccineViewHolder viewHolder = (VaccineViewHolder) holder;
            VaccineInfo vaccineInfo = mData.get(position);
            viewHolder.bindData(vaccineInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(List<VaccineInfo> dataList) {
        mData = dataList;
        notifyDataSetChanged();
    }

    public class VaccineViewHolder extends RecyclerView.ViewHolder {

        public TextView vaccine_name_txt;
        public TextView vaccine_inject_txt;

        public CheckBox vaccine_check_box;
        public ImageView vaccine_label_img;

        public DividerLine dividerLine;

        public VaccineViewHolder(View itemView) {
            super(itemView);
            vaccine_label_img = (ImageView) itemView.findViewById(R.id.vaccine_label_img);
            vaccine_check_box = (CheckBox) itemView.findViewById(R.id.vaccine_check_box);
            vaccine_name_txt = (TextView) itemView.findViewById(R.id.vaccine_name_txt);
            vaccine_inject_txt = (TextView) itemView.findViewById(R.id.vaccine_inject_txt);
            dividerLine = (DividerLine) itemView.findViewById(R.id.bottom_divider_line);
        }


        public void bindData(VaccineInfo vaccineInfo) {
            if (vaccineInfo == null) {
                return;
            }
            vaccine_name_txt.setText(vaccineInfo.vaccineName);
            if (vaccineInfo.isInjected == 1) {
                vaccine_check_box.setChecked(true);
//                vaccine_check_box.setBackgroundResource(R.mipmap.selected);
                vaccine_inject_txt.setText(vaccineInfo.injectDate + mContext.getString(R.string.injected));
                vaccine_inject_txt.setTextColor(ContextCompat.getColor(mContext, R.color.color_999999));
            } else {
//                vaccine_check_box.setBackgroundResource(R.mipmap.normal);
                vaccine_check_box.setChecked(false);
                vaccine_inject_txt.setText(mContext.getString(R.string.not_injected));
                vaccine_inject_txt.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
            }

            vaccine_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    if (isChecked) {
                        datePickerDialog = new SimpleDatePickerDialog.Builder()
                                .setContext(mContext)
                                .setTitleId(R.string.vaccine_injecte_date_dialog_title)
                                .setYYYYMMDD(now)
                                .setConfirmListener(new CommonDialog.OnActionListener() {
                                    @Override
                                    public void onAction(int which) {
                                        vaccine_inject_txt.setText(datePickerDialog.getYYYYMMDD() + mContext.getString(R.string.injected));
                                        // FIXME: 2017/4/24 更新数据库
                                    }
                                })
                                .build();
                        datePickerDialog.show();
                    }else {
                        vaccine_inject_txt.setText(mContext.getString(R.string.not_injected));
                    }
                }
            });
            if (isLastInGroup(getLayoutPosition())) {
                dividerLine.setVisibility(View.GONE);
            } else {
                dividerLine.setVisibility(View.VISIBLE);
            }

        }
    }

    private boolean isFirstInGroup(int position) {
        boolean isFirst;
        if (position == 0) {
            isFirst = true;
        } else {
            if (mData.get(position).ageToInject ==
                    (mData.get(position - 1).ageToInject)) {
                isFirst = false;
            } else {
                isFirst = true;
            }
        }
        return isFirst;
    }

    private boolean isLastInGroup(int pos) {

        int preAge = mData.get(pos).ageToInject;
        int nextAge;
        try {
            nextAge = mData.get(pos + 1).ageToInject;
        } catch (IndexOutOfBoundsException exception) {
            return true;
        }

        if (!(preAge == nextAge)) return true;

        return false;
    }
}
