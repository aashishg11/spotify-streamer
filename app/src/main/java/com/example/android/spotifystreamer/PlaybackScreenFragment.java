package com.example.android.spotifystreamer;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackScreenFragment extends DialogFragment {

    TextView songTX;
    TextView albumTX;
    TextView artistTX;
    ImageView imageView;
    ImageButton nextBT;
    ImageButton playBT;
    ImageButton pauseBT;
    ImageButton previousBT;
    String url = "";
    String trackName;
    String image = "";
    String albumName;
    String artistName;
    int trackPosition;
    IntentFilter filter;
    private static Intent playbackService = null;
    private static BroadcastReceiver receiver = null;
    private boolean isPlaying = false;
    private static boolean saveState = false;
    static final String SONG_URL = "URL";
    static final String IMAGE_URL = "IMAGE";
    static final String ARTIST_NAME = "NAME";
    static final String ALBUM_NAME = "ALBUM";
    static final String SONG_NAME = "SONG";
    static final String TRACK_POSITION = "TRACK_POSITION";


    public PlaybackScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if( getDialog() != null ) getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_playback_screen, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            url = intent.getStringExtra(SONG_URL);
            trackName = intent.getStringExtra(SONG_NAME);
            artistName = intent.getStringExtra(ARTIST_NAME);
            albumName = intent.getStringExtra(ALBUM_NAME);
            image = intent.getStringExtra(IMAGE_URL);
            trackPosition = intent.getIntExtra(TRACK_POSITION, 0);

        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            url = arguments.getString(PlaybackScreenFragment.SONG_URL);
            image = arguments.getString(PlaybackScreenFragment.IMAGE_URL);
            artistName = arguments.getString(PlaybackScreenFragment.ARTIST_NAME);
            trackName = arguments.getString(PlaybackScreenFragment.SONG_NAME);
            albumName = arguments.getString(PlaybackScreenFragment.ALBUM_NAME);
            trackPosition = arguments.getInt(TRACK_POSITION);
        }


        artistTX = (TextView) view.findViewById(R.id.textview_playback_artist);
        artistTX.setText(artistName);
        songTX = (TextView) view.findViewById(R.id.textview_playback_song);
        trackName = TopTracksFragment.allTracks.get(trackPosition).trackName;
        songTX.setText(trackName);
        albumTX = (TextView) view.findViewById(R.id.textview_playback_album);
        albumTX.setText(albumName);
        nextBT = (ImageButton) view.findViewById(R.id.button_next_playback);
        previousBT = (ImageButton) view.findViewById(R.id.button_previous_playback);
        playBT = (ImageButton) view.findViewById(R.id.button_play_playback);
        pauseBT = (ImageButton) view.findViewById(R.id.button_pause_playback);
        imageView = (ImageView) view.findViewById(R.id.imageview_playback);
        if (!image.equals("")) {
           // imageView = (ImageView) view.findViewById(R.id.imageview_playback);
            Picasso.with(getActivity()).load(image).into(imageView);
        }


        playBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
            }
        });
        nextBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });
        pauseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSong();
            }
        });
        previousBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSong();
            }
        });

        loadData(trackPosition);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.d("playerfragment", "onSaveInstanceState");

        saveState = true;
        savedInstanceState.putInt("trackIndex", trackPosition);
        savedInstanceState.putBoolean("playing", isPlaying);
    }

    private void previousSong() {
        trackPosition--;
        if(trackPosition < 0 ){
            trackPosition = TopTracksFragment.allTracks.size()-1;
        }
        loadData(trackPosition);
    }

    private void pauseSong() {
        if(isPlaying) {
            togglePlayPause();
            isPlaying = false;
        }

        Intent intent = new Intent();
        intent.setAction(PlaybackService.ACTION_PAUSE);
        this.getActivity().sendBroadcast(intent);
    }

    private void playSong() {
        isPlaying = true;
        togglePlayPause();

        Intent intent = new Intent();
        intent.setAction(PlaybackService.ACTION_PLAY);
        this.getActivity().sendBroadcast(intent);
    }

    private void togglePlayPause() {
        if(playBT.getVisibility() == View.VISIBLE){
            playBT.setVisibility(View.GONE);
            pauseBT.setVisibility(View.VISIBLE);
        }else {
            pauseBT.setVisibility(View.GONE);
            playBT.setVisibility(View.VISIBLE);
        }
    }

    private void nextSong() {
      trackPosition++;
        if(trackPosition > TopTracksFragment.allTracks.size()-1){
            trackPosition = 0;
        }
        loadData(trackPosition);

    }

    private void loadData(int trackPosition) {
        artistTX.setText(artistName);
        trackName = TopTracksFragment.allTracks.get(trackPosition).trackName;
        songTX.setText(trackName);
        albumName = TopTracksFragment.allTracks.get(trackPosition).albumName;
        albumTX.setText(albumName);
        url = TopTracksFragment.allTracks.get(trackPosition).previewUrl;
        image = TopTracksFragment.allTracks.get(trackPosition).imageUrl_large;
        if (!image.equals("")) {
            Picasso.with(getActivity()).load(image).into(imageView);
        }
        if(!saveState) {

            if (playbackService != null) {
                //if(receiver != null) {
                //this.getActivity().unregisterReceiver(receiver);
                //}
                this.getActivity().stopService(playbackService);
            }
            playbackService = new Intent(this.getActivity(), PlaybackService.class);
            playbackService.putExtra("previewUrl", url);
            this.getActivity().startService(playbackService);
            //this.getActivity().registerReceiver(receiver, filter);
        }
        else {
            saveState = false;
        }
    }


}
