package com.vishalinventory.physicalinventory;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Manifest;

public class ScanActivity extends AppCompatActivity {

    EditText GandolaNo;
    AutoCompleteTextView Barcode;
    TextView Totalqty,Timeqty,Qty,totalcount,gandolatextview;
    Button Exit,Edit,Save,Enter;
    String Storecode,Stocktype,Areatype,Floortype,userTypedString,Ecode;
    private TextWatcher barcodetextwatcher;
    public static final String BARCODE_STRING_PREFIX = "@";
    DBController dbController;
    int count = 1;
    Boolean check;
    ArrayList<String>duplicatebarcode;
    ArrayList<String>quantity;

    ArrayList<String>duplicatebarcodedialog;
    ArrayList<String>received;
    android.support.v7.app.ActionBar actionBar;
    int countbarcode = 0;
    Bundle bundle = new Bundle();
    LinearLayout gandolaedit,gandolatext,scanlayout,scantextlayout,countlayout;
    private String STORECODE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        dbController = new DBController(getApplicationContext());
        totalcount = (TextView)findViewById(R.id.totalcount) ;

        GandolaNo = (EditText)findViewById(R.id.gandolano);
        Qty = (TextView)findViewById(R.id.qty);
        Barcode = (AutoCompleteTextView)findViewById(R.id.barcodeno);
        Exit = (Button)findViewById(R.id.exit);
        Edit = (Button)findViewById(R.id.edit);
        Save = (Button)findViewById(R.id.next);
       // Totalqty = (TextView)findViewById(R.id.totalqty);
        Timeqty = (TextView)findViewById(R.id.timetotal);
        gandolaedit = (LinearLayout)findViewById(R.id.gandolalayout) ;
        gandolatext = (LinearLayout)findViewById(R.id.gandolatext);
        Enter = (Button)findViewById(R.id.enter);
        gandolatextview = (TextView) findViewById(R.id.gandolanotext);
        scanlayout = (LinearLayout) findViewById(R.id.scanlayout);
        scantextlayout = (LinearLayout)findViewById(R.id.scantextlayout);
        countlayout = (LinearLayout)findViewById(R.id.countlayout);



        Bundle bundle1 = getIntent().getExtras();
        Storecode = bundle1.getString("Storecode");
        Stocktype = bundle1.getString("Stocktype");
        Areatype = bundle1.getString("Areatype");
        Floortype = bundle1.getString("Floortype");
        Ecode = bundle1.getString("Ecode");

        STORECODE =Storecode.toUpperCase();
        Log.e("Value",STORECODE +" " +Stocktype + " " + Areatype
        + " " + Floortype + "" + Ecode);


        actionBar = getSupportActionBar();
        //  actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
      //  actionBar.setIcon(R.drawable.ic_launchervishal);


        Timeqty.setText(Time());
//        totalcount.setText(0);

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GandolaNo.getText().toString().matches(""))
                {
                    Toast.makeText(ScanActivity.this, "Please Enter Gandola No", Toast.LENGTH_SHORT).show();
                    return;
                }

                gandolatextview.setText(GandolaNo.getText().toString());
                gandolatext.setVisibility(View.VISIBLE);
                gandolaedit.setVisibility(View.GONE);
                scanlayout.setVisibility(View.VISIBLE);
                scantextlayout.setVisibility(View.VISIBLE);
                countlayout.setVisibility(View.VISIBLE);

            }
        });

        barcodetextwatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                try {

                    if (Barcode.isPerformingCompletion()) {
                        return;
                    }
                    userTypedString = Barcode.getText().toString();

                    if (userTypedString.endsWith(BARCODE_STRING_PREFIX)) {

                        Qty.setText("1");
                        duplicatebarcode = dbController.gethualreadyscanned(userTypedString);
                        if (duplicatebarcode.size() >= 1)
                        {
                            // Qty.setText(duplicatebarcode.get(0));
                            String newqty = String.valueOf(Integer.parseInt(duplicatebarcode.get(0)) + 1);
                           // String newcount = String.valueOf(Integer.parseInt(totalcount.getText().toString()) + 1);

                            Toast.makeText(ScanActivity.this, newqty, Toast.LENGTH_SHORT).show();

                            dbController.updateExistinghustatus(userTypedString,newqty);
                            Barcode.setText("");
                            quantity =    dbController.getQuantity(GandolaNo.getText().toString());
                         //   countbarcode++;
                            totalcount.setText(quantity.get(0));

                        } else {
                            dbController.Insertgateentry(STORECODE, Stocktype, Ecode, Areatype,Floortype, GandolaNo.getText().toString(), Barcode.getText().toString(), Qty.getText().toString(), Timeqty.getText().toString());
                            quantity =    dbController.getQuantity(GandolaNo.getText().toString());
                           // String newcount = String.valueOf(Integer.parseInt(totalcount.getText().toString()) + 1);

                            Barcode.setText("");
                          //  countbarcode++;
                            totalcount.setText(quantity.get(0));
                        }


                        // Totalqty.setText("1");
                    }
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        };

        Barcode.addTextChangedListener(barcodetextwatcher);


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Savebuttondialog();

            }
        });

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Exitbuttondialog();
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog();
            }
        });

    }
    @Override
    public void onBackPressed() {
    Exitbuttondialog();
    }

    public void Exitbuttondialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Exit!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you Sure you want to Exit");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    clearfield();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public  void clearfield()
    {
       dbController =  new DBController(this);
        SQLiteDatabase db = dbController.getWritableDatabase();
        String tableName = "retail_physical_scanning";
        db.execSQL("delete from " + tableName);
        Log.e("Data","Deleted");
      //  db.execSQL("delete from " + tableName2);

    }


    public void showChangeLangDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final AutoCompleteTextView edt = (AutoCompleteTextView) dialogView.findViewById(R.id.barcodedialog);
        final EditText edt1 = (EditText) dialogView.findViewById(R.id.Qtydialog);
        barcodetextwatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                try {

                    if (edt.isPerformingCompletion()) {
                        return;
                    }
                    userTypedString = edt.getText().toString();

                    if (userTypedString.endsWith(BARCODE_STRING_PREFIX)) {


                        duplicatebarcodedialog = dbController.gethualreadyscanned(userTypedString);
                        if (duplicatebarcodedialog.size() >= 1) {


                            edt1.setText(duplicatebarcodedialog.get(0));
                        }


                    }
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        };

        edt.addTextChangedListener(barcodetextwatcher);

        dialogBuilder.setTitle("Update Or Delete Previous Barcode");
        dialogBuilder.setMessage("Please Scan Barcode below");
        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dbController.updateExistinghustatus(userTypedString,edt1.getText().toString());
                quantity =    dbController.getQuantity(GandolaNo.getText().toString());

                totalcount.setText(quantity.get(0));

                // String newqty = String.valueOf(Integer.parseInt(duplicatebarcodedialog.get(0)) + 1);
                Toast.makeText(ScanActivity.this, " Quantity Updated", Toast.LENGTH_SHORT).show();


            }
        });
        dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dbController.removeSingleContact(userTypedString);
                quantity =    dbController.getQuantity(GandolaNo.getText().toString());

                totalcount.setText(quantity.get(0));

                Toast.makeText(ScanActivity.this, "Barcode Deleted", Toast.LENGTH_SHORT).show();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }



    public void Savebuttondialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("You Can't Do the changes once the file is saved");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to save it");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {


                        new Exportgandolascanning().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {

                        new Exportgandolascanning().execute();

                    }
                    GandolaNo.setText("");
                    totalcount.setText("");
                    gandolatextview.setText("");
                    gandolaedit.setVisibility(View.VISIBLE);
                    gandolatext.setVisibility(View.INVISIBLE);
                    countlayout.setVisibility(View.INVISIBLE);
                    scanlayout.setVisibility(View.INVISIBLE);
                    scantextlayout.setVisibility(View.INVISIBLE);






                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public class Exportgandolascanning extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(ScanActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;
        String csvname = GandolaNo.getText().toString();

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();
            dbhelper = new DBController(ScanActivity.this);
        }

        protected Boolean doInBackground(final String... args) {
            recieved = dbhelper.getScanData();
            if (recieved.size() == 0) {
                Log.e("recieved", "" + recieved);
                return false;
            } else {
                File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalPi/");
                Log.e("Nishant", exportDir.getAbsolutePath());
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, STORECODE.concat("_").concat("S").concat("_").concat(csvname).concat("_").concat(Stocktype).concat(datetime().concat(".csv")));
                try {

                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = dbhelper.getScanninggandola();
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while (curCSV.moveToNext()) {
                        String arrStr[] = null;
                        String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                        for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                            mySecondStringArray[i] = curCSV.getString(i);
                        }
                        csvWrite.writeNext(mySecondStringArray);
                    }
                    csvWrite.close();
                    curCSV.close();
                    MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(ScanActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();

                clearfield();
            } else {
                Toast.makeText(ScanActivity.this, "No Gandola file Recieved", Toast.LENGTH_SHORT).show();
            }
        }
    }

        public String Time() {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
            String formattedDate = df.format(c.getTime());
            // formattedDate have current date/time
            // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
            Log.e("Time", formattedDate);
            return formattedDate;
        }

        public String datetime() {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            Format formatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss");

          //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String formattedDate = formatter.format(c.getTime());
            // formattedDate have current date/time
            // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
            Log.e("Time", formattedDate);
            return formattedDate;


        }



}
