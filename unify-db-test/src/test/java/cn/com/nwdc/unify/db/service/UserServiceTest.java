package cn.com.nwdc.unify.db.service;

import cn.com.nwdc.unify.db.IDbOperator;
import cn.com.nwdc.unify.db.condition.DbCondition;
import cn.com.nwdc.unify.db.entity.Book;
import cn.com.nwdc.unify.db.entity.User;
import cn.com.nwdc.unify.db.index.common.IndexDbCondition;
import cn.com.nwdc.unify.db.mapper.UserMapper;
import cn.com.nwdc.unify.db.pageinfo.PageInfo;
import cn.com.nwdc.unify.db.vo.UserVo;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author coffee
 * @Classname UserServiceTest
 * @Description TODO
 * @Date 2020/7/17 12:48
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    @Qualifier("esDbOperator")
    private IDbOperator dbOperator;

    @Test
    public void testAdd(){

        User user = User.builder().id("1001").name("likf").age(11).build();
        dbOperator.save(user);
    }

    @Test
    public void testExistTable(){



        System.out.println("存在:"+dbOperator.isExistTable("zhongbaoindex"));
    }


    @Test
    public void testBatchAdd(){


        List<User> userList = Lists.newLinkedList();
        for (int i = 0; i < 100; i++) {


            userList.add(User.builder().id("1001"+i).name("likf"+i).age(i).build());
        }

        dbOperator.batchSave(userList);
    }

    @Test
    public void testRawQuery(){

        DbCondition esDbCondition = IndexDbCondition.builder().build().template(
                "User","queryUserList"
        ).tableName("t_test_likf_20200717").addParam("name","likf");


        System.out.println(dbOperator.rawExecute(esDbCondition));
    }

    @Test
    public void testQueryPageInfo(){

        DbCondition esDbCondition = IndexDbCondition.builder().build().template(
                "User","queryUserList"
        ).elemClass(User.class).addParam("name","likf");




        PageInfo<User> pageInfo = new PageInfo();
        System.out.println(dbOperator.queryForPage(esDbCondition,pageInfo));
    }



    @Autowired
    private UserMapper userMapper;

    @Test
    public void testQueryPageInfo1(){

        PageInfo<User> pageInfo = userMapper.queryUserPageInfo("wang1");
        System.out.println(pageInfo);
    }

    @Test
    public void testQueryList(){

        List<User> userList = userMapper.queryUserList("likf");
        System.out.println(userList);
    }


    @Test
    public void testQueryList1(){


        List<Map<String,Object>> userList = userMapper.queryUserList1("t_test_zj","likf");
        System.out.println(userList);
    }

    @Test
    public void testQueryUserVoList(){


        List<UserVo> userList = userMapper.queryUserVoList("t_test_ty","likf");
        System.out.println("-------"+userList);
    }


    @Test
    public void testQueryUserVoListByStretegyBean(){
        List<UserVo> userList = userMapper.queryUserVoListByStretegyBean(User.builder().age(1).build(),"likf");
        System.out.println(userList);
    }

    @Test
    public void testBatchAdd1(){


        List<User> userList = Lists.newLinkedList();
        for (int i = 0; i < 100; i++) {


            User user = User.builder().id("1001"+i).name("likf").age(i).build();

            List<Book> books = Lists.newLinkedList();
            for (int j = 0; j < new Random().nextInt(3) + 3; j++) {
                books.add(Book.builder().bookName("重构"+i).bookNo(j).build());
            }
            user.setBookList(books);
            userList.add(user);
        }

        userMapper.batchSave(userList);
    }


    @Test
    public void testAdd1(){


        userMapper.save(User.builder().id("1001").name("wang1").age(2).build());
    };

 /*   @Test
    public void testExecuteRestRequest(){


        Object object = userMapper.executeRestRequest(
                HttpMethod.POST,
                "",


        );
    }
    */



    @Test
    public void testAllIndex(){
        List<Map<String,Object>> indexs = userMapper.queryAllIndex("");

        for(Map<String,Object> index:indexs){
            System.out.println("["+index.get("index")+"]["+index.get("docs.count")+"]");
        }



    }

}

