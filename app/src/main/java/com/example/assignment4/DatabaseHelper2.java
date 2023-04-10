package com.example.assignment4;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.hardware.SensorEventListener;

@Database(entities = Light.class, exportSchema = false,version = 2)
public abstract class DatabaseHelper2 extends RoomDatabase {
    private static final String DB_NAME2 = "Lightdb";
    private static DatabaseHelper2 instance2;

    public static synchronized DatabaseHelper2 getDB2(Context context){
        if(instance2==null){

            instance2= Room.databaseBuilder
                            (context,DatabaseHelper2.class,DB_NAME2)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance2;
    }


    public abstract LightDao lightDao();
}
