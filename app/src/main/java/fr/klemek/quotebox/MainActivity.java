package fr.klemek.quotebox;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import fr.klemek.quotebox.quote.Quote;
import fr.klemek.quotebox.quote.QuoteAdapter;
import fr.klemek.quotebox.quote.QuoteEditionActivity;
import fr.klemek.quotebox.quote.QuoteList;
import fr.klemek.quotebox.utils.ConnectionUtils;
import fr.klemek.quotebox.utils.Constants;
import fr.klemek.quotebox.utils.DataManager;
import fr.klemek.quotebox.utils.FileUtils;
import fr.klemek.quotebox.youtube.YoutubeSearchActivity;

/**
 * Created by klemek on ? !
 */

public class MainActivity extends AppCompatActivity{

    private QuoteList quotes;
    private boolean firstLoad = true;
    private SparseArray<MediaPlayer> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setTitle(R.string.title_activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, YoutubeSearchActivity.class);
                //Intent i = new Intent(MainActivity.this, QuoteCreationActivity.class);
                //i.putExtra(Constants.EXTRA_VIDEOID,"s5-nUCSXKac");
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        quotes = DataManager.getInstance().getQuoteList();

        players = new SparseArray<>();

        if(!quotes.isEmpty())
            findViewById(R.id.quote_list_empty_tv).setVisibility(View.GONE);
        else
            findViewById(R.id.quote_list_empty_tv).setVisibility(View.VISIBLE);

        QuoteAdapter adapter = new QuoteAdapter(this, quotes);

        final GridView gridview = (GridView) findViewById(R.id.quotelist);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v,
                                    int position, long id) {
                Quote q = quotes.get(position);
                MediaPlayer mp = players.get(position);
                if(mp == null) {
                    mp = FileUtils.loadSound(q.getFile());
                    if(mp != null){
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.stop();
                            ((ImageView)v.findViewById(R.id.quote_icon)).setImageDrawable(getDrawable(R.drawable.quotes));
                        }
                    });
                    players.put(position, mp);}
                    if(mp != null)
                        mp.prepareAsync();
                    ((ImageView)v.findViewById(R.id.quote_icon)).setImageDrawable(getDrawable(R.drawable.play));
                }else if(mp.isPlaying()){
                    mp.stop();
                    ((ImageView)v.findViewById(R.id.quote_icon)).setImageDrawable(getDrawable(R.drawable.quotes));
                }else{
                    mp.prepareAsync();
                    ((ImageView)v.findViewById(R.id.quote_icon)).setImageDrawable(getDrawable(R.drawable.play));
                }
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Intent intent = new Intent(MainActivity.this, QuoteEditionActivity.class);
                intent.putExtra(Constants.EXTRA_QUOTEID, i);
                startActivityForResult(intent,0);
                return true;
            }
        });
        if(firstLoad){
            firstLoad = false;
            ConnectionUtils.checkVersion(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.dialog_about_title)
                        .content(getResources().getString(R.string.dialog_about_content,
                                Constants.VERSION_ID))
                        .negativeText(R.string.dialog_close)
                        .cancelable(true)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(players != null)
            for(int i = 0; i < players.size(); i++) {
                int key = players.keyAt(i);
                // get the object by the key.
                players.get(key).release();
            }
    }
}
