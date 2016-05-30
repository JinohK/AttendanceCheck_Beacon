package kr.waytech.attendancecheck_beacon.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.DeleteClassDB;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 */
public class ClassListAdapter extends BaseAdapter {

    private ArrayList<ClassData> data = new ArrayList<>();

    public static int TYPE_DELETE = 1;
    public static int TYPE_NORMAL = 2;
    private int type;
    private Context con;

    private int delPos;


    public ClassListAdapter(Context con, int type) {
        this.type = type;
        this.con = con;
    }

    public void setData(ArrayList<ClassData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ClassData getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_class, viewGroup, false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tvClass = (TextView) view.findViewById(R.id.tvClassName);
        ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);


        if(type == TYPE_NORMAL)
            ivDelete.setVisibility(View.GONE);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ClassData listViewItem = data.get(i);

        // 아이템 내 각 위젯에 데이터 반영
        tvClass.setText(listViewItem.getClassName());

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(con)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("삭제")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton(con.getString(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delPos = pos;
                                        SharedPreferences pref = con.getSharedPreferences(con.getPackageName(), 0);
                                        new DeleteClassDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""), listViewItem.getClassName());
                                        dialog.dismiss();
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
                case DeleteClassDB.HANDLE_INSERT_OK:
                    data.remove(delPos);
                    notifyDataSetChanged();
                    break;

                case DeleteClassDB.HANDLE_INSERT_FAIL:

                    break;
            }
        }
    };

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
