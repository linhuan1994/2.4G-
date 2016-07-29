package com.zokin.lin.main;
import java.util.LinkedList;

import com.zokin.rfid.Zokin;

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
	Zokin zokin;
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
	    zokin=new Zokin(MainActivity.this);
		
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
		zokin.readCard.startRead();
		
		break;
	case R.id.submitBn:
		messageView.setText("");
		LinkedList<String> mesString=new LinkedList<String>();
		
		mesString=zokin.readCard.getCardId();
		for (int i = 0; i < mesString.size(); i++)   {
				messageView.append( mesString.get(i)
						+ "\n");
			}
		break;
	case R.id.cleanBn:
		zokin.readCard.stopRead();
		break;
	default:
		break;
	}
		
	}

}
