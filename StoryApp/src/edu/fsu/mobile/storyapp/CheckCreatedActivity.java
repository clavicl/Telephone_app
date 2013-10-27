package edu.fsu.mobile.storyapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.LogRecord;

public class CheckCreatedActivity extends Activity {
    private ArrayList<Item> createdList = new ArrayList<Item>();
    private ItemAdapter myAdapter;
    private String Pnumber;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.story_view);
        Pnumber = getMyPhoneNumber();

        myAdapter = new ItemAdapter(this,R.layout.story_list_fragment, createdList);

        ListView listView = (ListView)findViewById(R.id.list);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView story_Textview = (TextView) view.findViewById(R.id.story);
                String story="";
                if (story_Textview != null)
                    story = story_Textview.getText().toString();
                TextView storyText_textView = (TextView)findViewById(R.id.storyText);
                if (storyText_textView != null)
                    storyText_textView.setText(story);
            }
        });


        String amp = "&";
        String base = "http://myligaapi.elementfx.com/teleApp/getStory.php";//?flag=add&" +
        // "story=";
        String url = base +"?flag=created" + amp +
                "creator=" + Pnumber;
        url = url.replaceAll(" ", "%20");
        new ProcessingTask().execute(url);

	}
    private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyMgr.getLine1Number() != null)
            return mTelephonyMgr.getLine1Number().substring(1);
        else
            return "0000000000";
    }

    protected class ProcessingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String ... urls) {
            String flag = "0";
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(urls[0]);

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e(CreateStoryActivity.class.toString(), "Failed to download file");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                JSONObject jsonObject = new JSONObject(builder.toString());
                flag = jsonObject.getString("flag");
                if(flag != null && flag.equals("1")){
                    JSONArray stories = jsonObject.getJSONArray("stories");
                    for(int i=0; i<stories.length();i++)
                    {
                        JSONObject row = stories.getJSONObject(i);
                        String story = row.getString("story");
                        String title = row.getString("title");
                        if(story != null && title!=null)
                            createdList.add(new Item(story,title));
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }


            //m_adapter = new ItemAdapter(MainActivity.this, R.layout.list_item, m_parts);

            // display the list.
            //setListAdapter(m_adapter);
            return flag;
        }

        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminateVisibility(true);
        }
        @Override
        protected void onPostExecute(String result) {
            setProgressBarIndeterminateVisibility(false);
            if (result.equals("1")){
                myAdapter = new ItemAdapter(CheckCreatedActivity.this,R.layout.story_list_fragment, createdList);
                ListView listView = (ListView)findViewById(R.id.list);

                listView.setAdapter(myAdapter);
            }
            else {
                Toast.makeText(getApplicationContext(),"Error creating story, try again later",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



    private class Item {
        private String story;
        private String story_title;

        public Item(){

        }

        public Item(String s, String t){
            this.story = s;
            this.story_title = t;
        }

        public String getStory() {
            return story;
        }

        public void setStory(String story) {
            this.story = story;
        }

        public String getStory_title() {
            return story_title;
        }

        public void setStory_title(String title) {
            this.story_title = title;
        }
    }

    private class ItemAdapter extends ArrayAdapter<Item>{
        private ArrayList<Item> objects;

        public ItemAdapter(Context context,int textViewResourceId, ArrayList<Item> objects){
            super(context,textViewResourceId,objects);
            this.objects = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent){

            // assign the view we are converting to a local variable
            View v = convertView;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.story_list_fragment, null);
            }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
            Item i = objects.get(position);

            if (i != null) {

                // This is how you obtain a reference to the TextViews.
                // These TextViews are created in the XML files we defined.

                TextView from_textView = (TextView) v.findViewById(R.id.from);
                TextView title_textView = (TextView) v.findViewById(R.id.title);
                TextView story_textView = (TextView) v.findViewById(R.id.story);

                // check to see if each individual textview is null.
                // if not, assign some text!
                if (from_textView != null){
                    from_textView.setText("From: "+Pnumber);
                }
                if (title_textView != null){
                    title_textView.setText("Title: " +i.getStory_title());
                }
                if (story_textView != null){
                    story_textView.setText(i.getStory());
                }
            }

            // the view must be returned to our activity
            return v;

        }
    }
}
