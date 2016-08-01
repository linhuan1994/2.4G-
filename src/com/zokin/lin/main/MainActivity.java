package com.zokin.lin.main;
import com.zokin.rfid.Zokin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private TextView messageView;
	private Button readBn, submitBn, cleanBn;
	Zokin zokin;
	Handler testHandler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case 1:
				String ss=String.valueOf(msg.obj);
				messageView.append(ss);
				break;

			default:
				break;
			}
		}
	};
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
	 switch (v.getId()) {
	case R.id.readBn:
		//zokin.readCard.startRead();
		Object n=zokin.setCommand.SetStatus("Set_Power", 2);
		
		testHandler.obtainMessage(1, n).sendToTarget();
		
		break;
	case R.id.submitBn:
		testHandler.obtainMessage(1, zokin.setCommand.ReadStatus("Read_Power")).sendToTarget();
//		messageView.setText("");
//		LinkedList<String> mesString=new LinkedList<String>();
//		
//		mesString=zokin.readCard.getCardId();
//		for (int i = 0; i < mesString.size(); i++)   {
//				messageView.append( mesString.get(i)
//						+ "\n");
//			}
		break;
	case R.id.cleanBn:
		//zokin.readCard.stopRead();
		Object n1=zokin.setCommand.SetStatus("Set_Power", 3);
		
		testHandler.obtainMessage(1, n1).sendToTarget();
		break;
	default:
		break;
	}
		
	}

}
