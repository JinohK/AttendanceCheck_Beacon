package kr.waytech.attendancecheck_beacon.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 */
public class BeaconListAdapter extends BaseAdapter {

    private ArrayList<BeaconListData> data = new ArrayList<>();
    private int selectPos = -1;

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BeaconListData getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setData(ArrayList<BeaconListData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setData(BeaconListData listData) {
        for (int i = 0; i < data.size(); i++) {
            BeaconListData beaconData = data.get(i);
            if (beaconData.getUuid().equals(listData.getUuid()) &&
                    beaconData.getMajor() == listData.getMajor() &&
                    beaconData.getMinor() == listData.getMinor()) {
                data.set(i, listData);
                notifyDataSetChanged();
                return;
            }
        }

        data.add(listData);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_beacon, viewGroup, false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tvUuid = (TextView) view.findViewById(R.id.tvUUID);
        TextView tvMajor = (TextView) view.findViewById(R.id.tvMajor);
        TextView tvMinor = (TextView) view.findViewById(R.id.tvMajor);
        TextView tvRssi = (TextView) view.findViewById(R.id.tvRssi);



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        BeaconListData listViewItem = data.get(i);

        // 아이템 내 각 위젯에 데이터 반영
        tvUuid.setText(listViewItem.getUuid());
        tvMajor.setText(listViewItem.getMajor() + "");
        tvMinor.setText(listViewItem.getMinor() + "");
        tvRssi.setText(listViewItem.getRssi() + "");

        return view;
    }

}
