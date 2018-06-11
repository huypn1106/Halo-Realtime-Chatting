package com.deadk.halo.views.message.holder;

import android.view.View;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.common.ViewHolder;
import com.deadk.halo.ultilities.DateFormatter;

import java.util.Date;

public class DateHeaderViewHolder extends ViewHolder<Date> {

    protected TextView text;
    protected String dateFormat;
    public DateFormatter.Formatter dateHeadersFormatter;

    public DateHeaderViewHolder(View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.messageText);
        dateFormat = dateFormat == null ? DateFormatter.Template.STRING_DAY_MONTH_YEAR.get() : dateFormat;
    }

    @Override
    public void onBind(Date date) {
        if (text != null) {
            String formattedDate = null;
            if (dateHeadersFormatter != null) formattedDate = dateHeadersFormatter.format(date);
            text.setText(formattedDate == null ? DateFormatter.format(date, dateFormat) : formattedDate);
        }
    }
}
