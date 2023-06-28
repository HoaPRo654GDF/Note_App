package com.example.note_app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.note_app.R;
import com.example.note_app.database.NotesDatabase;
import com.example.note_app.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity{

    //khai báo các biến
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;

    private String selectedNoteColor;   // for note color
    private View subtitleIndicator;

    private ImageView imageNote;  // for image
    private  String selectedImagePath;

    private TextView textWebURL;  // for URL
    private  LinearLayout layoutWebURL;

    private AlertDialog dialogAddURL;
    private  AlertDialog dialogDelete; // for delete for note

    private Note alreadyAvailableNote;


    private  static  final  int REQUEST_CODE_STORAGE_PERMISSION = 1; ////Mã yêu cầu (request code) để xin quyền truy cập vào bộ nhớ.
    private static final int REQUEST_CODE_SELECT_IMAGE = 2; //Mã yêu cầu để chọn hình ảnh từ bộ nhớ.

    private static boolean isBottomSheetExpanded=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //Xử lý sự kiện khi người dùng nhấn nút back
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Ánh xạ các thành phần giao diện
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        subtitleIndicator=findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);

        textWebURL=findViewById(R.id.textWebURL);
        layoutWebURL=findViewById(R.id.layoutWebURL);

        //Thiết lập ngày và giờ hiện tại
        textDateTime.setText(
                new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );

        //Xử lý sự kiện khi người dùng nhấn nút lưu ghi chú
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectedNoteColor = "#333333";
        selectedImagePath = "null";

        //Lấy dữ liệu từ Intent
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        //Xử lý sự kiện khi người dùng nhấn nút xóa url web.
        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });

        // //Xử lý sự kiện khi người dùng nhấn nút xóa ảnh.
        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath="null";
            }
        });

        //*kiểm tra sự kiện nhấn nút "ActionImage" trong một OnClickListener. kiểm tra các dữ liệu được truyền qua từ Intent và thực hiện các hành động tương ứng dựa trên dữ liệu

        // kiểm tra xem Intent có chứa một giá trị Boolean với khóa "isFromQuickActions" hay không. getBooleanExtra("isFromQuickActions", false) sẽ trả về giá trị Boolean tương ứng với khóa "isFromQuickActions" từ Intent. Nếu không có giá trị nào được tìm thấy, giá trị mặc định là false
        if (getIntent().getBooleanExtra("isFromQuickActions",false)){
            // lấy giá trị String tương ứng với khóa "quickActionType" từ Intent và gán vào biến type. getStringExtra("quickActionType") trả về giá trị String tương ứng với khóa "quickActionType" từ Intent. Nếu không có giá trị nào được tìm thấy, giá trị null được gán cho biến type.
            String type=getIntent().getStringExtra("quickActionType");
            // kiểm tra xem biến type có khác null hay không.Nếu type không phải là null, nghĩa là Intent chứa giá trị String với khóa "quickActionType".
            if (type!=null){
                //\ kiểm tra giá trị của biến type để xác định loại hành động nhanh được thực hiện.Nếu type là "image", nghĩa là hành động nhanh là liên quan đến hình ảnh.Nếu type là "URL", nghĩa là hành động nhanh là liên quan đến URL.
                if (type.equals("image")){
                    //gán giá trị String tương ứng với khóa "imagePath" từ Intent cho biến selectedImagePath.
                    selectedImagePath=getIntent().getStringExtra("imagePath");
                    //sử dụng thư viện Glide để tải hình ảnh từ selectedImagePath và hiển thị nó trong imageNote
                    Glide.with(this).load(selectedImagePath).into(imageNote);
                    //đặt sự hiển thị của imageNote thành VISIBLE để hiển thị hình ảnh.
                    imageNote.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                }else if (type.equals("URL")){
                    textWebURL.setText(getIntent().getStringExtra("URL"));
                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }
        // end

        initMiscellaneous();
        setsubtitleIndicatorColor();
    }

   // thiết lập hoặc cập nhật giao diện của ghi chú dựa trên dữ liệu đã có sẵn.
    private  void setViewOrUpdateNote(){

        //thiết lập văn bản cho
        inputNoteTitle.setText(alreadyAvailableNote.getTitle()); //bằng tiêu đề của ghi chú
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle()); //bằng phụ đề của ghi chú
        inputNoteText.setText(alreadyAvailableNote.getNoteText()); //bằng nội dung của ghi chú
        textDateTime.setText(alreadyAvailableNote.getDateTime());// bằng thời gian của ghi chú
        selectedNoteColor=alreadyAvailableNote.getColor();
        //Gán giá trị màu được chọn của ghi chú alreadyAvailableNote vào selectedNoteColor.
        setsubtitleIndicatorColor();
        //Gọi phương thức setsubtitleIndicatorColor() để đặt màu cho chỉ báo phụ đề tương ứng với selectedNoteColor.

        //Kiểm tra xem đường dẫn hình ảnh của ghi chú alreadyAvailableNote có khác "null" hay không.
        if( ! alreadyAvailableNote.getImagePath().equals("null") ){
            //Nếu khác, sử dụng thư viện Glide để tải hình ảnh từ đường dẫn alreadyAvailableNote.getImagePath() và hiển thị nó trong imageNote (giả sử là một ImageView).
            Glide.with(CreateNoteActivity.this).load(alreadyAvailableNote.getImagePath()).into(imageNote);
            //Đặt sự hiển thị của imageNote thành VISIBLE để hiển thị hình ảnh.
            imageNote.setVisibility(View.VISIBLE);
            //Đặt sự hiển thị của view có ID là "imageRemoveImage" thành VISIBLE để hiển thị một nút xóa hình ảnh.
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            //Gán đường dẫn hình ảnh của ghi chú alreadyAvailableNote vào selectedImagePath
            selectedImagePath=alreadyAvailableNote.getImagePath();
        }

        //Kiểm tra xem liên kết web của ghi chú alreadyAvailableNote có khác null và không rỗng hay không.
        if(alreadyAvailableNote.getWebLink()!=null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            //Nếu không, thiết lập văn bản cho textWebURL (giả sử là một TextView) bằng liên kết web của ghi chú alreadyAvailableNote.
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            //Đặt sự hiển thị của layoutWebURL thành VISIBLE để hiển thị WebURL.
            layoutWebURL.setVisibility(View.VISIBLE);
        }
    }

    //luu ghi chú
    private  void saveNote()
    {
        //Kiểm tra xem văn bản nhập vào trong inputNoteTitle (giả sử là một EditText) có trống không.
        if(inputNoteTitle.getText().toString().trim().isEmpty())
        {
            //Nếu trống, hiển thị một thông báo ngắn thông báo rằng "Tiêu đề ghi chú không được để trống!"
            Toast.makeText(this, "Tiêu đề ghi chú không thể trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Kiểm tra xem cả phụ đề và nội dung ghi chú trong inputNoteSubtitle và inputNoteText (giả sử là các EditText) có trống không.
        else if( inputNoteSubtitle.getText().toString().trim().isEmpty()
                 && inputNoteText.getText().toString().trim().isEmpty() )
        {//nếu cả hai đều trống, hiển thị một thông báo ngắn thông báo rằng "Phụ đề không được để trống!"
            Toast.makeText(this, "Phụ đề không thể trống!", Toast.LENGTH_SHORT).show();
             return;
        }

        //Tạo một đối tượng Note mới để lưu trữ thông tin ghi chú.
        final Note note=new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        // Kiểm tra xem layout layoutWebURL có hiển thị không.
        //Nếu hiển thị, lấy nội dung từ textWebURL (giả sử là một EditText) và đặt nó làm liên kết web cho ghi chú trong đối tượng note.
        if(layoutWebURL.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebURL.getText().toString());
        }

        //Kiểm tra xem alreadyAvailableNote đã tồn tại (được sử dụng để chỉnh sửa ghi chú) hay không.
        //Nếu tồn tại, lấy ID từ alreadyAvailableNote và đặt nó cho ghi chú trong đối tượng note.
        if (alreadyAvailableNote != null)
        {
            note.setId(alreadyAvailableNote.getId());
        }


        //Chú thích này chỉ định rằng việc sử dụng một trường tĩnh (staticFieldLeak) là hợp lệ và không nên tạo ra cảnh báo.
        @SuppressLint("StaticFieldLeak")
        //Định nghĩa một lớp con tên SaveNoteTask kế thừa từ lớp AsyncTask. Các tham số generics Void, Void, Void chỉ định kiểu dữ liệu không được sử dụng trong quá trình thực thi (doInBackground, onProgressUpdate, onPostExecute).
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{

            @Override
            //Phương thức này chạy trong luồng nền và thực hiện công việc lưu trữ ghi chú vào cơ sở dữ liệu.
            protected Void doInBackground(Void... voids) {
                //Sử dụng lớp NotesDatabase để truy cập vào đối tượng noteDao và gọi phương thức insertNote() để lưu trữ đối tượng note trong cơ sở dữ liệu.
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            //Phương thức này được gọi sau khi doInBackground hoàn thành công việc và được thực thi trong luồng giao diện người dùng.
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Tạo một đối tượng Intent.
                Intent intent = new Intent();
                //Đặt kết quả trả về cho hoạt động gọi phương thức saveNote() là RESULT_OK.
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        // Tạo một thể hiện của lớp SaveNoteTask và gọi phương thức execute() để bắt đầu thực thi nhiệm vụ lưu trữ ghi chú trong cơ sở dữ liệu.
        new SaveNoteTask().execute();
        selectedImagePath="null";

    }

    //khởi tạo các phần tử và xử lý sự kiện cho phần Miscellaneous của giao diện (bottom sheet)
    public void initMiscellaneous()
    {   //Bottom operation
        //Đối tượng LinearLayout chứa các phần tử trong bottom sheet.
        final LinearLayout layoutMicellaneous = findViewById(R.id.layoutMiscellaneous);
        //Đối tượng BottomSheetBehavior để quản lý trạng thái của bottom sheet.
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior=BottomSheetBehavior.from(layoutMicellaneous);

        //Xử lý sự kiện khi người dùng nhấp vào tiêu đề Miscellaneous.
        layoutMicellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nếu trạng thái của bottom sheet không phải là STATE_EXPANDED (đang được mở rộng), thì mở rộng bottom sheet và đặt isBottomSheetExpanded là true.
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    isBottomSheetExpanded=true;
                    //Ngược lại, thu nhỏ bottom sheet và đặt isBottomSheetExpanded là false.
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    isBottomSheetExpanded=false;
                }
            }

        });
        // end operation

         final ImageView imageColor1 = layoutMicellaneous.findViewById(R.id.imageColor1);
         final ImageView imageColor2 = layoutMicellaneous.findViewById(R.id.imageColor2);
         final ImageView imageColor3 = layoutMicellaneous.findViewById(R.id.imageColor3);
         final ImageView imageColor4 = layoutMicellaneous.findViewById(R.id.imageColor4);
         final ImageView imageColor5 = layoutMicellaneous.findViewById(R.id.imageColor5);


        //Xử lý sự kiện khi người dùng chọn màu sắc cho ghi chú.

        //Grey color = Defalut
         layoutMicellaneous.findViewById(R.id.imageColor1).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 selectedNoteColor="#333333";
                 imageColor1.setImageResource(R.drawable.ic_done);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(0);
                 //cập nhật màu sắc hiển thị của phụ đề ghi chú.
                 setsubtitleIndicatorColor();
             }
         });

        //Yellow Color
        layoutMicellaneous.findViewById(R.id.imageColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedNoteColor="#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setsubtitleIndicatorColor();
            }
        });

        //Red Color
        layoutMicellaneous.findViewById(R.id.imageColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedNoteColor="#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setsubtitleIndicatorColor();
            }
        });

        //Blue Color
        layoutMicellaneous.findViewById(R.id.imageColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedNoteColor="#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setsubtitleIndicatorColor();
            }
        });

        //Black Color
        layoutMicellaneous.findViewById(R.id.imageColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedNoteColor="#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setsubtitleIndicatorColor();
            }
        });


        //Xử lý màu sắc cho ghi chú đã tồn tại.
        //Nếu alreadyAvailableNote tồn tại và có màu sắc khác null và không rỗng, thực hiện switch case dựa trên màu sắc và thực hiện performClick() tương ứng với màu sắc đó.
        if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FDBE3B":
                    layoutMicellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMicellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutMicellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMicellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }




        // Xử lý sự kiện khi người dùng nhấn nút thêm hình ảnh
        layoutMicellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Khi người dùng nhấp vào, bottom sheet được thu nhỏ và isBottomSheetExpanded được đặt thành false.
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                isBottomSheetExpanded=false;

                //Kiểm tra quyền truy cập vào bộ nhớ.
                //Sử dụng ContextCompat.checkSelfPermission() để kiểm tra xem ứng dụng đã được cấp quyền truy cập vào bộ nhớ hay chưa.
                if(ContextCompat.
                        checkSelfPermission( getApplicationContext(),
                                              Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED  )
                {
                    //Nếu quyền truy cập vào bộ nhớ chưa được cấp, sử dụng ActivityCompat.requestPermissions() để yêu cầu quyền từ người dùng. Mã yêu cầu quyền được đặt là REQUEST_CODE_STORAGE_PERMISSION.
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String []{ Manifest.permission.READ_EXTERNAL_STORAGE },
                            REQUEST_CODE_STORAGE_PERMISSION  );
                } else
                {
                    //Nếu quyền truy cập vào bộ nhớ đã được cấp, gọi phương thức selectImage() để chọn hình ảnh từ bộ nhớ.
                    selectImage();
                }

            }
        });

        //  Xử lý sự kiện khi người dùng nhấn nút thêm đường dẫn web
        layoutMicellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khi người dùng nhấp vào, bottom sheet được thu nhỏ và gọi phương thức showAddURLDialog() để hiển thị hộp thoại thêm đường dẫn web.isBottomSheetExpanded được đặt thành false.
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
                isBottomSheetExpanded=false;
            }
        });

        // xử lí sự kiện xóa ghi chú
        //Nếu alreadyAvailableNote tồn tại, hiển thị nút xóa ghi chú và xử lý sự kiện khi người dùng nhấp vào nút xóa.
        if(alreadyAvailableNote != null)
        {
            layoutMicellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMicellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Khi người dùng nhấp vào, bottom sheet được thu nhỏ và gọi phương thức showDeleteNoteDialog() để hiển thị hộp thoại xác nhận xóa ghi chú.
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();

                }
            });
        }
    }


    //hiển thị hộp thoại xác nhận xóa ghi chú.
    private  void showDeleteNoteDialog(){
        //Kiểm tra xem dialogDelete có null hay không. Nếu là null, tạo một đối tượng AlertDialog.Builder và inflate layout layout_delete_note.
        if (dialogDelete == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );

            builder.setView(view);
            //Đặt view cho hộp thoại và tạo hộp thoại bằng cách gọi builder.create().
            dialogDelete=builder.create();
            //Thiết lập background cho cửa sổ hộp thoại bằng cách sử dụng getWindow().setBackgroundDrawable().
            if(dialogDelete.getWindow()!=null){
                dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            //Xử lý sự kiện khi người dùng nhấp vào nút xóa.
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Tạo một lớp DeleteNoteTask là một AsyncTask để xóa ghi chú.
                    @SuppressLint("StaticFieldLeak")
                    class  DeleteNoteTask extends AsyncTask<Void,Void,Void>{
                        @Override
                        //Trong phương thức doInBackground(), xóa ghi chú bằng cách gọi NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote).
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext())
                                    .noteDao()
                                    .deleteNote(alreadyAvailableNote);

                            return null;
                        }

                        @Override
                        //Trong phương thức onPostExecute(), gửi kết quả là ghi chú đã bị xóa về cho Activity chủ và kết thúc hoạt động hiện tại.
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            Intent intent=new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                     new DeleteNoteTask().execute();
                }

            });


            //Xử lý sự kiện khi người dùng nhấp vào nút hủy.
            view.findViewById(R.id.textCancelDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                //gọi dialogDelete.dismiss() để đóng hộp thoại.
                public void onClick(View view) {
                    dialogDelete.dismiss();
                }
            });
        }

        //hiển thị hộp thoại lên màn hình.
        dialogDelete.show();
    }

    //hàm này đặt màu cho subtitleIndicator, một View dùng để hiển thị màu sắc của ghi chú.
    private  void setsubtitleIndicatorColor()
    {
        //Lấy Background của subtitleIndicator và ép kiểu thành GradientDrawable. GradientDrawable là một lớp cho phép tạo các hình dạng và gradient.
        GradientDrawable gradientDrawable = (GradientDrawable) subtitleIndicator.getBackground();
        //Sử dụng setColor() trên gradientDrawable để đặt màu cho subtitleIndicator. Màu sẽ được định dạng bằng phương thức Color.parseColor(selectedNoteColor), trong đó selectedNoteColor là chuỗi màu đã chọn cho ghi chú.
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    //hàm này để chọn một hình ảnh từ thư viện hình ảnh của thiết bị.
    private  void selectImage()
    {      Log.d("MYLOG","Starting Select-Image ");

        //Tạo một Intent với hành động ACTION_PICK. Hành động này sẽ mở một giao diện người dùng cho phép người dùng chọn một hình ảnh từ thư viện hình ảnh của thiết bị.
          Intent intent= new Intent(Intent.ACTION_PICK);
          //Đặt kiểu của intent thành "image/*". Điều này chỉ định rằng chỉ các tệp hình ảnh sẽ được hiển thị cho người dùng để chọn.
         intent.setType("image/*");
         //Gọi startActivityForResult() với intent và REQUEST_CODE_SELECT_IMAGE. Khi người dùng chọn một hình ảnh từ thư viện, kết quả sẽ được trả về trong phương thức onActivityResult() với mã yêu cầu là REQUEST_CODE_SELECT_IMAGE.
         startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    //onRequestPermissionsResult() được gọi khi người dùng trả lời yêu cầu cấp quyền của ứng dụng. Phương thức này kiểm tra xem yêu cầu cấp quyền có mã REQUEST_CODE_STORAGE_PERMISSION và có được cấp quyền hay không.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Kiểm tra xem requestCode có phải là REQUEST_CODE_STORAGE_PERMISSION và grantResults có chứa ít nhất một kết quả (grantResults.length > 0).
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0 )
        {
            //Nếu người dùng đã cấp quyền (grantResults[0] == PackageManager.PERMISSION_GRANTED), gọi hàm selectImage() để chọn hình ảnh từ thư viện.
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            //Nếu người dùng từ chối cấp quyền, hiển thị một thông báo ngắn cho biết quyền đã bị từ chối.
            else{
                Toast.makeText(this, " Quyền bị từ chối !!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //onActivityResult() được gọi khi hoạt động con đã khởi chạy từ selectImage() hoàn thành và trả về kết quả.
    // Phương thức này kiểm tra mã yêu cầu requestCode và mã kết quả resultCode để xác định xem hoạt động đã thành công và dữ liệu trả về có khả dụng hay không.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d("MYLOG","Entered onActivity ===");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MYLOG","Inside On ActivityResult");
        //Kiểm tra xem requestCode có phải là REQUEST_CODE_SELECT_IMAGE và resultCode có phải là RESULT_OK để đảm bảo hoạt động đã thành công và trả về dữ liệu.
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            //Kiểm tra xem data có khác null không. Nếu không, hiển thị thông báo lỗi và kết thúc phương thức.
            if( data != null)
            {
                //Lấy đường dẫn của hình ảnh từ data.getData() và sử dụng thư viện Glide để hiển thị hình ảnh trong imageNote ImageView.
                Uri selectedImageUri = data.getData();
                Log.d("MYLOG","Selected Image Uri : "+selectedImageUri);
                if( selectedImageUri != null)
                {
                    Log.d("MYLOG","Glide Is Starting");
                    Glide.with(this).
                            load(selectedImageUri).error(R.drawable.ic_cross_add).
                             into(imageNote);

                    //Picasso.get().load(selectedImagePath).fit().into(imageNote);
                    Log.d("MYLOG","Glide Ending");

                    //Sau khi hiển thị hình ảnh thành công, phương thức đặt imageNote và nút xóa hình ảnh (findViewById(R.id.imageRemoveImage)) hiển thị lên trên giao diện người dùng.
                    imageNote.setVisibility(View.VISIBLE);

                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                    //phương thức lưu đường dẫn của hình ảnh đã chọn vào biến selectedImagePath để sử dụng trong các phần khác của mã
                    selectedImagePath = getPathFromUri(selectedImageUri);

                } else{
                    Log.d("MYLOG","Selected Image uri is null ==");
                    Toast.makeText(this, "Selected Image Uri Null", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {   Log.d("MYLOG","Data is null ==");
                Toast.makeText(this, "Wrong Request Code == ", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {   Log.d("MYLOG","Password Encountered");
            //Toast.makeText(this, "Image Too Large ! !", Toast.LENGTH_SHORT).show();
        }

    }

    //lấy đường dẫn tệp từ Uri của hình ảnh được chọn.
    private  String getPathFromUri(Uri contentUri)
    {
        //khai báo biến filePath để lưu trữ đường dẫn tệp.
        String filePath;
        //sử dụng một Cursor để truy vấn các cột của contentUri. contentUri đại diện cho Uri của hình ảnh được chọn.
        Cursor cursor = getContentResolver()
                         .query(contentUri,null,null,null,null);

        // kiểm tra xem cursor có null hay không. Nếu cursor là null, nghĩa là không thể truy vấn được dữ liệu từ contentUri. Trong trường hợp này, bạn lấy đường dẫn từ contentUri bằng cách sử dụng contentUri.getPath() và gán cho filePath. Đây là trường hợp đặc biệt khi không thể truy vấn thông tin từ hệ thống MediaStore.
        if (cursor == null)
        {
            filePath = contentUri.getPath();
            Log.d("MYLOG","Cursor NULL thi");
        }
        //trường hợp cursor không null, bạn di chuyển con trỏ của cursor đến hàng đầu tiên bằng cursor.moveToFirst().
        else{
            Log.d("MYLOG","Cursor Moving To First");
            cursor.moveToFirst();
            //sử dụng cursor.getColumnIndex("_data") để lấy chỉ mục của cột "_data", đại diện cho đường dẫn tệp.
            int index = cursor.getColumnIndex("_data");
            //sử dụng cursor.getString(index) để lấy đường dẫn tệp từ chỉ mục của cột "_data" và gán cho filePath.
            filePath = cursor.getString(index);
            //đóng cursor
            cursor.close();
        }
        return  filePath;
    }

    //hiển thị hộp thoại cho phép người dùng thêm một URL vào ghi chú
    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            // tạo mới hộp thoại
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            //tạo một đối tượng LayoutInflater và sử dụng phương thức inflate() để gắn kết tệp tin layout_add_url.xml với hộp thoại. Đối số thứ hai của inflate() là ViewGroup gốc của tệp tin layout_add_url.xml.
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            //đặt giao diện của hộp thoại là view đã được tạo.
            builder.setView(view);

            //tạo đối tượng AlertDialog
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                //đặt nền của cửa sổ là một ColorDrawable trong suốt (new ColorDrawable(0)) bằng cách sử dụng dialogAddURL.getWindow().setBackgroundDrawable().
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //kiểm tra xem trường nhập liệu URL có trống hay không
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNoteActivity.this, "Nhập URL", Toast.LENGTH_SHORT).show();
                    } else if (! Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, " URL không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        //Nếu URL hợp lệ, gán giá trị của trường nhập liệu URL cho textWebURL và hiển thị layout chứa URL (layoutWebURL).
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        //dong hop thoai
                        dialogAddURL.dismiss();
                    }

                }
            });

            //khi người dùng ấn nút cancel đóng hộp thoại
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });


        }

        dialogAddURL.show();
    }

    //xử lý sự kiện khi người dùng nhấn nút Back của bootomsheet.
    @Override
    public void onBackPressed() {
        // kiểm tra biến isBottomSheetExpanded để xem xem bottom sheet có được mở rộng hay không
        if(isBottomSheetExpanded)
        {
            final LinearLayout layoutMicellaneous = findViewById(R.id.layoutMiscellaneous);
            // Nếu isBottomSheetExpanded là true, tức là bottom sheet đang mở rộng,thu nhỏ bottom sheet bằng cách đặt trạng thái của bottomSheetBehavior thành BottomSheetBehavior.STATE_COLLAPSED, và đặt isBottomSheetExpanded thành false để đánh dấu rằng bottom sheet đã được thu nhỏ.
            final BottomSheetBehavior<LinearLayout> bottomSheetBehavior=BottomSheetBehavior.from(layoutMicellaneous);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            isBottomSheetExpanded=false;
        }else {
            super.onBackPressed();
        }
    }
}
