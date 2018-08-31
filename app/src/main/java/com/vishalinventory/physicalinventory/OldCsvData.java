package com.vishalinventory.physicalinventory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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

public class OldCsvData extends AppCompatActivity {

    EditText GandolaNo;
    AutoCompleteTextView Barcode;
    TextView Totalqty,Timeqty,Qty,totalcount,gandolatextview;
    Button Exit,Edit,Save;
  public String Stocktype = null;
    public String Areatype = null;
    public String Floortype = null;
String userTypedString;
    public String Ecode = null;
    private TextWatcher barcodetextwatcher;
    public static final String BARCODE_STRING_PREFIX = "@";
    DBController dbController;
    int count = 1;
    Boolean check;
    ArrayList<String> duplicatebarcode;
    ArrayList<String>quantity;

    ArrayList<String>duplicatebarcodedialog;
    ArrayList<Gandolamodel>gandola;
    android.support.v7.app.ActionBar actionBar;
    int countbarcode = 0;
    Bundle bundle = new Bundle();
    LinearLayout gandolaedit,gandolatext,scanlayout,scantextlayout,countlayout;
    private String Gandolacode,STORECODE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_csv_data);
        dbController = new DBController(getApplicationContext());
        totalcount = (TextView)findViewById(R.id.totalcount) ;

        Qty = (TextView)findViewById(R.id.qty);
        Barcode = (AutoCompleteTextView)findViewById(R.id.barcodeno);
        Exit = (Button)findViewById(R.id.exit);
        Edit = (Button)findViewById(R.id.edit);
        Save = (Button)findViewById(R.id.next);
        // Totalqty = (TextView)findViewById(R.id.totalqty);
        Timeqty = (TextView)findViewById(R.id.timetotal);
        gandolatext = (LinearLayout)findViewById(R.id.gandolatext);
        gandolatextview = (TextView) findViewById(R.id.gandolanotext);
        scanlayout = (LinearLayout) findViewById(R.id.scanlayout);
        scantextlayout = (LinearLayout)findViewById(R.id.scantextlayout);
        countlayout = (LinearLayout)findViewById(R.id.countlayout);


        gandola = dbController.getalldatainvoice();

        STORECODE = gandola.get(0).getStoreName();
        Gandolacode = gandola.get(0).getGandolano();
        Stocktype = gandola.get(0).getStocktype();
        Areatype = gandola.get(0).getAreatype();
        Ecode = gandola.get(0).getEcode();
        Floortype = gandola.get(0).getFloor();
        quantity = dbController.getQuantity(Gandolacode);
        totalcount.setText(quantity.get(0));

        Log.e("Value",Gandolacode +""+ Stocktype +""+ Areatype +""+ Ecode +""+ Floortype +""+ STORECODE);

        gandolatextview.setText(String.valueOf(Gandolacode));






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

                            Toast.makeText(OldCsvData.this, newqty, Toast.LENGTH_SHORT).show();

                            dbController.updateExistinghustatus(userTypedString,newqty);
                            Barcode.setText("");
                            quantity = dbController.getQuantity(Gandolacode);
                            //   countbarcode++;
                            totalcount.setText(quantity.get(0));

                        } else {
                            dbController.Insertgateentry(STORECODE, Stocktype, Ecode, Areatype,Floortype,Gandolacode, Barcode.getText().toString(), Qty.getText().toString(), Timeqty.getText().toString());
                            quantity =    dbController.getQuantity(Gandolacode);
                            // String newcount = String.valueOf(Integer.parseInt(totalcount.getText().toString()) + 1);

                            Barcode.setText("");
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
                quantity =    dbController.getQuantity(Gandolacode);

                totalcount.setText(quantity.get(0));

                // String newqty = String.valueOf(Integer.parseInt(duplicatebarcodedialog.get(0)) + 1);
                Toast.makeText(OldCsvData.this, " Quantity Updated", Toast.LENGTH_SHORT).show();


            }
        });
        dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dbController.removeSingleContact(userTypedString);
                quantity =    dbController.getQuantity(Gandolacode);

                totalcount.setText(quantity.get(0));

                Toast.makeText(OldCsvData.this, "Barcode Deleted", Toast.LENGTH_SHORT).show();

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
                    totalcount.setText("");
                    gandolatextview.setText("");







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

        private final ProgressDialog dialog = new ProgressDialog(OldCsvData.this);
        ArrayList<String> recieved = new ArrayList<>();
        ArrayList<Gandolamodel> gandolamodels  = new ArrayList<>();
        DBController dbhelper = new DBController(OldCsvData.this);




        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();



        }

        protected Boolean doInBackground(final String... args) {
            recieved = dbhelper.getScanData();
//            gandolamodels = dbhelper.getalldatainvoice();
//            STORECODE = gandola.get(0).getStoreName();
//            Gandolacode = gandola.get(0).getGandolano();
//            Stocktype = gandola.get(0).getStocktype();
            if (recieved.size() == 0) {
                Log.e("recieved", "" + recieved);
                return false;
            } else {
                File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalPi/");
                Log.e("Nishant", exportDir.getAbsolutePath());
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, STORECODE.concat("_").concat("S").concat("_").concat(Gandolacode).concat("_").concat(Stocktype).concat(datetime().concat(".csv")));
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
                Toast.makeText(OldCsvData.this, "Export successful!", Toast.LENGTH_SHORT).show();

                clearfield();
            } else {
                Toast.makeText(OldCsvData.this, "No Gandola file Recieved", Toast.LENGTH_SHORT).show();
            }
        }
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


