package cn.com.nwdc.unify.db.vo;

import cn.com.nwdc.unify.db.entity.Book;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author coffee
 * @Classname UserVo
 * @Description TODO
 * @Date 2020/7/20 10:32
 */
@Data
@ToString
public class UserVo {




    private String id;

    private String name;

    private int age;

    private List<Book> bookList;




}
