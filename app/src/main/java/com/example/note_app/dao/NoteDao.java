package com.example.note_app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.note_app.entities.Note;

import java.util.List;


@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();
    //Phương thức này truy vấn bảng "notes" và trả về một danh sách ghi chú
    // Các ghi chú được sắp xếp theo ID của chúng theo thứ tự giảm dần.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    //Phương thức này chèn một ghi chú mới vào bảng "notes".
    // Nếu một ghi chú có cùng ID đã tồn tại, nó sẽ được thay thế bằng ghi chú mới do chiến lược giải quyết xung đột OnConflictStrategy.REPLACE.

    @Delete
    void deleteNote(Note note);

    //Phương thức này xóa một ghi chú khỏi bảng "notes". Ghi chú cần xóa được chỉ định thông qua tham số.

}

// ****Giao diện DAO (Data Access Object) xác định các phương thức để thực hiện các hoạt động
// CRUD (Tạo, Đọc, Cập nhật, Xóa) trên bảng "notes" trong cơ sở dữ liệu của ứng dụng.****


//Các chú thích @Dao, @Query, @Insert, và @Delete là các phần của thư viện Room và
// cung cấp các chỉ thị để tạo mã cơ sở dữ liệu cần thiết trong quá trình biên dịch.


