package com.zokin.lin.main;
import java.util.LinkedList;

import com.lin.readdata.ReadCard;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private TextView messageView;
	private Button readBn, submitBn, cleanBn;
	ReadCard readCard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		messageView = (TextView) findViewById(R.id.messageView);
		readBn = (Button) findViewById(R.id.readBn);
		submitBn = (Button) findViewById(R.id.submitBn);
		cleanBn = (Button) findViewById(R.id.cleanBn);
		readBn.setOnClickListener(this);
		submitBn.setOnClickListener(this);
		cleanBn.setOnClickListener(this);
	     readCard=new ReadCard(MainActivity.this);
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	 switch (v.getId()) {
	case R.id.readBn:
		readCard.startRead();
		break;
	case R.id.submitBn:
		messageView.setText("");
		LinkedList<String> mesString=new LinkedList<String>();
		
		mesString=readCard.getCardId();
		for (int i = 0; i < mesString.size(); i++)   {
				messageView.append( mesString.get(i)
						+ "\n");
			}
		break;
	case R.id.cleanBn:
		readCard.stopRead();
		break;
	default:
		break;
	}
		
	}

}
