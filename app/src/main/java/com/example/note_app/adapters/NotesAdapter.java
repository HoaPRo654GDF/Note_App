package com.example.note_app.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.note_app.R;
import com.example.note_app.entities.Note;
import com.example.note_app.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;

    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));

        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime,textDisplayURL;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        // Để lưu trữ View và tránh việc tìm kiếm nhiều lần sử dụng findViewById.
        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDate);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
            textDisplayURL=itemView.findViewById(R.id.textDisplayURL);

        }


        //Phương thức để thiết lập dữ liệu cho mỗi hàng dựa trên dữ liệu được cung cấp
        void setNote(Note note) {
            textTitle.setText(note.getTitle());

            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }

            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));

                if (note.getColor().equals("#FDBE3B")) {
                    textTitle.setTextColor(Color.BLACK);
                    textSubtitle.setTextColor(Color.BLACK);
                    textDateTime.setTextColor(Color.BLACK);

                    textDisplayURL.setLinkTextColor(Color.BLUE);
                } // Để đặt nền màu vàng cho một View.
                else if (note.getColor().equals("#FF4842")) {
                    textTitle.setTextColor(Color.WHITE);
                    textSubtitle.setTextColor(Color.WHITE);
                    textDateTime.setTextColor(Color.WHITE);
                    textDisplayURL.setTextColor(Color.BLACK);
                    textDisplayURL.setLinkTextColor(Color.YELLOW);

                } // Để đặt nền màu đỏ cho một View.
                else if (note.getColor().equals("#000000")) {
                    textTitle.setTextColor(Color.rgb(211, 211, 211));
                    textSubtitle.setTextColor(Color.rgb(211, 211, 211));
                    textDateTime.setTextColor(Color.rgb(211, 211, 211));
                    textDisplayURL.setLinkTextColor(Color.rgb(1,255,199));
                } // Để đặt nền màu đen cho một View.
                else {
                    textTitle.setTextColor(Color.WHITE);
                    textSubtitle.setTextColor(Color.WHITE);
                    textDateTime.setTextColor(Color.WHITE);
                    textDisplayURL.setLinkTextColor(Color.YELLOW);
                    textDisplayURL.setLinkTextColor(Color.rgb(1,255,199));
                } // Để đặt nền màu xám cho một View.

            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            // Để đặt một hình ảnh cho một View.
            if (!note.getImagePath().equals("null")) {
                //imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                Glide.with(itemView).
                        load(note.getImagePath()).
                        into(imageNote);
                Log.d("MYLOG", "Loaded..");

                imageNote.setVisibility(View.VISIBLE);

            } else {
                imageNote.setVisibility(View.GONE);
            }

            if (note.getWebLink()!=null)
            {
                textDisplayURL.setText(note.getWebLink());
                textDisplayURL.setVisibility(View.VISIBLE);
            }
            else{
                textDisplayURL.setVisibility(View.GONE);
            }

        }

    }


    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource)
                    {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


}




//****Lớp NotesAdapter là một Adapter dùng để hiển thị danh sách các ghi chú trong một RecyclerView trong ứng dụng ghi chú ****.

//(36)Constructor: NotesAdapter(List<Note> notes, NotesListener notesListener):Constructor này nhận vào một danh sách
// các ghi chú (notes) và một đối tượng NotesListener để lắng nghe sự kiện khi ghi chú được nhấp vào.

//(44)onCreateViewHolder(): Phương thức này tạo một ViewHolder bằng cách sử dụng layout item_container_note để định nghĩa
// giao diện của mỗi item trong RecyclerView.

//(55)onBindViewHolder(): Phương thức này gắn dữ liệu của một ghi chú cụ thể vào ViewHolder tại vị trí tương ứng trong
// danh sách ghi chú. Nó cũng thiết lập một bộ lắng nghe sự kiện cho item ghi chú, để khi item được nhấp vào,
// sự kiện onNoteClicked() được gọi trong NotesListener.

//(68)getItemCount(): Phương thức này trả về số lượng ghi chú trong danh sách.

//(73)getItemViewType(): Phương thức này trả về loại view của item tại một vị trí cụ thể trong danh sách.
// Trong trường hợp này, nó trả về vị trí chính nó.

//(77)Lớp con NoteViewHolder: Lớp này kế thừa lớp RecyclerView.ViewHolder và đại diện cho ViewHolder của mỗi item ghi chú.
// Nó chứa các thành phần giao diện như TextView, LinearLayout, RoundedImageView, và textDisplayURL để hiển thị dữ liệu
// của ghi chú.

//(97)Phương thức setNote(): Phương thức này được sử dụng để thiết lập dữ liệu cho một ViewHolder với các giá trị từ một
// ghi chú cụ thể. Nó đặt các giá trị cho các thành phần giao diện như tiêu đề, phụ đề, ngày giờ, màu nền và hình ảnh.

//(173)Phương thức searchNotes(): Phương thức này được sử dụng để tìm kiếm các ghi chú dựa trên một từ khóa tìm kiếm.
// Nó sử dụng một Timer để chờ một khoảng thời gian nhất định trước khi thực hiện tìm kiếm. Khi kết quả tìm kiếm có sẵn,
// danh sách ghi chú sẽ được cập nhật và gọi phương thức notifyDataSetChanged() để cập nhật giao diện.

//(204)Phương thức cancelTimer(): Phương thức này được sử dụng để hủy Timer nếu cần thiết, để ngăn chặn việc tìm kiếm được
// thực hiện khi người dùng nhập tiếp các ký tự tìm kiếm.