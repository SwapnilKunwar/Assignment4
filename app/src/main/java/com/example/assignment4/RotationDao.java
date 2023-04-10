package com.example.assignment4;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RotationDao {

    @Insert
    void addTx(Rotation rot_obj);

}
