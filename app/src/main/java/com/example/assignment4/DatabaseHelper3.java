package com.example.assignment4;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.hardware.SensorEventListener;

@Database(entities = Rotation.class, exportSchema = false,version = 3)
public abstract class DatabaseHelper3 extends RoomDatabase {
    private static final String DB_NAME3 = "Rotationdb";
    private static DatabaseHelper3 instance3;

    public static synchronized DatabaseHelper3 getDB3(Context context){
        if(instance3==null){

            instance3= Room.databaseBuilder
                            (context,DatabaseHelper3.class,DB_NAME3)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance3;
    }


    public abstract RotationDao rotationDao();
}
