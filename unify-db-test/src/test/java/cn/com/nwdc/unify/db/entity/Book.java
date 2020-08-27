package cn.com.nwdc.unify.db.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder

public class Book {

    private String bookName;

    private int bookNo;

}
