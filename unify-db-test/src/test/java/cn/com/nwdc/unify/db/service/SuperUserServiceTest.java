package cn.com.nwdc.unify.db.service;


import cn.com.nwdc.unify.db.entity.AggVo;
import cn.com.nwdc.unify.db.entity.Book;
import cn.com.nwdc.unify.db.entity.SuperUser;
import cn.com.nwdc.unify.db.mapper.SuperUserMapper;
import cn.com.nwdc.unify.db.vo.UserVo;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperUserServiceTest {



    @Autowired
    private SuperUserMapper superUserMapper;

    @Test
    public void testQueryUserListByGroup(){


        List<Map<String,Object>> userList = superUserMapper.queryUserListByGroup("t_test_zj","groupAge","likf");
        System.out.println(userList);
    }

    @Test
    public void testQueryUserListByGroup1(){


        List<AggVo> userList = superUserMapper.queryUserListByGroup1("t_test_zj","groupAge","likf");
        System.out.println(userList);
    }


    @Test
    public void testFindElemById(){

        SuperUser superUser = superUserMapper.findElemById("100157", SuperUser.class);
        System.out.println(superUser);
    }

    public static final String ID_FLAG = "weide";

    @Test
    public void testBatchUpdate(){

        List list = Lists.newLinkedList();

        for (int i = 0; i < 2; i++) {
            SuperUser superUser = superUserMapper.findElemById(ID_FLAG+i, SuperUser.class);
            superUser.setName("张三丰" +i);
            list.add(superUser);

        }
        superUserMapper.update(list);

    }
    @Test
    public void testQueryUserList(){

        List<UserVo> userList = superUserMapper.queryUserVoListByStretegyBean(SuperUser.builder().age(1).build(),"likf");
        System.out.println(userList);
    }
    @Test
    public void testBatchAdd(){

        List<SuperUser> userList = Lists.newLinkedList();
        for (int i = 0; i < 2; i++) {


            SuperUser user = SuperUser.builder().id("weide"+i).name("likf").age(new Random().nextInt(9)).build();

            List<Book> books = Lists.newLinkedList();
            for (int j = 0; j < new Random().nextInt(3) + 3; j++) {
                books.add(Book.builder().bookName("重构"+i).bookNo(j).build());
            }
            user.setBookList(books);
            userList.add(user);
        }

        superUserMapper.batchSave(userList);
    }
}
