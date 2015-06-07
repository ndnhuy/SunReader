package example.com.sunreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;

public class DetailItemActivity extends ActionBarActivity {
    public static final String MY_INTENT_MESSAGE = "intent_message";
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.detail_item_activity);

        // Get Feed title from Intent
        Intent intent = getIntent();
        String feedTitle = intent.getStringExtra(MY_INTENT_MESSAGE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailItemFragment.newInstance(feedTitle));
        }
    }
}
