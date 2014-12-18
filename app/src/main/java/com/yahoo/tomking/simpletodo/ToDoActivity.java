 package com.yahoo.tomking.simpletodo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ToDoActivity extends Activity {
	private ArrayList<String> todoItems;
	private ArrayAdapter<String> todoAdapter;
	private ListView lvItems;
	private EditText etNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        todoAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
        lvItems.setAdapter(todoAdapter);
        setupListViewListner();
        setupEditTextViewListener();
    }

	private void setupListViewListner() {
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View items, int pos, long id) {
				todoItems.remove(pos);
				todoAdapter.notifyDataSetChanged();
				writeItems();
				return false;
			}
		});
	}
	
	private void setupEditTextViewListener() {
		etNewItem.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					String itemText = etNewItem.getText().toString();
					// strip new line before appending item added by pressing Enter
					if ("" != itemText) etNewItem.setText(itemText.toCharArray(), 0, itemText.length()- 1);
					onAddedItem(v);
					return true;
				}
				return false;
			}
		});
	}
	
	public void onAddedItem(View v) {
		String itemText = etNewItem.getText().toString();
		if ( "" == itemText.replaceAll("[\n\r]","")) {
			// beep on null entry
			final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
		}
		else {
			// update view adapter and file store
			todoAdapter.add(itemText);
			writeItems();
		}
		etNewItem.setText("");
	}
	
	private void readItems() {
		File filesDir = getFilesDir();
		File todoFile = new File(filesDir, "todo.text");
		try {
			todoItems= new ArrayList<String>(FileUtils.readLines(todoFile));
		} catch (IOException e) {
			todoItems= new ArrayList<String>();
		}
	}
	
	private void writeItems() {
		File filesDir = getFilesDir();
		File todoFile = new File(filesDir, "todo.text");
		try {
			FileUtils.writeLines(todoFile, todoItems);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
