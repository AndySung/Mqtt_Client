package com.soft.nice.mqtt_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soft.nice.mqtt_client.R;

import java.util.List;

public class MessageListViewAdapter extends BaseAdapter {
    private List<Message> list;
    LayoutInflater inflater;

    public MessageListViewAdapter(Context context, List<Message> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = inflater.inflate(R.layout.topic_message_item, null);
            viewHolder = new ViewHolder();
            viewHolder.message_txt = (TextView) convertView.findViewById(R.id.message_txt);
            viewHolder.topic_txt = (TextView) convertView.findViewById(R.id.topic_txt);
            viewHolder.send_type_txt = convertView.findViewById(R.id.type_txt);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.message_txt.setText(list.get(i).getMessage_txt());
        viewHolder.topic_txt.setText(list.get(i).getTopic_txt());
        viewHolder.send_type_txt.setText(list.get(i).getSend_type_txt());
        // 将这个处理好的view返回
        return convertView;
    }

    private class ViewHolder {
        public TextView message_txt;
        public TextView topic_txt;
        public TextView send_type_txt;
    }

}
