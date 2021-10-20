# 1. Lưu Trữ Dữ Liệu Local Với SQLiteDatabase Sử Dụng Room API <a id="1"></a>
________________________________________________________________________________________________________________________
- Room là một bộ các thư viện, bao bọc lấy cơ sở dữ liệu SQLite.
- Nó có khả năng truy cập vào cơ sở dữ liệu này một cách dễ dàng mà vẫn không ảnh hưởng đến sức mạnh vốn có của SQLite.
- Có 2 nguyên nhân nên sử dụng Room để quản lý SQLiteDatabase hơn là sử dụng extends SQLiteOpenHelper thuần túy:
  - hạn chế việc viết code dài, lặp đi lặp lại, tốn thời gian và dễ xảy ra lỗi
  - dễ dàng quản lý các query SQL cho các database có quan hệ phức tạp
- 1 số phương thức mà Room đã định nghĩa sẵn

Annotations |Purpose
------------|------------------------------------------------------------------------------------------------------------
@Entity     |create a SQLite table in the database using a data model class
@Dao        |create a Data Access Object in the database using an interface class
@Database   |a class with this annotation will create an abstraction for the Data Access Object
@PrimaryKey |a variable with this annotation will set a primary key for the table
@Insert     |inserts parameters into the table
@Update     |updates parameters of existing table
@Delete     |deletes parameters of existing table
@Query      |running SQL query method within the table
@Ignore     |ignores the parameter form the Room database

- các ứng dụng sử dụng 1 lượng lớn data có cấu trúc có thể hưởng lợi lớn từ việc lưu lại data trên local thông qua Room database
- trường hợp thường gặp nhất là chỉ cache những data liên quan, nếu làm vậy thì khi thiết bị không có kết nối internet thì user
vẫn có thể truy cập data đấy khi đang offline
  - mọi dữ liệu phát sinh hay thay đổi do user sau đó sẽ được đồng bộ với server khi họ online trở lại

## 1.1. Đặc Điểm Của Room Database <a id="1.1"></a>
________________________________________________________________________________________________________________________
- Framework chính (SQLiteDatabase) cung cấp các built-in support cho các trường hợp làm việc với các nội dung SQL thô
- mặc dù API này khá mạnh nhưng nó lại tương đối low-level, yêu cầu khá nhiều thời gian và công sức để sử dụng
  - không có xác thực các câu truy vấn SQL ở thời điểm compile-time (không kiểm lỗi tại thời điểm viết code)
  - khi data schema thay đổi thì developer sẽ phải cập nhật lại các query SQL thủ công (mất thời gian, và dễ xảy ra lỗi)
  - sẽ phải dùng nhiều code soạn sẵn để chuyển đổi giữa truy vấn SQL với các Java data object
> Room sẽ giải quyết các vấn đề trên thay cho developer

- để sử dụng Room, trước tiên ta phải nhúng API này vào trong Project
- trong file build.gradle cấp module ta thêm vào thư viện mới nhất từ [https://developer.android.com/training/data-storage/room]("https://developer.android.com/training/data-storage/room)
```groovy
dependencies {
  def room_version = "2.3.0"

  implementation "androidx.room:room-runtime:$room_version"
  annotationProcessor "androidx.room:room-compiler:$room_version"

  // optional - RxJava2 support for Room
  implementation "androidx.room:room-rxjava2:$room_version"

  // optional - RxJava3 support for Room
  implementation "androidx.room:room-rxjava3:$room_version"

  // optional - Guava support for Room, including Optional and ListenableFuture
  implementation "androidx.room:room-guava:$room_version"

  // optional - Test helpers
  testImplementation "androidx.room:room-testing:$room_version"

  // optional - Paging 3 Integration
  implementation "androidx.room:room-paging:2.4.0-alpha05"
}
```

## 1.2. Các Thành Phần Chính Trong Room <a id="1.2"></a>
________________________________________________________________________________________________________________________

<img src="https://developer.android.com/images/training/data-storage/room_architecture.png">

### 1.2.1. Entity <a id="1.2.1"></a>
________________________________________________________________________________________________________________________
- nếu SQLite là 1 dạng database được xây dựng dựa trên các Table
- thì với Room được xây dựng dựa trên các Entity (về mặt sử dụng thì Entity là 1 Class)
  - khi compile Room sẽ dựa vào các Entity để tạo ra các Table
  - các thuộc tính trong Entity chính là các Column của Table
- mỗi Entity đại diện cho 1 Class dùng để tạo ra các Object (mỗi Object là 1 Row của Table)
- mỗi Entity thì 1 Table database sẽ được tạo ra để giữ các item tương ứng (Object = Row)
- mỗi 1 Field của Entity sẽ được persist (duy trì) trong database trừ trường hợp bị chú thích là ``@Ignore``
- các Entity có thể
  - có Constructor empty (trường hợp clas DAO có thể truy cập từng Field đã persist)
  - có Constructor với các đối số đầy đủ hoặc 1 phần

#### Ví dụ lưu lại toàn bộ các user và add vào database, đối tượng user gồm các thuộc tính name, password, và place
- User.java
```java
package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import io.reactivex.rxjava3.annotations.NonNull;

@Entity(tableName = "users")
public class User {
    private static final String DEFAULT_PASSWORD = "1234567890";

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "first_name")
    private String mFirstName;

    @ColumnInfo(name = "last_name")
    private String mLastName;

    @ColumnInfo(name = "password")
    private String mPassword;

    @Embedded
    private Place mPlace;

    public User() {

    }

    @Ignore
    public User(String mFirstName, String mLastName) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = DEFAULT_PASSWORD;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        if (mPlace != null) {
            return mFirstName + " " + mLastName + "\n" + mPlace.getmName();
        }

        return mFirstName + " " + mLastName;
    }
}
```
- Place.java
```java
package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "place")
public class Place {
    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "lat")
    private double mLat;

    @ColumnInfo(name = "lng")
    private double mLng;

    @ColumnInfo(name = "name")
    private String mName;

    public Place() {

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLng() {
        return mLng;
    }

    public void setmLng(double mLng) {
        this.mLng = mLng;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
```
- với 1 Class được định nghĩa với annotation ``@Entity(tableName = "tên_Table")`` thì Room sẽ:
  - tạo 1 Table với name được chỉ định trong chú thích ``(tableName = "tên_Table")``
  - Row trong Table là 1 Object của Class
  - Column
    - mỗi Column trong Table là 1 Field của Class
      - có thể chỉ định tên của Column trong Table với annotation ``@ColumnInfo(name = "tên_Column")`` trước mỗi Field của Class
      - nếu không muốn Field nào đó (làm Column) lưu trữ trong Table thì khai báo annotation ``@Ignore`` trước Field đó
  - Primary Key
    - phải có ít nhất 1 Column (Field) làm Primary Key bằng cách 
      - khai báo annotation ``@PrimaryKey(autoGenerate = true)`` trước Field cần làm Primary Key
      - trường hợp Field có kiểu ``int``, ``long`` có thể thiết lập Room gán ID tự động cho các Primary Key của Entity ta thêm thuộc tính ``(autoGenerate = true)`` 
  - Indices and Uniqueness (Chỉ số & tính duy nhất)
    - Indices
      - nếu muốn đánh dấu index cho 1 số Field trong database để tăng tốc độ query <br/>``@Entity(tableName = "users", indices = {@Index(value = {"first_name", "last_name"})})``
    - Uniqueness
      - nếu muốn 1 số Field nào đó có tính duy nhất (trong database sẽ không có bản ghi nào trùng lặp), ví dụ "first_name" và "last_name" là duy nhất <br/>``@Entity(tableName = "users", indices = {@Index(value = {"first_name", "last_name"}, unique = true)})``
  - Nested objects (Object Column)
    - trong trường hợp có 1 số Field có kiểu Object, nếu muốn Field này đơn giản chỉ là 1 Column bình thường trong Table của database chứ không phải là 1 Table riêng biệt
    - ta sử dụng annotation ``@Embedded`` trước Field cần chỉ định là 1 Column
```java
    @Embedded
    private Place mPlace;
```
  - Relationships Between Objects (mối quan hệ giữa các Object)
    - nếu ta muốn thiết lập mối quan hệ giữa các Entity (parent Entity và child Entity) thì sử dụng annotation ``@ForeignKey``
    - ví dụ ta có đối tượng Pet là child của User
```java
package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"))
public class Pet {
    @PrimaryKey
    public int petId;

    public String name;

    @ColumnInfo(name = "user_id")
    public int userId;
}
```

### 1.2.2. DAO - Data Access Object <a id="1.2.2"></a>
________________________________________________________________________________________________________________________
- DAO phải được khai báo là ``interface``
- DAO là thành phần chịu trách nhiệm làm việc với các Entity trong database
- khi 1 ``interface`` được đánh dấu với annotation ``@Dao`` giúp Room hiểu rằng đây là 1 DAO và sẽ hành xử với các phương thức
đi kèm với các annotation sau:
  - ``@Insert`` đi kèm với phương thức insert
  - ``@Update`` đi kèm với phương thức update
  - ``@Delete`` đi kèm với phương thức delete
  - ``@Query`` giúp ta định nghĩa các câu query SQL theo nhu cầu
- vì thành phần DAO là 1 ``interface`` nên các phương thức của nó luôn là ``public abstract`` và không cần phải khai báo thân hàm
- ở Class được gắn annotation ``@Database`` khai báo 1 phương thức trừu tượng ``public abstract``:
  - không có đối số
  - kiểu trả về là Class được gắn annotation ``@Dao``
- khi code được sinh ra ở compile-time: Room sẽ tạo 1 implementation của Class được gắn annotation ``@Dao``
> bằng cách truy cập database thông qua DAO thay vì query builder hay query trực tiếp, thì ta có thể cô lập các thành
> phần khác nhau của database
- UserDao.java
```java
package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    Flowable<User> getUserByUserId(int userId);

    @Query("SELECT * FROM users WHERE first_name LIKE :userName OR last_name LIKE :userName ")
    Flowable<List<User>> getUserByName(String userName);

    @Query("SELECT * FROM users")
    Flowable<List<User>> getAllUser();

    @Insert
    void insertUser(User... users);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUser();

    @Update
    void updateUser(User... users);
}
```

### 1.2.3. Abstract class Database extends RoomDatabase <a id="1.2.3"></a>
________________________________________________________________________________________________________________________
- Entity (dùng để tạo Table)
- DAO (xây dựng các câu truy vấn trên Table)
- để gom các Table và các câu query vào chung 1 nơi để xử lý, ta phải khai báo 1 abstract class extends RoomDatabase
- RoomDatabase giúp tạo database holder:
- abstract class extends RoomDatabase phải
  - được khai báo với annotation ``@Database``
  - đi kèm với annotation ``@Database`` là chỉ định các thực thể (các Class Entity) và version của database
  - phải là abstract class và extends RoomDatabase
  - để lấy Instance của abstract class này tại thời điểm runtime ta sử dụng:
    - ``Room.databaseBuilder()``
    - hoặc ``Room.inMemoryDatabaseBuilder()``
  - xây dựng 1 phương thức abstract để lấy về các DAO đã khai báo
  - nên được xây dựng theo Singleton Design Pattern (vì chỉ cần 1 RoomDatabase trong suốt quá trình sống của ứng dụng)
- UserDatabase.java
```java
package com.hienqp.roomdatabasewithdeveloperandroiddotcom;

import static com.hienqp.roomdatabasewithdeveloperandroiddotcom.UserDatabase.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = DATABASE_VERSION)
public abstract class UserDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "Room-database";
    public static final int DATABASE_VERSION = 1;

    private static UserDatabase sUserDatabase;

    public abstract UserDao userDao();

    public static UserDatabase getInstance(Context context) {
        if (sUserDatabase == null) {
            sUserDatabase = Room.databaseBuilder(context, UserDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sUserDatabase;
    }
}
```