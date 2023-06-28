package com.example.note_app.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.note_app.dao.NoteDao;
import com.example.note_app.entities.Note;

 @Database(entities = Note.class, version = 1, exportSchema = false)
 public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase notesDatabase;

    public static  synchronized  NotesDatabase getDatabase(Context context)
    {
        if(notesDatabase==null)
        {
            notesDatabase= Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes_db"
            ).build();
        }
        return notesDatabase;
    }

    public abstract NoteDao noteDao();

}



//****Lớp NotesDatabase là lớp trừu tượng đại diện cho cơ sở dữ liệu của ứng dụng ghi chú.
// Đây là nơi xác định cấu trúc và các toàn vẹn của cơ sở dữ liệu sử dụng thư viện Room.****


//@Database(entities = Note.class, version = 1, exportSchema = false): Đây là chú thích để đánh
// dấu lớp NotesDatabase là một cơ sở dữ liệu Room. Trong chú thích này, chúng ta chỉ định danh sách
// các entity mà cơ sở dữ liệu sẽ bao gồm (trong trường hợp này là lớp Note), phiên bản cơ sở dữ liệu (bắt đầu từ 1),
// và exportSchema = false để không tạo file schema.

//notesDatabase: Đây là biến tĩnh của lớp NotesDatabase để theo dõi một phiên bản duy nhất của cơ sở dữ liệu trong ứng dụng.

//getDatabase(Context context): Đây là phương thức tĩnh để truy cập và khởi tạo cơ sở dữ liệu Room.
// Phương thức này sử dụng mô hình singleton để đảm bảo chỉ có một phiên bản của cơ sở dữ liệu được tạo ra.

//Room.databaseBuilder(...): Phương thức này tạo một phiên bản của lớp NotesDatabase bằng cách sử dụng Room.databaseBuilder.
// Phương thức này yêu cầu một Context, lớp NotesDatabase và tên của cơ sở dữ liệu. Ta có thể cấu hình thêm các tùy chọn
// khác như xử lý versioning hoặc migrations.

//noteDao(): Phương thức trừu tượng này trả về một đối tượng NoteDao. Bạn sẽ cần triển khai phương thức này trong
// lớp NotesDatabase để cung cấp một giao diện để truy cập và thao tác dữ liệu trong cơ sở dữ liệu.


