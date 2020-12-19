package com.example.chokipedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WrongAnswerListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<WrongAnswerListItem> mList = null;
    private int mCnt = 0;

    public WrongAnswerListAdapter(ArrayList<WrongAnswerListItem> list){
        mList = list;
        mCnt = mList.size();
    }

    @Override
    public int getCount() {
        return mCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            final Context context = parent.getContext();
            if(inflater==null){
                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.wrong_answer_listview_item, parent, false);
        }

        TextView index = convertView.findViewById(R.id.tv_index);
        TextView word = convertView.findViewById(R.id.tv_word);
        TextView rate = convertView.findViewById(R.id.tv_rate);

        index.setText((mList.get(position).index).toString());
        word.setText(mList.get(position).word);
        rate.setText(String.format("%.2f",mList.get(position).rate)+"%");

        return convertView;
    }
}
