package com.example.assignment4;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "rotation")
public class Rotation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="value")
    private float value;

    Rotation(int id , float value){
        this.id=id;
        this.value=value;
    }
    @Ignore
    Rotation(float value){
        this.value=value;
    }

    public int getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
