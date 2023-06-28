package com.example.note_app.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    //Đây là trường ID đại diện cho ID của ghi chú.
    // Chúng ta sử dụng annotation @PrimaryKey(autoGenerate = true) để đánh dấu trường này là khóa chính và tự động tạo giá trị ID duy nhất cho mỗi ghi chú được chèn vào cơ sở dữ liệu.

    @ColumnInfo(name = "title")
    private String title;
    //Đây là trường tiêu đề của ghi chú

    @ColumnInfo(name = "date_time")
    private String dateTime;
    // Đây là trường thời gian và ngày của ghi chú.

    @ColumnInfo(name = "subtitle")
    private String subtitle;
    //Đây là trường phụ đề của ghi chú.

    @ColumnInfo(name = "note_text")
    private String noteText;
    //Đây là trường văn bản chính của ghi chú.

    @ColumnInfo(name = "image_path")
    private String imagePath;
    //Đây là đường dẫn đến hình ảnh được đính kèm trong ghi chú.

    @ColumnInfo(name = "color")
    private String color;
    //Đây là trường màu sắc của ghi chú

    @ColumnInfo(name = "web_link")
    private String webLink;
    //Đây là trường liên kết web được liên kết với ghi chú.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " : " + dateTime;
    }
}


//****Lớp Note là một lớp entity đại diện cho một ghi chú trong ứng dụng. Đây là nơi định nghĩa các trường
// dữ liệu cho ghi chú và ánh xạ chúng với cột tương ứng trong bảng "notes" của cơ sở dữ liệu.****


//Lớp Note cũng được chú thích bằng @Entity(tableName = "notes")
// để xác định rằng nó là một entity và tên của bảng tương ứng trong cơ sở dữ liệu là "notes".

//Lớp Note cũng triển khai ghi đè phương thức toString() để trả về một chuỗi biểu diễn của ghi chú,
// bao gồm tiêu đề và thời gian.

//Lớp Note được triển khai từ giao diện Serializable, cho phép đối tượng Note
// có thể được chuyển đổi thành dạng byte để có thể gửi đi hoặc lưu trữ.


