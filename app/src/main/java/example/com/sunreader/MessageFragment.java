package example.com.sunreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends Fragment {
    public static final String MESSAGE = "message";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_fragment, container, false);
        if (getArguments() != null) {
            TextView textView = (TextView) rootView.findViewById(R.id.message_textview);
            textView.setText(getArguments().getString(MESSAGE));
        }
        return rootView;
    }

    public static MessageFragment createWithMessage(String message) {
        Bundle bundle = new Bundle();
        bundle.putString(MessageFragment.MESSAGE, message);
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);

        return messageFragment;
    }
}
