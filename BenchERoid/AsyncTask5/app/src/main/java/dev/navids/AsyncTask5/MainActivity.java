package dev.navids.AsyncTask5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    MyObject myObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"a");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"b");
            }
        });
    }


    class MyAsyncTask extends AsyncTask<String,Void,MyObject>{

        @Override
        protected MyObject doInBackground(String... strings) {
            MyObject ret = new MyObject(strings[0]);
            try {
                Thread.sleep(new Random().nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(MyObject o) {
            myObject = o;
        }

    }

    class MyObject{
        String name;
        public MyObject (String name){
            this.name = name;
        }
    }
}
