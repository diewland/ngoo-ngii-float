package diewland.com.ngoongii;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This implementation provides multiple windows. You may extend this class or
 * use it as a reference for a basic foundation for your own windows.
 * 
 * <p>
 * Functionality includes system window decorators, moveable, resizeable,
 * hideable, closeable, and bring-to-frontable.
 * 
 * <p>
 * The persistent notification creates new windows. The hidden notifications
 * restores previously hidden windows.
 * 
 * @author Mark Wei <markwei@gmail.com>
 * 
 */
public class EmojiWindow extends StandOutWindow {

	String TAG = "DIEWLAND";
	int items_per_row = 1;
		
	LinearLayout main;
	LinearLayout row;
	LinearLayout.LayoutParams btn_params;
		
	// clipboard
	ClipboardManager clipboard;
	ClipData clip;
		
	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_main, frame, true);
		
		view.setBackgroundColor(Color.BLACK);
		
		main = (LinearLayout)view.findViewById(R.id.main);
		row = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.row, null, false);
		
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		btn_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		
		// draw buttons, default is angry
		draw_btns(Emojis.angry, items_per_row);
	}

	private void draw_btns(String[] items, int ipr){
		// remove all rows
		main.removeAllViews();
		
		// add rows
		// skip first item ( title label )
		for(int i=1; i< items.length; i++){
			// new row
			if(i % ipr == 0){
				row = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.row, null, false);
				main.addView(row);
			}
			
			// add row
			final Button btn = new Button(this);
			btn.setLayoutParams(btn_params);
			btn.setText(items[i]);
			row.addView(btn);
			
			// add button click event
			btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String btn_text = btn.getText().toString();
				    copyToClipboard(btn_text);
					Toast.makeText(getApplicationContext(), "Send " + btn_text + " to clipboard.", Toast.LENGTH_SHORT).show();
				}
			});
		}
		/*
		// full field last row
		for(int i=1; i<= ipr - (emojis.length % ipr); i++){
			Button btn = new Button(this);
			btn.setLayoutParams(btn_params);
			btn.setText("N/A");
			row.addView(btn);
		}
		*/
	}
	
	private void copyToClipboard(String text){
		clip = ClipData.newPlainText("sample text", text);
	    clipboard.setPrimaryClip(clip);
	}
	
	private void setWinTitle(String title){
		// 0 - id of first window
		setTitle(0, title);
	}
	
	// ================================================================================================================
	
	// float window title
	@Override
	public String getAppName() {
		return "ಠ益ಠ";
	}

	// float window icon
	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_more;
	}

	// every window is initially same size
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 450, 500,
				StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
	}

	// we want the system window decorations, we want to drag the body, we want
	// the ability to hide windows, and we want to tap the window to bring to
	// front
	@Override
	public int getFlags(int id) {
		return StandOutFlags.FLAG_DECORATION_SYSTEM
				| StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
				// | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP --- random crash when rapidly clicks on title bar
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
				| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

	// close set
	@Override
	public String getPersistentNotificationTitle(int id) {
		return getAppName();
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "ngoo ngii is ready";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		// return StandOutWindow.getCloseIntent(this, EmojiWindow.class, id);
		// do nothing when click notification
		return null;
	}

	// restore set
	@Override
	public int getHiddenIcon() {
		return android.R.drawable.ic_menu_revert;
	}

	@Override
	public String getHiddenNotificationTitle(int id) {
		return getAppName();
	}

	@Override
	public String getHiddenNotificationMessage(int id) {
		return "click to restore";
	}

	// return an Intent that restores the EmojiWindow
	@Override
	public Intent getHiddenNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, getClass(), id);
	}

	@Override
	public Animation getShowAnimation(int id) {
		if (isExistingId(id)) {
			// restore
			return AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in);
		} else {
			// show
			return super.getShowAnimation(id);
		}
	}

	@Override
	public Animation getHideAnimation(int id) {
		return AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
	}
	
	@Override
	public List<DropDownListItem> getDropDownItems(int id) {
		List<DropDownListItem> items = new ArrayList<DropDownListItem>();
		for (Field field : Emojis.class.getFields()) {
			try {
				String field_name = field.getName();
				final String[] field_values = (String[])field.get(null);
				if(field_values != null) {
					items.add(new DropDownListItem(android.R.drawable.ic_menu_compass,
						field_name, new Runnable() {
						public void run() {
							setWinTitle(field_values[0]);
							draw_btns(field_values, items_per_row);
						}
					}));
				}
			}
			catch(Exception e){
				// skip virus
			}
		}
		items.add(new DropDownListItem(android.R.drawable.ic_menu_compass,
				"virus", new Runnable() {
					public void run() {
						copyToClipboard(Emojis.virus);
						Toast.makeText( EmojiWindow.this, "!! WARNING !!\nVirus is in your clipboard.", Toast.LENGTH_SHORT).show();
					}
				}));
		return items;
	}
}