package com.example.note_app.listeners;

import com.example.note_app.entities.Note;

public interface NotesListener {

    void onNoteClicked(Note note, int position);
}



//****Giao diện NotesListener định nghĩa một phương thức onNoteClicked() để lắng nghe sự kiện
// khi một ghi chú được nhấp vào trong ứng dụng ghi chú.****

//onNoteClicked(Note note, int position): Phương thức này được gọi khi một ghi chú được nhấp vào. Nó nhận vào hai tham số:

//note: Đối tượng ghi chú (Note) mà người dùng đã nhấp vào.

//position: Vị trí của ghi chú trong danh sách, dùng để
// xác định vị trí chính xác của ghi chú trong giao diện người dùng.

