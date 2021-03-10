package com.example.mambafinal.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mambafinal.Client.HomeActivity;
import com.example.mambafinal.Client.MainActivity;
import com.example.mambafinal.R;

public class AdminActivity extends AppCompatActivity {

    private ImageView adminImage;
    private ImageView viewOrders;
    private Button admin_logout;
    private ImageView adminMaintain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toast.makeText(this, "Welcome Admin...", Toast.LENGTH_SHORT).show();
        viewOrders = (ImageView) findViewById(R.id.admin_new_product);
        admin_logout = (Button) findViewById(R.id.admin_log_out);
        adminMaintain = (ImageView) findViewById(R.id.admin_maintain);

        adminMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        admin_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });
        adminImage = (ImageView) findViewById(R.id.admin_image);

        adminImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminActivity.this, AdminAddProductsActivity.class);
                intent.putExtra("category","adminImage");
                startActivity(intent);
            }
        });
    }
}