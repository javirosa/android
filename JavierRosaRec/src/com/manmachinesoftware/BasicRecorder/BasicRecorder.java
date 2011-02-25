package com.manmachinesoftware.BasicRecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;


enum RecordingState {
	REC(R.string.recording),
	PRESSBUTTON(R.string.pressButton),
	PLAY(R.string.playing);	
	
	private int txtId;
	
	RecordingState(final int txtId){
		this.txtId = txtId;
	}
	
	String getString(Context context) {
		return context.getString(txtId);
	}
}


public class BasicRecorder extends Activity {
	static MediaRecorder mRec = null;
	RecordingState state = RecordingState.PRESSBUTTON;
	Context context;
	public BasicRecorder(){
		super();
	}
	
	// OnClickListener for the record button
	private OnClickListener recButListener = new OnClickListener() {
	    public synchronized void onClick(View v) {
	    	TextView txtV = (TextView)findViewById(R.id.txtIns);
	    	Chronometer chronos = (Chronometer)findViewById(R.id.chronos);
	    	chronos.stop();
	    	
	    	if (state == RecordingState.PRESSBUTTON) {
	    		state = RecordingState.REC;
	    		chronos.setBase(android.os.SystemClock.elapsedRealtime());
	    		chronos.start();
	    		
	    		//FileName is for a file in the public directory
	    		String outName = "testing.mp4";
	    		//File pubRecFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
	    		//pubRecFile = new File(pubRecFile,outName);
	    		
	    		try {	    			
		    		FileOutputStream recFile = openFileOutput(outName,Context.MODE_PRIVATE);//new FileOutputStream(pubRecFile);
	    			
		    		//Record
		    		mRec = new MediaRecorder();
		    		mRec.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
		    		mRec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		    		mRec.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		    		mRec.setMaxDuration(2*60*1000);
		    		mRec.setOutputFile(recFile.getFD());
		    		mRec.prepare();
		    		mRec.start();
	    		} catch (IOException ioe) {
	    			state = RecordingState.PRESSBUTTON;
		    		chronos.setBase(android.os.SystemClock.elapsedRealtime());
		    		chronos.stop();
	    			mRec = null;
	    		}
	    		
	    	} else if (state == RecordingState.REC) {
	            mRec.stop();
	            mRec.release();
	            mRec = null;
	            
	    		state = RecordingState.PLAY;
	    		txtV.setText(state.getString(context));
	    		
	    		//Play
	    		
	    		//Ready to record again
	    		state = RecordingState.PRESSBUTTON;
	    	} 

	    	txtV.setText(state.getString(context));
	    }
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        //The big red button
        ImageButton recBut = (ImageButton)findViewById(R.id.recImageBut);
        recBut.setOnClickListener(recButListener);
        
        //Alert Dialog which keeps user support at bay.
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(context.getString(R.string.alertMsg));
		
		
		DialogInterface.OnClickListener alertClickListener = new DialogInterface.OnClickListener() {	
			public synchronized void onClick(DialogInterface dia, int what) {		
			}
		};
		alert.setNeutralButton(R.string.OK, alertClickListener);
		alert.show();
    }
}