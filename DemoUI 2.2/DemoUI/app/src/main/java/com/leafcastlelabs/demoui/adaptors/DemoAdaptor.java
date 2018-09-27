package com.leafcastlelabs.demoui.adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.leafcastlelabs.demoui.R;
import com.leafcastlelabs.demoui.model.Demo;

import java.util.ArrayList;

public class DemoAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<Demo> demos;
    private Demo demo;
    private String demoprefix;

    public DemoAdaptor(Context c, ArrayList<Demo> demoList){
        this.context = c; //we need the context to inflate views
        this.demos = demoList;  //we need the actual list of demos so we can
        demoprefix = context.getResources().getString(R.string.mrpbh);
    }

    @Override
    public int getCount() {
        //return size of the array list (number of demos)
        if(demos!=null) {
            return demos.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (demo object) in our demo array list at the given position
        if(demos!=null) {
            return demos.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //we only need to create the views once, if not null we will reuse the existing view and update its values
        if (convertView == null) {
            LayoutInflater demoInflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = demoInflator.inflate(R.layout.demo_list_item, null);
        }

        demo = demos.get(position);
        if(demo!=null){
            //set the title text from the demo list
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtDemoTitle);
            txtTitle.setText(demo.getName());

            //set the description text from the demo list
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDemoDescription);
            txtDescription.setText(demoprefix + "! " + demo.getDescription());
        }
        return convertView;
    }
}
