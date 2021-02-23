package Schedule;

import java.util.ArrayList;

public class Student {

    public String name;//名字
    public String number;//序号,年级
    public int todaypick = 1;//标识在同一天内，该同学是否被挑选,挑中就是0，没有挑中就是1
    public int newstudentpick = 1;//标识在新同学排班的过程中是否已经被挑选
    public int priority = 3;//排班的优先级,初始默认值为3
    public ArrayList<Integer> freetime = new ArrayList<Integer>();//存储空闲时间
    public ArrayList<Integer> worktime = new ArrayList<Integer>();//存储值班时间
    public float worknums;//值班次数，有可能是小数。
    public int pickprobability;//用来评估这个同学在多大程度上可以被挑选

    public void calculatepickprobability() {
        pickprobability = (int) (todaypick * 10 + priority * 30 + freetime.size() * (-5) + (3.5 - worktime.size()) * 40); //相比较今天是否被挑选上，已有班的个数显然是比较重要
        //如果这个同学的空闲时间个数比较少，那么也应该把他的优先级调高 也就相当于优先安排他//所以在权重上面，已有班的个数所占的比重要大一些
    }

    public void retodaypick() {
        todaypick = 1;
    }

}
