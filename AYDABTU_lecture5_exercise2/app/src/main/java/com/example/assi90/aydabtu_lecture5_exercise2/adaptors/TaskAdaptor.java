package com.example.assi90.aydabtu_lecture5_exercise2.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assi90.aydabtu_lecture5_exercise2.models.Task;
import com.example.assi90.aydabtu_lecture5_exercise2.R;


import java.util.List;

public class TaskAdaptor extends BaseAdapter {

    private List<Task> tasks;
    private Context context;

    Task tempTask;

    //Constructor
    public TaskAdaptor(Context context, List<Task> list){
        this.context = context;
        tasks = list;
    }

    @Override
    public int getCount() {
        if(tasks==null){
            return 0;
        }
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        if(tasks !=null && tasks.size() > position){
            return tasks.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater;
            inflater = LayoutInflater.from(context);
            // We use the inflater to inflate the listview with each element in the list
            // produced with the xml defined in tast_list_item.xml.
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }


        if(tasks!=null && tasks.size() > position){
            tempTask = tasks.get(position);
            ImageView i = convertView.findViewById(R.id.imageView);
            TextView t = convertView.findViewById(R.id.txtTaskName);
            t.setText(tempTask.getName());
            try {
                int c = Color.parseColor("#" + tempTask.getColorHex());
                t.setTextColor(c);
                i.setColorFilter(new PorterDuffColorFilter(c, PorterDuff.Mode.OVERLAY));
            } catch (IllegalArgumentException ex){
                Log.d("Adaptor", "unknown color: " + tempTask.getColorHex());
            }
            TextView p = convertView.findViewById(R.id.txtLocation);
            p.setText(tempTask.getPlace());
            TextView time = convertView.findViewById(R.id.txtTime);
            time.setText(tempTask.getDate().toString());
            return convertView;
        }
        return null;
    }
}
