package cn.com.nwdc.unify.db.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author coffee
 * @Classname User
 * @Description TODO
 * @Date 2020/7/17 12:38
 */

@TableName("t_test_super_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuperUser {


    @TableId
    private String id;
    @TableField("name")

    private String name;
    @TableField("age")
    private int age;
    @TableField("bookList")
    private List<Book> bookList;

}
