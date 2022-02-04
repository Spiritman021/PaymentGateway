package com.tworoot2.paymentgateway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    Button addMoney;
    EditText balanceEdit;
    TextView walletBalance, paymentStatus;
    int finalAmount = 0;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        addMoney = findViewById(R.id.addMoney);
        walletBalance = findViewById(R.id.walletBalance);
        balanceEdit = findViewById(R.id.balanceEdit);
        paymentStatus = findViewById(R.id.paymentStatus);

        paymentStatus.setVisibility(View.GONE);


        SharedPreferences prefs = getSharedPreferences("walletBalance", Context.MODE_PRIVATE);
        String name = prefs.getString("finalAmount", "0");//"No name defined" is the default value.
        walletBalance.setText(name.toString());


        balanceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {


                    if (Integer.parseInt(s.toString()) < 5000) {
                        addMoney.setVisibility(View.VISIBLE);
                        addMoney.setText("Proceed to add ₹" + s);
                    } else {
                        balanceEdit.setError("Max balance should not exceed to 5000");
                    }
                    if (Integer.parseInt(s.toString()) == 0) {
                        addMoney.setVisibility(View.GONE);
                    }

                } else {
                    addMoney.setVisibility(View.GONE);
                    balanceEdit.setHint("Amount");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Checkout.preload(MainActivity.this);


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int finalTotalAmount = Integer.parseInt(balanceEdit.getText().toString());
                startPayment(finalTotalAmount);


            }
        });


    }

    private void startPayment(int Amount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_8wwGf6jYxSCaoH");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name","twoRoot2");
            jsonObject.put("description","twoRoot2");
            jsonObject.put("image","https://play-lh.googleusercontent.com/7897vqzpaq8crWunNxDBSXN03OrpHSusFdx1pZYy2xI-QD541gEzxRqviTALPiPU2ZI=w144-h144-n-rw");
            jsonObject.put("theme.color","#50B6F4");
            jsonObject.put("currency","INR");
            jsonObject.put("amount", Amount*100);

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count",4);

            jsonObject.put("retry", retryObj);



            checkout.open(MainActivity.this,jsonObject);

        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        try {


            paymentStatus.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Payment Successful" + s, Toast.LENGTH_LONG).show();
            paymentStatus.setText("Payment status : ₹" + balanceEdit.getText().toString() + " Added successfully \nPayment ID = " + s);

            walletBalance.setText(String.valueOf(Integer.valueOf(walletBalance.getText().toString()) + Integer.valueOf(balanceEdit.getText().toString())));

            sharedPreferences = getSharedPreferences("walletBalance", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("finalAmount", walletBalance.getText().toString());
            editor.apply();

        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "eRROR : " + e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(MainActivity.this, "Payment Unsuccessful" + s, Toast.LENGTH_LONG).show();
        paymentStatus.setText("Payment status : " + balanceEdit.getText().toString() + "Failed to add \n Payment ID" +s);

    }
}