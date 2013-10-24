package edu.fsu.mobile.storyapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;


// this is a test - Ryan Alain
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

public void createStory(View view){
	Intent intent = new Intent(this, CreateStoryActivity.class);
	startActivity(intent);
}

public void checkCreated(View view){
	Intent intent = new Intent(this, CheckCreatedActivity.class);
	startActivity(intent);
}

public void checkContributed(View view){
	Intent intent = new Intent(this, CheckContributedActivity.class);
	startActivity(intent);
}

public void checkRequest(View view){
	Intent intent = new Intent(this, CheckRequestActivity.class);
	startActivity(intent);
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
