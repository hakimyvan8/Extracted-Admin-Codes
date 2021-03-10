package com.example.mambafinal.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mambafinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainActivity extends AppCompatActivity
{
    private Button applyChanges, deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;
    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChanges = (Button) findViewById(R.id.maintain_changes);
        name = (EditText) findViewById(R.id.maintain_product_name);
        price = (EditText) findViewById(R.id.maintain_price);
        description = (EditText) findViewById(R.id.maintain_Description);
        imageView = (ImageView) findViewById(R.id.maintain_image);
        deleteBtn = (Button) findViewById(R.id.maintain_delete);
        
        displaySpecificProductInfo();


        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                applyChange();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteThisProduct();
            }
        });
    }

    private void deleteThisProduct()
    {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Intent intent = new Intent(AdminMaintainActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminMaintainActivity.this, "The Product is deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChange()
    {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if (pName.equals(""))
        {
            Toast.makeText(this, "Write down Product Name", Toast.LENGTH_SHORT).show();
        }
        else  if (pPrice.equals(""))
        {
            Toast.makeText(this, "Write down Product Price", Toast.LENGTH_SHORT).show();
        }
        else  if (pDescription.equals(""))
        {
            Toast.makeText(this, "Write down Product Description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainActivity.this, "Changes applied successfully..", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo()
    {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    String pName = snapshot.child("pname").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pDescription = snapshot.child("description").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();


                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);

                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}