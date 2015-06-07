package example.com.sunreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailItemActivity extends ActionBarActivity {
    public static final String MY_INTENT_MESSAGE = "intent_message";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_item_activity);

        // Get Feed title from Intent
        Intent intent = getIntent();
        String feedTitle = intent.getStringExtra(MY_INTENT_MESSAGE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailItemFragment.newInstance(feedTitle))
                    .commit();
        }
    }
}
