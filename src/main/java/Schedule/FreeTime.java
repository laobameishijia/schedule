package Schedule;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class FreeTime {

    public int number;//标识序号

    //现在要解决这个变量的作用域的问题
    //下面的这个队列确实不同实例之间不能共用
    //但是队列里面包含的学生对象必须得是共用的，好像java本身是解决了这个问题的。
    public ArrayList<Student> seniorworkers = new ArrayList<Student>();//存储空闲时间里面的高年级学生
    public ArrayList<Student> juniorworkers = new ArrayList<Student>();//存储低年级
    public ArrayList<Student> allworkers = new ArrayList<Student>();//存储这个时间段内的所有同学
    public ArrayList<Student> realworkers = new ArrayList<Student> ();//存储在这个时间段内工作的同学

    public ArrayList<Student> senior3 = new ArrayList<Student> ();//存储不同年级的优先级的学生
    public ArrayList<Student> senior2 = new ArrayList<Student> ();
    public ArrayList<Student> senior1 = new ArrayList<Student> ();

    public ArrayList<Student> junior3 = new ArrayList<Student> ();
    public ArrayList<Student> junior2 = new ArrayList<Student> ();
    public ArrayList<Student> junior1 = new ArrayList<Student> ();

    //根据优先级把学生分类
    public void classifybypriority(){
        for (Student e : seniorworkers) {
            if (e.priority ==3 ) senior3.add(e);
            else if(e.priority == 2) senior2.add(e);
            else senior1.add(e);
        }
        for (Student e : juniorworkers) {
            if (e.priority ==3 ) junior3.add(e);
            else if(e.priority == 2) junior2.add(e);
            else junior1.add(e);
        }
    }

    //先根据被挑选可能性大小进行排序，然后从最高的三个人里面随机挑一个出来。
    public Student pickonejunior(){
        Collections.sort(juniorworkers, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                Student stu1=(Student)o1;
                Student stu2=(Student)o2;
                Integer a = stu1.pickprobability;
                Integer b = stu2.pickprobability;
                Integer c = stu1.newstudentpick;
                Integer d = stu2.newstudentpick;
                //目的就是为了多属性排序一下，方便选择低年级的学生。
                //先按是否被挑排序，然后按优先级排序
                int fixedComp;
                fixedComp = c.compareTo(d);
                if (fixedComp != 0) {
                    return -fixedComp;
                }
                else {
                    return -(a.compareTo(b));
                }
//                if(stu1.pickprobability>stu2.pickprobability){
//                    return -1;
//                }else if(stu1.pickprobability==stu2.pickprobability){
//                    return 0;
//                }else{
//                    return 1;
//                }
            }
        });
        //考虑到如果这个时间段内年级的人数不够三个人(设定的值班人数)的话
        if(juniorworkers.size() < Main.dutynumber){
            if(juniorworkers.size() == 0){
                System.out.println("星期"+(number/7+1)+"第" + (number/5+1) +"班，低年级人数为零。");
                return null;
            }
            Random r = new Random();
            int i = r.nextInt(juniorworkers.size()); // 生成[0,这个班低年级的总人数]区间的整数,相当于从里面随机挑一个人出来了
            return juniorworkers.get(i);
        }
        else {
            Random r = new Random();
            //// 生成[0,值班人数-1]区间的整数,相当于从前三个人(默认是三)里面挑一个人出来了
            int i = r.nextInt(Main.dutynumber); // 生成[0,2]区间的整数,相当于从前三个人里面挑一个人出来了
            return juniorworkers.get(i);//改成了直接挑最高的,还是不能直接挑最高的，因为有的时候一个班会出现两个同一个人
        }

    }

    public Student pickonesenior(){
        Collections.sort(seniorworkers, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                Student stu1=(Student)o1;
                Student stu2=(Student)o2;
                if(stu1.pickprobability>stu2.pickprobability){
                    return -1;
                }else if(stu1.pickprobability==stu2.pickprobability){
                    return 0;
                }else{
                    return 1;
                }
            }
        });
        if(seniorworkers.size() < Main.dutynumber){//如果高年级的人数小于设定的值班人数
            if(seniorworkers.size() == 0){
                System.out.println("星期"+(number/7+1)+"第" + (number/5+1) +"班，高年级人数为零。");
                return null;
            }

            Random r = new Random();
            int i = r.nextInt(seniorworkers.size()); // 生成[0,高年级总人数-1]区间的整数
            return seniorworkers.get(i);
        }
        else {
            Random r = new Random();
            int i = r.nextInt(Main.dutynumber); // 生成[0,设定的值班人数-1]区间的整数
            return seniorworkers.get(i);
        }
    }

    public Student pickoneall(int... optional){
        Collections.sort(allworkers, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                Student stu1=(Student)o1;
                Student stu2=(Student)o2;
                if(stu1.pickprobability>stu2.pickprobability){
                    return -1;
                }else if(stu1.pickprobability==stu2.pickprobability){
                    return 0;
                }else{
                    return 1;
                }
            }
        });
//        System.out.println(optional.length==0 ? 0:1);
        //这个optional并不是null,即使你并没有向他传入参数
        Random r = new Random();
        int i;// 生成[0,2]区间的整数
        if(optional.length == 0) {
            i = r.nextInt(Main.dutynumber);
        }
        else {//这个可能性，是为了防止这个班的空闲时间人数,不够设定的人数,而导致的产生的随机数越界。
            //但是现在我不需要这个了，因为在之前的代码里，不够的情况，已经直接从allworker里赋值到realworker里面了
            i = r.nextInt(optional[0]);
        }
        return allworkers.get(i);

    }

    //先按照年级，然后按照优先级排序
    public Student pickoneall_newStudent(){
        Collections.sort(allworkers, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                Student stu1=(Student)o1;
                Student stu2=(Student)o2;
                Integer a = stu1.pickprobability;
                Integer b = stu2.pickprobability;
                Integer c = Integer.parseInt(stu1.number);
                Integer d = Integer.parseInt(stu2.number);
                //目的就是为了多属性排序一下，方便选择低年级的学生。
                //先按是否被挑排序，然后按优先级排序
                int fixedComp;
                fixedComp = c.compareTo(d);
                if (fixedComp != 0) {
                    return -fixedComp;
                }
                else {
                    return -(a.compareTo(b));
                }
//                if(stu1.pickprobability>stu2.pickprobability){
//                    return -1;
//                }else if(stu1.pickprobability==stu2.pickprobability){
//                    return 0;
//                }else{
//                    return 1;
//                }
            }
        });
        //考虑到如果这个时间段内年级的人数不够三个人(设定的值班人数)的话
        if(allworkers.size() < Main.dutynumber){
            if(allworkers.size() == 0){
                System.out.println("星期"+(number/7+1)+"第" + (number/5+1) +"班，低年级人数为零。");
                return null;
            }
            Random r = new Random();
            int i = r.nextInt(allworkers.size()); // 生成[0,这个班低年级的总人数]区间的整数,相当于从里面随机挑一个人出来了
            return allworkers.get(i);
        }
        else {
            Random r = new Random();
            //// 生成[0,值班人数-1]区间的整数,相当于从前三个人(默认是三)里面挑一个人出来了
            int i = r.nextInt(Main.dutynumber); // 生成[0,2]区间的整数,相当于从前三个人里面挑一个人出来了
            return allworkers.get(i);//改成了直接挑最高的,还是不能直接挑最高的，因为有的时候一个班会出现两个同一个人
        }
    }
}
