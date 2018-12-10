package com.example.kristoffer.stockmonitorremake;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM book")
    List<Book> getAllBooks();

    @Delete
    void delete(Book b);
    @Insert
    void insert(Book... books);
    @Update
    void update(Book b);
    @Query("SELECT * FROM book WHERE symbol LIKE :symbol")
    Book findBookBySymbol(String symbol);
}
