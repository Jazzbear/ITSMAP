package com.example.assi90.aydabtu_lecture5_exercise2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.assi90.aydabtu_lecture5_exercise2.adaptors.TaskAdaptor;
import com.example.assi90.aydabtu_lecture5_exercise2.models.Task;
import com.facebook.stetho.Stetho;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Task> tasks;
    private TaskAdaptor adaptor;
    private Button addButton;
    private EditText taskName, taskPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for debugging/viewing database
        enableStethos();

        // ui elements
        listView = findViewById(R.id.taskList);
        taskName = findViewById(R.id.taskField);
        taskPlace= findViewById(R.id.placeField);
        addButton = findViewById(R.id.btnAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskPressed();
            }
        });

        // load tasks from database
        tasks = loadTasks();

        adaptor = new TaskAdaptor(this, tasks);
        listView.setAdapter(adaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //on click, delete task in the list and database.
                // the position is the specified item index.
                // it returns the element in the specified position.
                Task t = tasks.get(position);
                if (t != null) {
                    deleteTask(t);
                    tasks.remove(position);
                    adaptor.setTasks(tasks);
                    adaptor.notifyDataSetChanged();
                }
            }
        });
    }

    private void addTaskPressed() {
        final String name = taskName.getText().toString();
        final String place = taskPlace.getText().toString();

        if (name == null || name.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
        } else if (place == null || place.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
        } else {
            final Task t = new Task(name, place);
            t.setColorHex(Integer.toHexString(new Random()
                    .nextInt(255*255*255)));// add random rgb color hex
            tasks.add(t);
            addTask(t);

            taskName.setText("");
            taskPlace.setText("");
            // update the adaptor with the new tasks list as the new task has been added
            adaptor.setTasks(tasks);
            // Call on the super class of the adapter, notifying it and all its inheritants
            // that the data has changed.
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

        }
    }

    private void addTask(Task t) {
        ((TaskAppSingleton)getApplicationContext()).getTaskDatabase().myTaskDao().insertAll(t);
    }

    private void deleteTask(Task t) {
        ((TaskAppSingleton)getApplicationContext()).getTaskDatabase().myTaskDao().delete(t);
    }

    private List<Task> loadTasks(){
        return ((TaskAppSingleton)getApplicationContext()).getTaskDatabase().myTaskDao().getAll();
    }



    //enable stethos tool for inspecting database on device / emulator through chrome
    private void enableStethos(){

           /* Stetho initialization - allows for debugging features in Chrome browser
           See http://facebook.github.io/stetho/ for details
           1) Open chrome://inspect/ in a Chrome browse
           2) select 'inspect' on your app under the specific device/emulator
           3) select resources tab
           4) browse database tables under Web SQL
         */
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build());
        /* end Stethos */
    }
}
