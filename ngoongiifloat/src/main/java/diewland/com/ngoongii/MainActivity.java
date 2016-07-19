package diewland.com.ngoongii;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// show windows
		StandOutWindow.closeAll(this, EmojiWindow.class);
		StandOutWindow.show(this, EmojiWindow.class, StandOutWindow.DEFAULT_ID);

		// end main app
		finish();
	}

}