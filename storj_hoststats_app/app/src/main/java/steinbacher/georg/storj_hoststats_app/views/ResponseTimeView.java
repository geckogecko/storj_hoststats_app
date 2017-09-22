package steinbacher.georg.storj_hoststats_app.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import steinbacher.georg.storj_hoststats_app.R;

/**
 * Created by georg on 22.09.17.
 */

public class ResponseTimeView extends android.support.v7.widget.AppCompatRadioButton{
    private static final String TAG = "ResponseTimeView";

    private Context mContext;
    private int mResponseTime;

    public ResponseTimeView(Context context) {
        super(context);
        mContext = context;
    }

    public ResponseTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ResponseTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setResponseTime(int responseTime) {
        mResponseTime = responseTime;

        if(responseTime != 0) {
            //set text
            Float responseTimeSeconds = (float) responseTime / 1000;
            setText(String.format("%.1f", responseTimeSeconds));

            //set color
            GradientDrawable gd = (GradientDrawable) getBackground();
            gd.setColor(getReponseTimeColor(responseTime));
        } else {
            //set color
            GradientDrawable gd = (GradientDrawable) getBackground();
            gd.setColor(getResources().getColor(R.color.grey));
            setText("");
        }
    }

    private int getReponseTimeColor(int responseTime) {
        String[] dangerColors = mContext.getResources().getStringArray(R.array.responseTimeColors);

        if(responseTime >= 10000) {
            return Color.parseColor(dangerColors[9]);
        } else {
            int index = responseTime/1000;
            return Color.parseColor(dangerColors[index-1]);
        }
    }
}
