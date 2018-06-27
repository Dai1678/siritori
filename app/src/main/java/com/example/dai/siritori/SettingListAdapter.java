package com.example.dai.siritori;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SettingListAdapter extends ArrayAdapter<SettingParamModel> {

    private int resource;
    private LayoutInflater inflater;
    private List<SettingParamModel> list;

    SettingListAdapter(@NonNull Context context, int resource, List<SettingParamModel> list) {
        super(context, resource, list);

        this.resource = resource;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    private static class ViewHolder{
        TextView titleText;
        TextView paramText;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = this.inflater.inflate(this.resource, null);
            holder = new ViewHolder();
            holder.titleText = convertView.findViewById(R.id.title_text);
            holder.paramText = convertView.findViewById(R.id.param_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        SettingParamModel model = this.list.get(position);
        holder.titleText.setText(model.getTitle());
        holder.paramText.setText(model.getParam());

        return convertView;
    }
}
