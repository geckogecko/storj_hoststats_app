package com.steinbacher.storj_hoststats_app.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.steinbacher.storj_hoststats_app.R;

/**
 * Created by georg on 12.11.17.
 */

public class DetailsLineView extends android.support.v7.widget.LinearLayoutCompat {
    private Context mContext;
    private String mTitle = "";
    private String mValue = "";
    private Status mStatus = Status.NoStatus;

    private AppCompatTextView mTitleTextView;
    private AppCompatTextView mValueTextView;


    public enum Status {
        OK,
        NOK,
        NoStatus
    }

    public DetailsLineView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public DetailsLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public DetailsLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(context);
    }

    public void setTitle(String title) {
        mTitle = title;
        mTitle += " ";
        mTitleTextView.setText(mTitle);
    }

    public void setValue(String value) {
        mValue = value;
        mValueTextView.setText(mValue);
    }

    public void setStatus(Status status) {
        mStatus = status;

        if(mStatus == Status.OK) {
            mValueTextView.setTextColor(mContext.getResources().getColor(R.color.storj_color_green));
        } else if(mStatus == Status.NOK) {
            mValueTextView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            mValueTextView.setTextColor(mContext.getResources().getColor(R.color.textColor));
        }
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);

        //Title
        mTitleTextView = new AppCompatTextView(context);
        mTitleTextView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);

        //Value
        mValueTextView = new AppCompatTextView(context);
        mValueTextView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        addView(mTitleTextView);
        addView(mValueTextView);

    }
}
