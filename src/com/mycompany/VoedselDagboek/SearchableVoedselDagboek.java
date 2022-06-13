package com.mycompany.VoedselDagboek;
import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.widget.AdapterView.*;
import android.database.*;
import android.widget.SearchView.*;
import java.util.*;
import android.widget.CalendarView.*;
import java.text.*;//import android.icu.text.*; als je dit gebruikt dan problemen met gsm

//hoe hebben we dit gecopieerd van RememberList => eerst maken we een map aan, bvb. Folder en 
//copieren de map RememberList naar deze map, dan alles met RememberList aanpassen, 
//beginnen in de map "gen" enz... en dan deze map terugzetten

public class SearchableVoedselDagboek extends Activity implements
View.OnClickListener
{
	ListView lv;
	FoodDatabase mDb;
	SearchView searchView1;
	EditText mInput1;
	String stringInput1;
	TextView mTxtDate,mTxtTime;
	Button mBtnDate,mBtnTime,mBtnDateTimeToDay;
	private int mYear, mMonth, mDay, mHour, mMinute;
	MenuItem menuItemSearchDate;
	Button mButton1,mButtonDate, mButton2;
	Button msave;
	
	Calendar cal = Calendar.getInstance();
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");//HH:mm:ss
	DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	CalendarView calview;
	LinearLayout calviewLayout;
	Date date = cal.getTime();
	
	private static final int DIALOG_DELETE_LIST=1;
	private static final int DIALOG_SAVE=2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView1 = (SearchView) findViewById(R.id.edittext);
		searchView1.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView1.setIconifiedByDefault(false);
		mButton1=(Button)findViewById(R.id.button1);
		mButtonDate=(Button)findViewById(R.id.buttondate);
		mButton2=(Button)findViewById(R.id.button2);
		calview=(CalendarView)findViewById(R.id.calendarview);
		calviewLayout=(LinearLayout)findViewById(R.id.calendarviewlayout);
		
		msave=(Button)findViewById(R.id.savebutton);
		lv=(ListView)findViewById(R.id.listview);
		mDb=new FoodDatabase(this,isExternalStorageWritable());//isExternalStorageWritable()
		mDb.open();
		mButtonDate.setText(dateFormat.format(date));
		showlist1(searchView1.getQuery().toString()); 
		
		mButtonDate.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (calviewLayout.getVisibility()==View.VISIBLE){
						calviewLayout.setVisibility(View.GONE);
					}
					else{
						calviewLayout.setVisibility(View.VISIBLE);
						long ti=cal.getTimeInMillis();
						cal.add(Calendar.DATE, 1);
						calview.setDate(cal.getTimeInMillis());
						cal.setTimeInMillis(ti);
						calview.setDate(cal.getTimeInMillis());
					}		
				}
		});
		
		mButtonDate.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					cal = Calendar.getInstance();
					calview.setDate(cal.getTimeInMillis());
					date =cal.getTime();
					mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
					toast("Datum vandaag");
					return true;
				}
		});
		
		mButton1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					cal.add(Calendar.DATE, -1);
					calview.setDate(cal.getTimeInMillis());
					date =cal.getTime();
					mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
				}
		});
		
		mButton1.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					cal.setTimeInMillis(mDb.getOldestDateLong());
					calview.setDate(cal.getTimeInMillis());
					date =cal.getTime();
					mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
					toast("Naar oudste datum");
					return true;//true
				}
		});
		
		mButton2.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					cal.add(Calendar.DATE, 1);
					calview.setDate(cal.getTimeInMillis());
					date =cal.getTime();
					mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
				}
		});
		
		mButton2.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					cal.setTimeInMillis(mDb.getLatestDateLong()); 
					calview.setDate(cal.getTimeInMillis());
					date =cal.getTime();
					mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
					toast("Naar jongste datum");
					return true;
				}
		});
		
		calview.setOnDateChangeListener(new OnDateChangeListener(){
				public void onSelectedDayChange(CalendarView view,int year,int month,
												int day){							
					cal.set(year,month,day);
					date =cal.getTime();
		            mButtonDate.setText(dateFormat.format(date));
					showlist1(mButtonDate.getText().toString());
				}
			});	
		
		msave.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (!searchView1.getQuery().toString().isEmpty() && searchView1.getQuery().toString().compareTo(" ")!=0){
						showDialog(DIALOG_SAVE);
					}else{
						toast("Niets opgeslagen");
					}	
				}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long id)
				{
					Cursor cursor=mDb.getText(String.valueOf(id),null);
					String str=cursor.getString(cursor.getColumnIndex(FoodDatabase.KEY_TEXT));
					
					if(mButtonDate.getVisibility()==View.GONE){
						searchView1.setQuery(str,false);
						searchView1.requestFocus();
					}else{
						long ti=cursor.getLong(cursor.getColumnIndex(FoodDatabase.KEY_DATELONG));
						cal.setTimeInMillis(ti);
						stringInput1=str;
						showDialog(DIALOG_SAVE);
					}	
				}
		});
		
		searchView1.setOnQueryTextListener(new OnQueryTextListener(){

				@Override
				public boolean onQueryTextSubmit(String p1)
				{
					// TODO: Implement this method
					return false;
				}

				@Override
				public boolean onQueryTextChange(String query)
				{
					showlist1(query);
					return false;
				}
		});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
		menuItemSearchDate=menu.findItem(R.id.menu_searchDate);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.menu_searchText:
				searchView1.setVisibility(View.VISIBLE);
				msave.setVisibility(View.VISIBLE);
				mButton1.setVisibility(View.GONE);
				mButtonDate.setVisibility(View.GONE);
				mButton2.setVisibility(View.GONE);
				item.setChecked(true);
				calviewLayout.setVisibility(View.GONE);
				showlist1(searchView1.getQuery().toString());
				return true;
			case R.id.menu_searchDate:
				searchView1.setVisibility(View.GONE);
				mButton1.setVisibility(View.VISIBLE);
				mButtonDate.setVisibility(View.VISIBLE);
				mButton2.setVisibility(View.VISIBLE);
				msave.setVisibility(View.GONE);
				//calviewLayout.setVisibility(View.VISIBLE);
				item.setChecked(true);
				showlist1(mButtonDate.getText().toString());
				return true;
			case R.id.menu_deleteList:
				showDialog(DIALOG_DELETE_LIST);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
	
	@Override
    public void onClick(View v) {//dit is niet zo een goede manier om te gebruiken, onclicklistener is beter
        if (v == mBtnDate) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
										  int monthOfYear, int dayOfMonth) {					  
						cal.set(year,monthOfYear,dayOfMonth);	
						Date date=cal.getTime();
						String dateStr=dateFormat.format(date);
						mTxtDate.setText(dateStr);
						mYear = cal.get(Calendar.YEAR);
						mMonth = cal.get(Calendar.MONTH);
						mDay = cal.get(Calendar.DAY_OF_MONTH);
					}
				}, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
		
        if (v == mBtnTime) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						mYear = cal.get(Calendar.YEAR);
						mMonth = cal.get(Calendar.MONTH);
						mDay = cal.get(Calendar.DAY_OF_MONTH);
						cal.set(mYear,mMonth,mDay,hourOfDay,minute);
						Date date=cal.getTime();
						String timeStr=timeFormat.format(date);
						mTxtTime.setText(timeStr);
						mHour = cal.get(Calendar.HOUR_OF_DAY);
						mMinute = cal.get(Calendar.MINUTE);
					}
				}, mHour, mMinute, false);
            timePickerDialog.show();
        }
		
		if (v == mBtnDateTimeToDay) {
			cal = Calendar.getInstance();
			mYear = cal.get(Calendar.YEAR);
			mMonth = cal.get(Calendar.MONTH);
			mDay = cal.get(Calendar.DAY_OF_MONTH);
			mHour = cal.get(Calendar.HOUR_OF_DAY);
            mMinute = cal.get(Calendar.MINUTE);
			
			Date date=cal.getTime();
			String dateStr=dateFormat.format(date);
			mTxtDate.setText(dateStr);
			String timeStr=timeFormat.format(date);
			mTxtTime.setText(timeStr);
			cal.setTime(date);
		}	
    }
	
	private void showlist1(String query){
		Cursor cursor=null;
		if (query.compareTo("") == 0) {
			//cursor=mDb.getAllText();
			cursor=mDb.getAllDate();// dit gebruiken bij dates, werkt dus
		}else{
			cursor = mDb.getTextMatches(query,null);
		}
		
		if(mButtonDate.getVisibility()==View.VISIBLE){
			query=query.replaceAll("/","");
			cursor = mDb.getWordsDate(query);
		}
		
		if (cursor == null) {
			lv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
        } else {
			lv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            // Specify the columns we want to display in the result
            String[] from = new String[] {FoodDatabase.KEY_DATETIME, FoodDatabase.KEY_TEXT};//KEY_TEXT

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.date,R.id.food};

            // Create a simple cursor adapter for the words and apply them to the ListView
		    SimpleCursorAdapter words = new SimpleCursorAdapter(this,
																R.layout.result, cursor, from, to);
			lv.setAdapter(words);
			lv.setOnItemLongClickListener(new OnItemLongClickListener(){

					@Override
					public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, final long id)
					{
						PopupMenu popup = new PopupMenu(SearchableVoedselDagboek.this, p2);
						popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
						if(mButtonDate.getVisibility()==View.VISIBLE){popup.getMenu().findItem(R.id.item2).setVisible(false);}
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

								@Override
								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId()) {
										case R.id.item1:
											mDb.deleteText(id);
											if(mButtonDate.getVisibility()==View.GONE){
												showlist1(searchView1.getQuery().toString());
											}else{
												showlist1(mButtonDate.getText().toString());
											}
											toast("Item verwijderd uit lijst");
											return true;
										case R.id.item2:
											Cursor cursor=mDb.getText(String.valueOf(id),null);
											long ti=cursor.getLong(cursor.getColumnIndex(FoodDatabase.KEY_DATELONG));
											cal.setTimeInMillis(ti);
										
											calview.setDate(cal.getTimeInMillis());
											Date date1 =cal.getTime();
											mButtonDate.setText(dateFormat.format(date1));
											
											searchView1.setVisibility(View.GONE);
											mButton1.setVisibility(View.VISIBLE);
											mButtonDate.setVisibility(View.VISIBLE);
											mButton2.setVisibility(View.VISIBLE);
											msave.setVisibility(View.GONE);
											//calviewLayout.setVisibility(View.VISIBLE);
											menuItemSearchDate.setChecked(true);
											showlist1(mButtonDate.getText().toString());
											return true;
										default:
											return false;
									}	
								}
							});
						popup.show();
						return true;
					}
				});
        }
	}

	@Override
	protected void onDestroy() {
		//toast("onDestroy");
		super.onDestroy();
		if (mDb != null) {
			mDb.close();
		}
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	
	public void toast(String pp){
		Toast.makeText(this,pp,Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
			case DIALOG_DELETE_LIST:
				builder
					.setTitle("Verwijder volledige lijst?")
					.setOnCancelListener(new DialogInterface.OnCancelListener(){

						@Override
						public void onCancel(DialogInterface p1)
						{
							toast("Lijst niet verwijderd");
						}
					})	
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mDb.deleteTextList();
							showlist1(searchView1.getQuery().toString());
							toast("Lijst verwijderd");
						}
					})
					.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							dialog.cancel();
						}
					});
				AlertDialog dialogDelete = builder.create();
				dialogDelete.show();
			break;
			case DIALOG_SAVE:
				final Calendar oldCal=cal;
                View promptsView = layoutInflater.inflate(R.layout.save, null);
				mInput1 = (EditText)promptsView.findViewById(R.id.input1);
				
				mTxtDate = (TextView)promptsView.findViewById(R.id.txt_date);
				mTxtTime = (TextView)promptsView.findViewById(R.id.txt_time);
				mBtnDate = (Button)promptsView.findViewById(R.id.btn_date);
				mBtnTime = (Button)promptsView.findViewById(R.id.btn_time);
				mBtnDateTimeToDay = (Button)promptsView.findViewById(R.id.btn_dateTimeToDay);
				
				mBtnDate.setOnClickListener(this);
				mBtnTime.setOnClickListener(this);
				mBtnDateTimeToDay.setOnClickListener(this);
				
				String query="";
				if(mButtonDate.getVisibility()==View.GONE){
					query=searchView1.getQuery().toString().trim();
				}else{
					query=stringInput1;
				}
				mInput1.setText(query);
				mInput1.requestFocus();//cursorteken komt achteraan
				
				// Get Current Date
				mYear = cal.get(Calendar.YEAR);
				mMonth = cal.get(Calendar.MONTH);
				mDay = cal.get(Calendar.DAY_OF_MONTH);
				
				// Get Current Time
				mHour = cal.get(Calendar.HOUR_OF_DAY);
				mMinute = cal.get(Calendar.MINUTE);
				
				Date date=cal.getTime();
				String dateStr=dateFormat.format(date);
				mTxtDate.setText(dateStr);
				String timeStr=timeFormat.format(date);
				mTxtTime.setText(timeStr);
				
				//mInput1.setFocusable(false);
				//if (searchView.findFocus()!=null
				builder
					.setView(promptsView)
					.setCancelable(true)
					.setPositiveButton("Opslaan", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String que=mInput1.getText().toString().trim();
							mDb.addText(que,cal);
							if(mButtonDate.getVisibility()==View.GONE){
								searchView1.setQuery(que,false);
								showlist1(searchView1.getQuery().toString());
							}else{
								calview.setDate(cal.getTimeInMillis());
								Date date1 =cal.getTime();
								mButtonDate.setText(dateFormat.format(date1));
								showlist1(mButtonDate.getText().toString());
							}
							toast("Nieuw item opgeslagen");
						}
					})
					.setNegativeButton("Annuleren",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							if(mButtonDate.getVisibility()==View.VISIBLE){
								cal=oldCal;
								calview.setDate(cal.getTimeInMillis());
								Date date1 =cal.getTime();
								mButtonDate.setText(dateFormat.format(date1));
								showlist1(mButtonDate.getText().toString());
							}
							toast("Niets opgeslagen");
						}
					})
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface p1)
						{
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
					});
				AlertDialog dialogSave = builder.create();
				dialogSave.show();
			break;		 
		}			
		return super.onCreateDialog(id);
	}
}
