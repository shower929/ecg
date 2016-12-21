package com.swm.app.superrun.marathon;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.swm.marathon.MarathonModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2016/11/5.
 */

public class MarathonAdapter extends BaseAdapter {

    private List<MarathonModel> mList;

    public synchronized void add(MarathonModel marathonModel) {
        if (mList == null) {
            mList = new ArrayList();
        }
        mList.add(marathonModel);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
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
        return null;
    }
}
