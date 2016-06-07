package kr.waytech.attendancecheck_beacon.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.server.InsertAtdCompDB;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 */
public class AttendListAdapter extends BaseAdapter {

    private static final String TAG = "AttendListAdapter";
    private ArrayList<AttendListData> data = new ArrayList<>();

    private Context con;

    private boolean goneAttend = false;

    private int atdPos;


    public AttendListAdapter(Context con) {
        this.con = con;
    }
    public AttendListAdapter(Context con, boolean goneAttend) {
        this.con = con;
        this.goneAttend = goneAttend;
    }

    public void setData(ArrayList<AttendListData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AttendListData getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public ArrayList<AttendListData> getData() {
        return data;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_attend, viewGroup, false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        ImageView ivStatus = (ImageView) view.findViewById(R.id.ivAttendStatus);
        ImageView ivCheck = (ImageView) view.findViewById(R.id.ivAttendCheck);



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final AttendListData listViewItem = data.get(i);

        // 아이템 내 각 위젯에 데이터 반영
        tvName.setText(listViewItem.getName());

        ivStatus.setImageResource(listViewItem.getImage());

        if(goneAttend)
            ivCheck.setVisibility(View.GONE);

        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(con)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("출석")
                        .setMessage("출석인정 하시겠습니까?")
                        .setPositiveButton(con.getString(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new InsertAtdCompDB(mHandler).execute(listViewItem.getId(), listViewItem.getClassData().getClassName(), listViewItem.getSelectCal());
                                        atdPos = pos;
                                    }
                                }
                        ).setNegativeButton(con.getString(android.R.string.no), null).show();
            }
        });

        return view;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case InsertAtdCompDB.HANDLE_INSERT_OK:
                    data.get(atdPos).setImage(android.R.drawable.presence_online);
                    notifyDataSetChanged();
                    Toast.makeText(con, "출석인정되었습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case InsertAtdCompDB.HANDLE_INSERT_FAIL:
                    break;
            }
        }
    };


    public void reset(){
        for(int i = 0; i < data.size(); i++)
            data.get(i).setImage(android.R.drawable.presence_offline);
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
