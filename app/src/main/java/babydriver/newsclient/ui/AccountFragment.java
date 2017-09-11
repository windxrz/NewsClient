package babydriver.newsclient.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import babydriver.newsclient.R;
import babydriver.newsclient.model.Settings;


public class AccountFragment extends Fragment implements MyOneLineView.OnRootClickListener
{
    private LinearLayout llRoot;
    private MyOneLineView settingsView;
    private Button collectionBtn, downloadBtn;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        llRoot = view.findViewById(R.id.llAccount);
        collectionBtn = view.findViewById(R.id.collectionBtn);
        downloadBtn = view.findViewById(R.id.downloadBtn);
//        collectionBtn.setHeight(collectionBtn.getMeasuredWidth());
//        downloadBtn.setHeight(downloadBtn.getMeasuredWidth());
//        llRoot.setMinimumHeight(collectionBtn.getMeasuredHeight());
        settingsView = view.findViewById(R.id.settingsView);
        settingsView = settingsView
                .initMine(R.drawable.ic_settings_black_24dp, getString(R.string.title_activity_settings), "", false)
                .showDivider(false, false)
                .setOnRootClickListener(this, 1);
        return view;
    }

    @Override
    public void onRootClick(View view)
    {
        switch ((int) view.getTag())
        {
            case 1:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                        SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);
                break;
        }
    }

}

