package com.example.mambafinal.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mambafinal.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductsActivity extends AppCompatActivity {
    private String CategoryName,Description,Pname,Price,Quantity, saveCurrentDate,saveCurrentTime;
    private Button addnewproduct;
    private ImageButton backBtn;
    private ImageView InputProductImage;
    private ProgressDialog loadingBar;
    private EditText InputProductName, InputProductQuantity, InputProductPrice, InputProductDescription;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_products);

        CategoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        addnewproduct = (Button) findViewById(R.id.button_add);
        InputProductImage = (ImageView) findViewById(R.id.circular_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        backBtn = findViewById(R.id.backBtn);
        InputProductQuantity = (EditText) findViewById(R.id.product_Quantity);
        InputProductPrice = (EditText) findViewById(R.id.product_Price);
        InputProductDescription = (EditText) findViewById(R.id.product_Description);
        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        addnewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateProductData();

            }
        });
    }

    private void ValidateProductData()
    {
        Description = InputProductDescription.getText().toString();
        Pname = InputProductName.getText().toString();
        Price = InputProductPrice.getText().toString();
        Quantity = InputProductQuantity.getText().toString();

        if (ImageUri ==null)
        {
            Toast.makeText(this, "Product Image is Required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write Product Description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please write Product Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please write Product Price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Quantity))
        {
            Toast.makeText(this, "Please write Product Quantity", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }


    private void StoreProductInformation()
    {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Dear Admin, Please wait while we are adding the New Product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath =ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        
        final UploadTask uploadTask = filepath.putFile(ImageUri);
        
        //Upload Picture to the Database
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) 
            {
                String message = e.toString();
                Toast.makeText(AdminAddProductsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddProductsActivity.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();


                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddProductsActivity.this, "getting Product Image Url Successfully", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }

                    }
                });

            }
        });
        
        

    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);
        productMap.put("quantity", Quantity);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddProductsActivity.this, AdminActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProductsActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProductsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK  && data!=null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
}