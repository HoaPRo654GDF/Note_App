package com.example.note_app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.note_app.R;
import com.example.note_app.adapters.NotesAdapter;
import com.example.note_app.database.NotesDatabase;
import com.example.note_app.entities.Note;
import com.example.note_app.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1; // để xác định request code khi thêm ghi chú mới.
    public static final int REQUEST_CODE_UPDATE_NOTE = 2; // để xác định request code khi cập nhật ghi chú.
    public static final int REQUEST_CODE_SHOW_NOTES = 3; // xác định request code khi hiển thị danh sách ghi chú.
    public static final int REQUEST_CODE_SELECT_IMAGE=4; //để xác định request code khi chọn hình ảnh.
    public static final int REQUEST_CODE_STORAGE_PERMISSION=5; //để xác định request code khi yêu cầu quyền truy cập vào bộ nhớ.

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1; // để lưu vị trí ghi chú được chọn trong danh sách.

    private AlertDialog dialogAddURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        // xử lí sự kiện khi người dùng thêm ghi chú chính.
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        //Thiết lập giao diện RecyclerView.
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        //Gắn một LayoutManager của StaggeredGridLayoutManager vào RecyclerView để hiển thị danh sách ghi chú theo kiểu dạng lưới.
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        //Khởi tạo một danh sách ghi chú rỗng (noteList) và một NotesAdapter để quản lý việc hiển thị danh sách ghi chú.
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        //Gán notesAdapter cho RecyclerView để hiển thị danh sách ghi chú.
        notesRecyclerView.setAdapter(notesAdapter);

        //Gọi phương thức getNotes() để lấy danh sách ghi chú từ cơ sở dữ liệu.
        //Truyền REQUEST_CODE_SHOW_NOTES và false vào phương thức để chỉ định rằng yêu cầu lấy danh sách ghi chú là để hiển thị chúng.
        getNotes(REQUEST_CODE_SHOW_NOTES, false);


        //Xử lí sự kiện tìm kiếm
        EditText inputSearch = findViewById(R.id.inputSearch);

        //Gắn một TextWatcher vào EditText để theo dõi sự thay đổi trong nội dung tìm kiếm.
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            // Được gọi trước khi văn bản trong EditText thay đổi
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            //Được gọi khi văn bản trong EditText thay đổi.
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //hủy bỏ bất kỳ công việc tìm kiếm trước đó đang chạy trên notesAdapter.
                notesAdapter.cancelTimer();
            }

            @Override
            //Được gọi sau khi văn bản trong EditText đã thay đổi
            public void afterTextChanged(Editable editable) {
                if (noteList.size() != 0) {
                    //tìm kiếm các ghi chú dựa trên văn bản mới đã thay đổi.
                    notesAdapter.searchNotes(editable.toString());
                }
            }
        });

        // xử lí sự kiện thêm ghi chú
        findViewById(R.id.imageAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
       //

        // xử lí sự kiện thêm hình ảnh as creatNoteActivity 407
        findViewById(R.id.imageAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.
                        checkSelfPermission( getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED  )
                {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String []{ Manifest.permission.READ_EXTERNAL_STORAGE },
                            REQUEST_CODE_STORAGE_PERMISSION  );
                } else
                {
                    selectImage();
                }
            }
        });
        //End

        //Xử lý sự kiện thêm đường dẫn web as creatNoteActivity 435
        findViewById(R.id.imageAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddURLDialog();
            }
        });
        // end
    }
    // As creatNoteActivity 543
    private  void selectImage()
    {      Log.d("MYLOG","Starting Select-Image ");

        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
    // END

    //as createNoteActivity 554

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0 )
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this, " Quyền bị từ chối !!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //end p

    // as CreateNoteActivity 626
    private  String getPathFromUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);

        if (cursor == null)
        {
            filePath = contentUri.getPath();
            Log.d("MYLOG","Cursor NULL thi");
        }
        else{
            Log.d("MYLOG","Cursor Moving To First");
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return  filePath;
    }
    //end


    // xử lý sự kiện khi người dùng nhấp vào một ghi chú trong danh sách (onNoteClicked)
    @Override
    public void onNoteClicked(Note note, int position) {

        // phương thức được gọi khi một ghi chú được nhấp vào trong danh sách. Nó lấy ghi chú và vị trí tương ứng của nó. Sau đó, nó tạo một Intent để chuyển đến CreateNoteActivity để xem hoặc cập nhật ghi chú. Dữ liệu ghi chú và trạng thái xem hoặc cập nhật được chuyển đi qua Intent bằng phương thức putExtra. Khi hoạt động CreateNoteActivity kết thúc, kết quả sẽ được trả về với mã yêu cầu REQUEST_CODE_UPDATE_NOTE.
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    //lấy danh sách ghi chú từ cơ sở dữ liệu.
    private void getNotes(final int requestCode, final boolean isNoteDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            //trong phương thức doInBackground, danh sách ghi chú được truy xuất từ cơ sở dữ liệu bằng cách sử dụng lớp NotesDatabase. Phương thức getAllNotes được gọi để truy vấn tất cả các ghi chú từ cơ sở dữ liệu.
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            //phương thức onPostExecute được gọi với danh sách ghi chú nhận được từ doInBackground là tham số.
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                //Nếu requestCode là REQUEST_CODE_SHOW_NOTES, danh sách ghi chú được thêm vào danh sách hiện tại (noteList), và notesAdapter được thông báo để cập nhật giao diện hiển thị danh sách ghi chú.
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();

                }//Nếu requestCode là REQUEST_CODE_ADD_NOTE, ghi chú mới được thêm vào đầu danh sách (noteList), và notesAdapter được thông báo để cập nhật giao diện. Cuộn danh sách đến vị trí đầu tiên để hiển thị ghi chú mới.
                else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);

                }
                //Nếu requestCode là REQUEST_CODE_UPDATE_NOTE, ghi chú tại vị trí noteClickedPosition trong danh sách bị loại bỏ. Nếu isNoteDeleted là true, notesAdapter được thông báo rằng ghi chú đã bị xóa và giao diện được cập nhật.
                else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);

                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }
                    //Nếu không, ghi chú được chèn lại vào danh sách tại vị trí noteClickedPosition, và notesAdapter được thông báo để cập nhật giao diện với ghi chú đã được cập nhật.
                    else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

            }
        }

        new GetNotesTask().execute();
    }

    //Phương thức onActivityResult được gọi khi một hoạt động con kết thúc và trả về kết quả cho hoạt động gốc.
    // sử dụng để xử lý kết quả trả về từ các hoạt động con để cập nhật danh sách ghi chú.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // kiểm tra xem hoạt động con đã trả về kết quả thành công và request code có phải là REQUEST_CODE_ADD_NOTE hay không. Nếu đúng, gọi hàm getNotes() để cập nhật danh sách ghi chú.
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        }
        //kiểm tra xem hoạt động con đã trả về kết quả thành công và request code có phải là REQUEST_CODE_UPDATE_NOTE hay không. Nếu đúng, kiểm tra xem Intent data có tồn tại hay không và lấy giá trị của trường boolean "isNoteDeleted" từ Intent. Sau đó, gọi hàm getNotes() để cập nhật danh sách ghi chú và truyền giá trị "isNoteDeleted" vào hàm.
        else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
        //kiểm tra xem hoạt động con đã trả về kết quả thành công và request code có phải là REQUEST_CODE_SELECT_IMAGE hay không. Nếu đúng, kiểm tra xem Intent data có tồn tại hay không và lấy Uri của hình ảnh được chọn. Nếu Uri tồn tại, lấy đường dẫn của hình ảnh bằng cách gọi hàm getPathFromUri().Sau đó, tạo một Intent mới để mở hoạt động CreateNoteActivity và truyền thông tin về loại quick action (isFromQuickActions và quickActionType) và đường dẫn hình ảnh (imagePath). Cuối cùng, gọi startActivityForResult() để bắt đầu hoạt động con và truyền request code REQUEST_CODE_ADD_NOTE.
        else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data!=null)
            {
                Uri selectedImageUri=data.getData();
                if(selectedImageUri!=null){
                    try {
                        String selectedImagePath=getPathFromUri(selectedImageUri);
                        Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions",true);
                        intent.putExtra("quickActionType","image");
                        intent.putExtra("imagePath",selectedImagePath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);

                    }catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    //as CreateNoteActivity 655
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Nhập URL", Toast.LENGTH_SHORT).show();
                    } else if (! Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(MainActivity.this, "URL không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                       dialogAddURL.dismiss();
                        Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions",true);
                        intent.putExtra("quickActionType","URL");
                        intent.putExtra("URL",inputURL.getText().toString());
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    }

                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });


        }

        dialogAddURL.show();
    }

}
