package com.example.kristoffer.stockmonitorremake;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDb extends RoomDatabase {

    private static BookDb db;

    public abstract BookDao bookDao();

    static BookDb getBookDb(Context context){
        if(db == null){
            db = Room.databaseBuilder(context.getApplicationContext(),
                    BookDb.class, "book_db").allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build();
        }
        return db;
    }
}
