package com.practise.xmlparse.xmldom;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends ActionBarActivity {

    private Button mParseBtn;
    private InitiateMergingUtility initiateMergingUtility;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParseBtn =(Button)findViewById(R.id.parse_button);


        mParseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initiateMergingUtility=new InitiateMergingUtility(getApplicationContext());
                initiateMergingUtility.start("LogCodes.xml","/FTA/Log/","LogCodes1.xml");
            }
        });
    }


    //--------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
