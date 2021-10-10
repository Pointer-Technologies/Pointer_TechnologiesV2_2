package com.example.pointer_technologiesv2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class AppCenterActivity extends AppCompatActivity {
    private static final String TAG = "kekw";
    private final int REQUEST_CODE_GALLERY = 999;
    private final int REQUEST_CALL = 1;
    private MaterialToolbar topAppBar;
    private RecyclerView devicesRecView;
    private final List<Device> devices = new ArrayList<>();
    private Button buttonChangeAvatar;
    private ImageView imageViewAppCenter;
    private FloatingActionButton floatingActionButtonAdd;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private DatabaseReference databaseReferenceImages, databaseReferenceUserDevices;
    private StorageTask uploadTask;
    private FirebaseUser firebaseUser;
    private DevicesRecViewAdapter adapter;
    private TextView textAppCenter;


    // Takes care of initializing the Toolbar menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        MenuItem checkable = menu.findItem(R.id.nightModeTopBar);
        Drawable drawable = checkable.getIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.colorTitle), PorterDuff.Mode.SRC_ATOP);
        SharedPreferences preferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        checkable.setChecked(preferences.getBoolean("nightMode", false));

        if (preferences.getBoolean("nightMode", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        return true;
    }

    // Sets up the Toolbar action button functionalities:
    // Change theme, add device, ask for help and sign out.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTopBar:
                Intent intent = new Intent(AppCenterActivity.this, AddDeviceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.nightModeTopBar:
                // Change the "isChecked" status and the SharedPreferences accordingly.
                item.setChecked(!item.isChecked());
                SharedPreferences preferences_night = getSharedPreferences("nightMode", MODE_PRIVATE);
                SharedPreferences.Editor editor_night = preferences_night.edit();

                // Override pending transition, since the Activity is recreated by default.
                overridePendingTransition(0, 0);

                if (!preferences_night.getBoolean("nightMode", false)) {
                    Toast.makeText(this, "Night mode option selected", Toast.LENGTH_SHORT).show();
                    editor_night.putBoolean("nightMode", true);
                    editor_night.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    Toast.makeText(this, "Light Mode option selected.", Toast.LENGTH_SHORT).show();
                    editor_night.putBoolean("nightMode", false);
                    editor_night.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Override pending transition again, since Android acts up with changing the theme.
                startActivity(new Intent(this, getClass()));
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                break;

            case R.id.helpTopBar:
                helpDialog();
                break;

            // Sign user out and navigate to the Sign In Screen.
            case R.id.signOutTopBar:
                SharedPreferences preferences = getSharedPreferences("remember", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("remember", false);
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent2 = new Intent(AppCenterActivity.this, SignInActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // Asks user for the necessary permissions: Make phone calls and access storage.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Navigate to the gallery.
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "You don't have permission to access file location.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(AppCenterActivity.this, "You don't have permission to access phone calls.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    // Called whenever startActivityForResult() is called by the Fragment class.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON) // Add guidelines.
                .setAspectRatio(1, 1) // Square image.
                .start(this); // Start activity.
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();



                if (resultUri != null) {
                    // Convert given medium to ImageView.
                    GlideApp.with(this).load(resultUri).into(imageViewAppCenter);
                    StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "." + "jpg");

                    // Upload file and use a ProgressBar.
                    uploadTask = fileReference.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (uploadTask != null && uploadTask.isInProgress()) {
                                    Toast.makeText(AppCenterActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                                } else {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);
                                    progressBar.setVisibility(View.GONE);

                                    // Creating new Database entry.
                                    /*Upload upload = new Upload(firebaseUser.getDisplayName(), taskSnapshot.getUploadSessionUri().toString());
                                    databaseReferenceImages.child(firebaseUser.getUid()).setValue(upload);
                                     */

                                    Toast.makeText(AppCenterActivity.this, "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress((int) progress);
                        }
                    });
                } else {
                    // Notify the user that no image was selected.
                    Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                result.getError().printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Used to rearrange the list of device tabs, by letting the user move them vertically.
    ItemTouchHelper.SimpleCallback moveUpDown = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(devices, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    // Invoked when a device tab is swiped right. Produces the setup dialog.
    ItemTouchHelper.SimpleCallback swipeToEdit = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();

            // Bundle containing the position of the device to be set up.
            Bundle bundle = new Bundle();
            bundle.putInt("position", itemPosition);

            SetupDialog setupDialog = new SetupDialog();
            setupDialog.setArguments(bundle);

            setupDialog.show(getSupportFragmentManager(), null);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(AppCenterActivity.this, c, recyclerView, viewHolder,
                dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(Color.parseColor("#3700B3"))
                .addActionIcon(R.drawable.ic_setup)
                .setIconHorizontalMargin(70)
                .addSwipeRightLabel("Setup")
                .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18)
                .setSwipeRightLabelColor(R.color.black)
                .create()
                .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    // Invoked when a device tab is swiped left. Produces the delete dialog.
    ItemTouchHelper.SimpleCallback swipeToDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int pos = viewHolder.getAdapterPosition();
            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AppCenterActivity.this);

            builder.setTitle("Deleting item")
                .setMessage(R.string.confirmDelete)
                .setNegativeButton(R.string.cancelDeleteDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AppCenterActivity.this, AppCenterActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton(R.string.deleteDeleteDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReferenceUserDevices.child(firebaseUser.getUid()).child(devices.get(pos).getId()).removeValue();
                        devices.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                }).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(AppCenterActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(Color.parseColor("#BE1616"))
                .addActionIcon(R.drawable.ic_delete_black)
                .setIconHorizontalMargin(70)
                .addSwipeLeftLabel("Delete")
                .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18)
                .setSwipeLeftLabelColor(R.color.black)
                .create()
                .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_center);

        initializeViews();
        setSupportActionBar(topAppBar);

        adapter = new DevicesRecViewAdapter(AppCenterActivity.this);

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<Device> devices) {
                // Initialize and set up adapter.
                adapter.setDevices(devices);
                devicesRecView.setLayoutManager(new LinearLayoutManager(AppCenterActivity.this));
                devicesRecView.setAdapter(adapter);
            }
        });

        final StorageReference storage = storageReference.child(firebaseUser.getUid() + ".jpg");
            storage.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(AppCenterActivity.this).load(storage).into(imageViewAppCenter);
                        Toast.makeText(AppCenterActivity.this, "Image loaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        // Add a FloatingActionButton, so long as the software version is compatible with it.
        if (Build.VERSION.SDK_INT >= 23) {
            devicesRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView devicesRecView, int dx, int dy) {

                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView devicesRecView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        floatingActionButtonAdd.show();
                    else
                        floatingActionButtonAdd.hide();
                    super.onScrollStateChanged(devicesRecView, newState);
                }
            });
        }

        new ItemTouchHelper(swipeToEdit).attachToRecyclerView(devicesRecView);
        new ItemTouchHelper(swipeToDelete).attachToRecyclerView(devicesRecView);
        new ItemTouchHelper(moveUpDown).attachToRecyclerView(devicesRecView);


        // Set up the adapter's onClickListeners.
        // onSoundClick and onVibrationClick are setup in the custom RecyclerView class.
        adapter.setOnItemClickListener(new DevicesRecViewAdapter.onItemClickListener() {

            @Override
            public void onVibrationClick(int position) {
                Toast.makeText(AppCenterActivity.this, "Vibration button selected.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSoundClick(int position) {
                Toast.makeText(AppCenterActivity.this, "Sound button selected.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                final int pos = position;
                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AppCenterActivity.this);

                builder.setTitle("Deleting item")
                    .setMessage("Are you sure you want to delete this device ?\nYou will be able to use the 8-digit code again to add device.")
                    .setNegativeButton(R.string.cancelDeleteDialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton(R.string.deleteDeleteDialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReferenceUserDevices.child(firebaseUser.getUid()).child(devices.get(pos).getId()).removeValue();
                            devices.remove(pos);
                            adapter.notifyItemRemoved(pos);
                        }
                    })
                    .show();
            }

            @Override
            public void onEditClick(int position) {
                // Pass the position integer to SharedPreferences, for it to be used by the custom RecyclerView class
                // and EditDeviceActivity. The integer is passed so that the app knows which device to update.
                SharedPreferences preferences = getSharedPreferences("position", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("position", position);
                editor.apply();

                Intent intent = new Intent(AppCenterActivity.this, EditDeviceActivity.class);
                intent.putExtra("oldName", devices.get(position).getName());
                intent.putExtra("deviceId", devices.get(position).getId());
                intent.putExtra("code", devices.get(position).getIconCode());
                startActivity(intent);
            }

            /**
             * This section of the device tab was to be used for a more advanced, outdoors prototype.
             */
            @Override
            public void onLocationClick(int position) {
                Toast.makeText(AppCenterActivity.this, "Location selected for item " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the "Change my avatar" button.
        buttonChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AppCenterActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });

        // Lastly, set up the "Add device" button on the bottom right.
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppCenterActivity.this, AddDeviceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // Set up custom transitions to the SignInActivity.
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Left blank to do nothing when the back button is pressed.
    @Override
    public void onBackPressed() {

    }

    private interface FirebaseCallback {
        void onCallback(List<Device> devices);
    }

    // Gets the current user's devices.
    private void readData(final FirebaseCallback firebaseCallback) {

        databaseReferenceUserDevices.child(Objects.requireNonNull(FirebaseAuth.getInstance()
            .getCurrentUser())
            .getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    devices.clear();
                    for (DataSnapshot devicesSnapshot : snapshot.getChildren()) {
                        Device device = devicesSnapshot.getValue(Device.class);
                        devices.add(device);
                    }
                    firebaseCallback.onCallback(devices);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();
                }
            });
    }


    // Builds the help dialog, containing two prompts, call  and mail support.
    private void helpDialog() {
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setOnClickListener(new HelpDialog.onClickListener() {
            @Override
            public void onEmailClick() {
                sendMail();
            }

            @Override
            public void onPhoneClick() {
                makePhoneCall();
            }
        });
        helpDialog.show(getSupportFragmentManager(), "help dialog");
    }


    private void sendMail() {
        String subject = "";
        String message = "";
        String[] recipients = {"CompanyEmail@gmail.com"};

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose"));
    }


    private void makePhoneCall() {
        String number = "+301234567890";       //ΔΕ ΠΑΙΡΝΕΙ ΤΗΛ ΜΗΝ ΑΓΧΩ ΠΑΤΗΣΤΕ ΤΟ
        if (ContextCompat.checkSelfPermission(AppCenterActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AppCenterActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void initializeViews() {

        buttonChangeAvatar = findViewById(R.id.buttonAppCenter);
        topAppBar = findViewById(R.id.topAppBar);
        textAppCenter = findViewById(R.id.textAppCenter);
        imageViewAppCenter = findViewById(R.id.imageViewAppCenter);
        floatingActionButtonAdd = findViewById(R.id.floatingActionButtonAdd);
        storageReference = FirebaseStorage.getInstance().getReference("images_uploads");
        databaseReferenceImages = FirebaseDatabase.getInstance().getReference("images_uploads");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReferenceUserDevices = FirebaseDatabase.getInstance().getReference("devices");
        progressBar = findViewById(R.id.progressBar);
        devicesRecView = findViewById(R.id.devicesRecView);
        textAppCenter.setText(getString(R.string.hey, firebaseUser.getDisplayName()));
    }
}