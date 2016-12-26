package com.example.kps.dbtest;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Map;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import java.util.Random;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AIListener, TextToSpeech.OnInitListener
{
    private ImageButton listenButton;
    private TextView resultTextView;
    private AIService aiService;
    private TextToSpeech tts;
    private boolean listening;
    dataBaseOps dbo;
    private int idVar;
    private ListView lvItems;
    private ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (ImageButton) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        final AIConfiguration config = new AIConfiguration("4b325ccec5ea48d9b776084a6e5eab5a",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        tts = new TextToSpeech(this, this);
        dbo=new dataBaseOps(this);
    }
    public void listenButtonOnClick(final View view)
    {
        if(listening)
        {
            aiService.cancel();
        }
        else
        {
            if(tts.isSpeaking())
            {
                tts.stop();
            }
            resultTextView.setText("Listening.......");
            aiService.startListening();
        }
    }

    public void onResult(final AIResponse response)
    {
        Result result = response.getResult();
         // Get parameters
        String parameterString = "";
        String task="";
        String dueDate="",dueTime="";
        String id="";
       // int completed; //find use for it!
        if (result.getParameters() != null && !result.getParameters().isEmpty())
        {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet())
            {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                switch(entry.getKey()){
                    case "task":
                        task=""+entry.getValue();
                        break;
                    case "date":
                        dueDate=""+entry.getValue();
                        break;
                    case "time":
                        dueTime=""+entry.getValue();
                        break;
                    case "number-integer":
                        id=""+entry.getValue();
                        System.out.println(":ID:"+id);
                        break;
                    case "number":
                        id=""+entry.getValue();
                        System.out.println(":ID-marked:"+id);
                        break;

                }
            }

        }
        // Show results in TextView.
        resultTextView.setText("\n" + response.getResult().getFulfillment().getSpeech().toString());
        speakOut(response.getResult().getFulfillment().getSpeech().toString());

        resultTextView.setText("You Said:" + result.getResolvedQuery()+"\nTally Says:" +resultTextView.getText());
                //+"\n"+
                //"\nAction: " + result.getAction() +
                //"\nParameters: " + parameterString);
        switch (result.getAction()){
            case "entering_task":
                //insert
                String complete="TODO";
                long flag=dbo.insertTask(task,dueDate,dueTime,complete);
                if(flag==-1)
                    Toast.makeText(this.getApplicationContext(),"Insert not done!",Toast.LENGTH_LONG).show();
                ArrayList<String> allTasksToshow=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasksToshow);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "mark_task":
                ArrayList<String> allTasksTomark=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasksTomark);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "mark_complete":
                int markcom=dbo.markComplete(id);
                if (markcom==0){
                    Toast.makeText(this.getApplicationContext(),"no tasks were made complete/Task already in complete",Toast.LENGTH_LONG).show();
                }
                ArrayList<String> allTasksTomarkc=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasksTomarkc);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "del_task":
                ArrayList<String> allTasksToDel=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasksToDel);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "delete_task":
                int deleteid=dbo.deleteTask(id);
                if(deleteid==0)
                    Toast.makeText(this.getApplicationContext(),"empty Table",Toast.LENGTH_LONG).show();
                ArrayList<String> allTasksdele=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasksdele);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "display_task":
                //select * from
                ArrayList<String> allTasks=dbo.getAllTasks();
                lvItems = (ListView) findViewById(R.id.lvItems);
                lvItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.sample_textview,allTasks);
                lvItems.setAdapter(itemsAdapter);
                break;
            case "anything_no":
                Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onInit(int status)
    {
        if(status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS", "This language is not supported");
            }
            else
            {

            }

        }
        else
        {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    public void onDestroy()
    {
        if(tts!= null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakOut(String text)
    {
        tts.speak(text,TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onError(final AIError error)
    {
        resultTextView.setText(error.toString());
    }
    @Override
    public void onListeningStarted()
    {
        listening = true;
    }
    @Override
    public void onListeningCanceled()
    {
        resultTextView.setText("Click the button to speak");
        listening = false;
    }
    @Override
    public void onListeningFinished()
    {
        listening = false;
    }

    @Override
    public void onAudioLevel(final float level) {}


}
