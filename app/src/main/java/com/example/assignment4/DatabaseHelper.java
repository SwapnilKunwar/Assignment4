package com.example.assignment4;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.hardware.SensorEventListener;

@Database(entities = Proximity.class, exportSchema = false,version = 1)
public abstract class DatabaseHelper extends RoomDatabase {
    private static final String DB_NAME = "Proximitydb";
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getDB(Context context){
        if(instance==null){

            instance= Room.databaseBuilder
                    (context,DatabaseHelper.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }


    public abstract ProximityDao proximityDao();
}
