package edu.citadel.nmcanall.tipcalculator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class TipPane extends AppCompatActivity {

    private EditText billAmount;
    private EditText customTipAmount;
    private RadioGroup radioBts;
    private TextView tipAmountTV;
    private TextView totalAmountTV;
    private Switch roundUpSwitch;
    private int tipPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_pane);

        setDefaults();
        monitorActions();

        billAmount.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



    // Input controls in the view
    private void setDefaults() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        billAmount = (EditText) findViewById(R.id.billAmount);
        customTipAmount = (EditText) findViewById(R.id.customTipEditText);
        radioBts = (RadioGroup) findViewById(R.id.radioGroup);
        tipAmountTV = (TextView) findViewById(R.id.tipAmountTextView);
        totalAmountTV = (TextView) findViewById(R.id.totalAmountTextView);
        roundUpSwitch = (Switch) findViewById(R.id.roundUpTotalSwitch);
    }




    // Actions taken when a widget is clicked
    private void monitorActions() {

        billAmount.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Actions for a radio button selection
        radioBts.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int checkedID) {
                if(billAmount.getText().toString().equals(""))
                    return;
                customTipAmount.setText("");
                customTipAmount.setEnabled(false);
                switch (checkedID) {
                    case R.id.radioButton10:
                        tipPercent = 10;
                        setTipPercent(.10);
                        break;
                    case R.id.radioButton15:
                        tipPercent = 15;
                        setTipPercent(.15);
                        break;
                    case R.id.radioButton18:
                        tipPercent = 18;
                        setTipPercent(.18);
                        break;
                    case R.id.radioButton20:
                        tipPercent = 20;
                        setTipPercent(.20);
                        break;
                    case R.id.radioButtonCustom:
                        customTipAmount.requestFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        handleCustomTip();
                        break;
                }
            }
        });

        // Actions if the roundUpSwitch is triggered
        roundUpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (totalAmountTV.equals("")) {
                    return;
                }
                roundUp();
            }
        });
    }



    // Method to hanle the custom tip function
    private void handleCustomTip() {
        customTipAmount.setEnabled(true);
        customTipAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    double percent = .01 * Double.parseDouble(customTipAmount.getText().toString());
                    setTipPercent(percent);
                    tipPercent = (int) (100 * percent);
                    customTipAmount.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    return true;
                }
                return false;
            }
        });
    }



    // Method to calculate tip and put in text view
    private void setTipPercent(double tipPercent) {

        double bill = Double.parseDouble(billAmount.getText().toString());
        double tip = tipPercent * bill;
        double total = (1 + tipPercent) * bill;

        if (roundUpSwitch.isChecked()) {
            roundUp();
            return;
        }

        tipAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(tip));
        totalAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(total));

    }



    // Method to round up the total for given values
    private void roundUp() {

        // Convert strings from text view and edit text to a double
        String billS = billAmount.getText().toString();
        billS = billS.replace("$", "").replace(",", "");

        double bill = Double.parseDouble(billS);
        double tip = bill * .01 * tipPercent;
        double total = bill + tip;

        // Round up the total and adjust the tip
        if (roundUpSwitch.isChecked() && total - (int) total != 0) {
            total = (int) total + 1;
            tip = total - bill;
        }

        // Set the values
        tipAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(tip));
        totalAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(total));

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tip_pane, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // Save and restore instance state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String totalS = totalAmountTV.getText().toString();
        String tipS = tipAmountTV.getText().toString();

        totalS = totalS.replace("$", "").replace(",", "");
        tipS = tipS.replace("$", "").replace(",", "");

        double total = Double.parseDouble(totalS);
        double tip = Double.parseDouble(tipS);

        outState.putDouble("saved_total", total);
        outState.putDouble("saved_tip", tip);
        outState.putInt("saved_tip_percent", tipPercent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        double total = inState.getDouble("saved_total");
        double tip = inState.getDouble("saved_tip");
        tipPercent = inState.getInt("saved_tip_percent");

        totalAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(total));
        tipAmountTV.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(tip));
    }

}
