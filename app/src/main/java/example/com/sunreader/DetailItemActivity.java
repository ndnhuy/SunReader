package example.com.sunreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailItemActivity extends ActionBarActivity {
    public static final String ITEM_ID = "item_id";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_item_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get Feed title from Intent
        Intent intent = getIntent();
        int itemId = intent.getIntExtra(ITEM_ID, -1);

        Bundle bundle = new Bundle();
        bundle.putInt(ITEM_ID, itemId);
        DetailItemFragment detailItemFragment = new DetailItemFragment();
        detailItemFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailItemFragment)
                    .commit();
        }
    }
}
