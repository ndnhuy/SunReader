package example.com.sunreader;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MySimpleCursorAdapter extends SimpleCursorAdapter {
    private Context mContext;
    private int mLayoutId;
    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mLayoutId = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder();
//
//        }

        View v = super.getView(position, convertView, parent);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) v.findViewById(R.id.item_imageview);
        v.setTag(viewHolder);
        return v;
    }

    public static class ViewHolder {
        public ImageView imageView;
    }


}
