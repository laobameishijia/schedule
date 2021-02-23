package Schedule;

import Ui.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import static java.lang.System.exit;

public class Main {
    public static String filepath; //文件路径
    public static String scheduletype; //排班方式
    public static int oldnumber;//高年级人数
    public static int dutynumber;//值班人数
    public static String customornot;//是否自定义
    public static List timelist;//自定义的时间
    public static String title = null;//值班表的标题

    public static ArrayList<Student> students = new ArrayList<Student>();//存储所有同学
    public static ArrayList<FreeTime> freetimes = new ArrayList<FreeTime>();//存储所有空闲时间
    public static Integer timenum = 1;//用于表示35个班

    public static List convertStringToList(String str, String mark) {
        String[] strArray = str.split(mark);
        List list = Arrays.asList(strArray);
        return list;
    }

    public static String convertListToString(List list, String mark) {
        return StringUtils.join(list, mark).toString();
    }

    //截取值班结束时间段的时间，用来判断上下午
    public static String cutstring(String str) {
        //他是默认把两个起始位置的字符串也保存下来的
        Pattern p1 = Pattern.compile("-(.*?):");
        Matcher m = p1.matcher(str);
        ArrayList<String> list = new ArrayList<String>();
        while (m.find()) {
            list.add(m.group().trim().replace("\"", ""));
        }
        return list.get(0).replace(":", "").replace("-", "");
    }

    //用这个函数来创建值班表
    public static Sheet createSheet(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("值班表");

        if (sheet == null) {
            System.out.println("表单" + "值班表" + "不存在，试图创建该sheet，请稍后……");
            sheet = wb.createSheet("值班表");
            System.out.println("名为" + "值班表" + "的sheet创建成功！");
            //标题的格式
            CellStyle style1 = wb.createCellStyle();
            //文字
            Font font1 = wb.createFont();
            font1.setFontName("宋体");
            font1.setFontHeightInPoints((short) 24);
            font1.setBold(true);
            //设置样式
            style1.setFont(font1);
            style1.setAlignment(HorizontalAlignment.CENTER);        //横向居中
            style1.setVerticalAlignment(VerticalAlignment.CENTER);//纵向居中
            style1.setBorderTop(BorderStyle.THIN);                //上细线
            style1.setBorderBottom(BorderStyle.THIN);            //下细线
            style1.setBorderLeft(BorderStyle.THIN);                //左细线
            style1.setBorderRight(BorderStyle.THIN);                //右细线

            //标题行
            //这里也不是必须要是XSSF
            Row row = sheet.createRow((short) 0);
            row.createCell(0).setCellValue(title);//设置标题
            // 合并单元格：参数：起始行, 终止行, 起始列, 终止列
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, 8);
            sheet.addMergedRegion(cra);
            row.getCell(0).setCellStyle(style1);

            //除了标题以外的小格子的格式
            CellStyle style = wb.createCellStyle();
            //创建字体对象
            Font font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 24);
            //设置样式
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);        //横向居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);//纵向居中
            style.setBorderTop(BorderStyle.THIN);                //上细线
            style.setBorderBottom(BorderStyle.THIN);            //下细线
            style.setBorderLeft(BorderStyle.THIN);                //左细线
            style.setBorderRight(BorderStyle.THIN);                //右细线
            style.setWrapText(true);                        //自动换行


            row = sheet.createRow(1);
            row.createCell(0);
            row.createCell(1);
            row.createCell(2).setCellValue("星期一");
            row.createCell(3).setCellValue("星期二");
            row.createCell(4).setCellValue("星期三");
            row.createCell(5).setCellValue("星期四");
            row.createCell(6).setCellValue("星期五");
            row.createCell(7).setCellValue("星期六");
            row.createCell(8).setCellValue("星期日");
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.setCellStyle(style);
            }

            //如果是自定义的话
            if (customornot.equals("自定义")) {
                int rowcount = timelist.size();
                int moring = 0;//记录属于早上时间段的个数
                int noon = 0;//中午
                int afternoon = 0;//下午
                int evening = 0;//晚上
                for (int i = 0; i < rowcount; i++) {

                    row = sheet.createRow(i + 2);
                    //截取时间，然后判断属于时间段
                    int temp = Integer.parseInt(cutstring(timelist.get(i).toString()));
                    if (temp <= 12) {
                        row.createCell(0).setCellValue("上午");
                        moring++;
                    } else if (temp <= 14) {
                        row.createCell(0).setCellValue("中午");
                        noon++;
                    } else if (temp <= 18) {
                        row.createCell(0).setCellValue("下午");
                        afternoon++;
                    } else {
                        row.createCell(0).setCellValue("晚上");
                        evening++;
                    }

                    row.createCell(1).setCellValue(timelist.get(i).toString());
                    row.getCell(0).setCellStyle(style);
                    row.getCell(1).setCellStyle(style);
                }

                //合并单元格并居中
                CellRangeAddress cra1 = new CellRangeAddress(2, 1 + moring, 0, 0);
                sheet.addMergedRegion(cra1);
                CellRangeAddress cra2 = new CellRangeAddress(2 + moring + noon, 1 + moring + noon + afternoon, 0, 0);
                sheet.addMergedRegion(cra2);

                //设置列宽行高
                for (int i = 0; i < 9; i++) {
                    sheet.setColumnWidth(i, 20 * 256);
                }
                for (int i = 0; i < 2 + timelist.size(); i++) {
                    sheet.getRow(i).setHeightInPoints(115);
                }

            }
            //如果要是默认的话
            else {
                sheet.createRow(2);
                sheet.createRow(3);
                sheet.createRow(4);
                sheet.createRow(5);
                sheet.createRow(6);

                row = sheet.getRow(2);
                row.createCell(0).setCellValue("上午");
                row.createCell(1).setCellValue("8:00-10:00");
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);

                row = sheet.getRow(3);
                row.createCell(0);
                row.createCell(1).setCellValue("10:00-12:00");
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);

                row = sheet.getRow(4);
                row.createCell(0).setCellValue("下午");
                row.createCell(1).setCellValue("14:00-16:00");
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);

                row = sheet.getRow(5);
                row.createCell(0);
                row.createCell(1).setCellValue("16:00-18:00");
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);

                row = sheet.getRow(6);
                row.createCell(0).setCellValue("晚上");
                row.createCell(1).setCellValue("19:00-22:00");
                row.getCell(0).setCellStyle(style);
                row.getCell(1).setCellStyle(style);

                //合并单元格并居中,只用合并上午和下午
                CellRangeAddress cra1 = new CellRangeAddress(2, 3, 0, 0);
                sheet.addMergedRegion(cra1);
                CellRangeAddress cra2 = new CellRangeAddress(4, 5, 0, 0);
                sheet.addMergedRegion(cra2);

                //设置列宽行高
                for (int i = 0; i < 9; i++) {
                    sheet.setColumnWidth(i, 20 * 256);
                }
                for (int i = 0; i < 7; i++) {
                    sheet.getRow(i).setHeightInPoints(115);
                }
            }
        }
        //行高列宽在改一改，标题的边框设置一下，基本问题不大了
        return sheet;
    }

    //审查空闲时间表是否符合标准
    public static List Inspectiondocuments1(String path) {
        String filePath = path;
        InputStream fis = null;
        List problem = Collections.singletonList(new int[]{0, 0});
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = null;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            int standardrows;//标准行数
            int standardcoloumn;//标准列数
            if (customornot.equals("自定义")) {
                standardrows = 1 + timelist.size();
                standardcoloumn = 7;
            } else {
                standardrows = 1 + 5;
                standardcoloumn = 7;
            }
            /* 读EXCEL文字内容 */
            //获取第一个sheet表
            Sheet sheet = workbook.getSheetAt(0);
            /*
            System.out.println(sheet.getPhysicalNumberOfRows());//6
            System.out.println(sheet.getLastRowNum());//5

            System.out.println(sheet.getRow(0).getPhysicalNumberOfCells());//7
            System.out.println(sheet.getRow(0).getLastCellNum());//8

            System.out.println(sheet.getRow(1).getPhysicalNumberOfCells());//8
            System.out.println(sheet.getRow(1).getLastCellNum());//8
            */
            int sheet0_coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();////7
            int sheet0_rowNum = sheet.getPhysicalNumberOfRows();//编号1开始的。excel里面是从1开始的
            List list = new ArrayList();

            list.add(sheet0_coloumNum);
            list.add(sheet0_rowNum-1);
            if (sheet0_coloumNum != standardcoloumn || sheet0_rowNum != standardrows) {
                return problem;
            } else return list;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return problem;
        } catch (IOException e) {
            e.printStackTrace();
            return problem;
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //审查班数表是否符合标准
    public static List Inspectiondocuments2(String path) {
        List problem = Collections.singletonList(new int[]{0, 0});
        String filePath = path;
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = null;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            /* 读EXCEL文字内容 */

            // 获取第二个sheet表，也可使用sheet表名获取
            Sheet sheet1 = workbook.getSheetAt(1);
            //获取总行数和总列数
            int sheet1_coloumNum = sheet1.getRow(0).getPhysicalNumberOfCells();
            int sheet1_rowNum = sheet1.getPhysicalNumberOfRows();//编号1开始的。excel里面是从1开始的
            List list = new ArrayList();
            list.add(sheet1_coloumNum);
            list.add(sheet1_rowNum);

            return list;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return problem;
        } catch (IOException e) {
            e.printStackTrace();
            return problem;
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void getallstudents(String path) {
//        String filePath = "F:\\c语言实践\\排班\\src\\main\\java\\空闲时间表.xlsx";
        String filePath = path;
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = null;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            /* 读EXCEL文字内容 */
            // 获取第二个sheet表，也可使用sheet表名获取
            Sheet sheet = workbook.getSheetAt(1);
            //获取总行数和总列数
            int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
            int rowNum = sheet.getLastRowNum();//编号零开始的。excel里面是从1开始的
            Row row;
            Cell cell, cell1;

            //获取所有同学的姓名
            for (int i = 1; i <= rowNum; i++) {
                row = sheet.getRow(i);
                cell = row.getCell(1);//获取姓名
                List cellList = convertStringToList(cell.getStringCellValue(), " ");
                /*输出样例：[杨新羽]*/
                //System.out.print(cellList + "\n");
                cell1 = row.getCell(0);//获取年级
                cell1.setCellType(CellType.STRING);
                List cellList1 = convertStringToList(cell1.getStringCellValue(), " ");
                //创建同学类，添加到list里面
                Student student = new Student();
                student.name = cellList.get(0).toString();
                student.number = cellList1.get(0).toString();
                students.add(student);
            }

            //创建值班表
//            Sheet sheet1 = createSheet((XSSFWorkbook) workbook);
//            //写入保存文件
//            FileOutputStream fileOut = new FileOutputStream(filePath);
//            workbook.write(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void getfreetime(String path) {
//        String filePath = "F:\\c语言实践\\排班\\src\\main\\java\\空闲时间表.xlsx";
        String filePath = path;
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = null;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            /* 读EXCEL文字内容 */
            // 获取第一个sheet表，也可使用sheet表名获取
            Sheet sheet = workbook.getSheetAt(0);
            //获取总行数和总列数
            int rowNum = sheet.getPhysicalNumberOfRows();
            int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();//因为这个是获取有数据的列，
            //由于第一个列没有数据，所以这个数是7

            Row row;
            Cell cell;

            //循环读取内容，并且创建相关的对象。并且初始化相关的列表
            //按列来给定number序号的
            //虽然是一行一行地在进行读取，但是是按照列来对每一个值班时间进行标识的
            for (int i = 1; i < rowNum; i++) {
                row = sheet.getRow(i);
                for (int j = 1; j <= coloumNum; j++) {
                    FreeTime freetime = new FreeTime();
                    timenum = i + (j - 1) * 5;
                    cell = row.getCell(j);
                    cell.setCellType(CellType.STRING);
                    List cellList = convertStringToList(cell.getStringCellValue().replace("\n", " "), " ");
                    for (int k = 0; k < cellList.size(); k++) {
                        String name = cellList.get(k).toString();
                        students.forEach((e) -> {
                            if (e.name.equals(name)) {
                                //同学添加空闲时间，---王：1，2，3
                                e.freetime.add(timenum);
                                //空闲时间添加同学,---1：王；朱；李
                                freetime.number = timenum;
                                if (e.number.equals("1")) freetime.juniorworkers.add(e);
                                else freetime.seniorworkers.add(e);
                                freetime.allworkers.add(e);
                            }
                        });
                    }
                    freetimes.add(freetime);
                }
            }

            for (FreeTime e : freetimes) {
                e.classifybypriority();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //重新计算评判值
    public static void recalculateprobability() {
        freetimes.forEach((e) -> {
            e.seniorworkers.forEach((a) -> {
                a.calculatepickprobability();
            });
            e.juniorworkers.forEach((c) -> {
                c.calculatepickprobability();
            });
        });
    }

    //第二天开始的时候，把每个人的today初始化为1
    public static void retodaypick() {
        freetimes.forEach((e) -> {
            e.seniorworkers.forEach((a) -> {
                a.retodaypick();
            });
            e.juniorworkers.forEach((c) -> {
                c.retodaypick();
            });
        });
    }

    //根据每个人的参数去挑选值班人员
    public static void selectrealworkers() {
        //先计算每一个同学的可能程度
        recalculateprobability();
        //然后按星期一-星期二这样的顺序去挑选值班人员
        //有个问题，就是在操作freetimes.seniorworkers里面的students实例时，会不会作用到全局里面
        //我觉得应该是可以的，因为他们都是类变量，是作用于全局的。

        for (int i = 0; i < 7; i++) {//确定七天
            for (int j = 0; j < 5; j++) {//确定一天中的五个时间段
                int m = oldnumber;//高年级的人数
                int n = dutynumber - oldnumber;//剩余的人数

                int k = i + j * 7;//保证按照每天的五个时间段去排班

                //如果高年级和低年级的人数都不够咋办，因为你现在是默认的高年级1个人，低年级2个人
                int seniorconunt = freetimes.get(k).seniorworkers.size();
                int juniorconunt = freetimes.get(k).juniorworkers.size();
                //不能按照上面的默认来，原来的意思是每个班至少一个高年级，如果卡死的话，低年级的人的班数会增多
                //如果空闲时间内的人数超过设定的值班人数时
                if (seniorconunt + juniorconunt >= dutynumber) {
                    if (seniorconunt >= m) {
                        while (m > 0) {
                            //先挑高年级，满足要求
                            Student senior = freetimes.get(k).pickonesenior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(senior))
                                continue;
                            freetimes.get(k).realworkers.add(senior);
                            //当天被选，而且优先级降低
                            senior.todaypick = 0;
                            senior.priority = senior.priority - 1;
                            //更新班数,晚班算1.5
                            senior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) senior.worknums = (float) (senior.worknums + 1.5);
                            else senior.worknums = senior.worknums + 1;
                            recalculateprobability();//更新权重
                            m--;
                        }
                        //在从总共的里面再跳剩下的人
                        //这里就又出现了一个问题，如果这里面跳出来的人，跟前面的跳出来的一样怎么办？？？
                        while (n > 0) {
                            Student oneofall = freetimes.get(k).pickoneall();
                            //如果跳出来的人，已经在这个班里面值班了，那么我们可以重新挑选
                            if (freetimes.get(k).realworkers.contains(oneofall))
                                continue;
                            freetimes.get(k).realworkers.add(oneofall);
                            //当天被选，而且优先级降低
                            oneofall.todaypick = 0;
                            oneofall.priority = oneofall.priority - 1;
                            //更新班数,晚班算1.5
                            oneofall.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) oneofall.worknums = (float) (oneofall.worknums + 1.5);
                            else oneofall.worknums = oneofall.worknums + 1;

                            recalculateprobability();//更新权重
                            n--;
                        }

                    }
                    //高年级人数不够要求的话，只能是把高年级的全部调出来，剩下的再从全部低年级的人里面挑。
                    else {
                        m = seniorconunt;//高年级的人数
                        n = dutynumber - seniorconunt;//剩下的人数
                        while (m > 0) {
                            //先挑一个高年级
                            Student senior = freetimes.get(k).pickonesenior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(senior))
                                continue;
                            freetimes.get(k).realworkers.add(senior);
                            //当天被选，而且优先级降低
                            senior.todaypick = 0;
                            senior.priority = senior.priority - 1;
                            //更新班数,晚班算1.5
                            senior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) senior.worknums = (float) (senior.worknums + 1.5);
                            else senior.worknums = senior.worknums + 1;
                            recalculateprobability();//更新权重
                            m--;
                        }
                        while (n > 0) {
                            Student junior = freetimes.get(k).pickonejunior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(junior))
                                continue;
                            freetimes.get(k).realworkers.add(junior);
                            //当天被选，而且优先级降低
                            junior.todaypick = 0;
                            junior.priority = junior.priority - 1;
                            //更新班数,晚班算1.5
                            junior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) junior.worknums = (float) (junior.worknums + 1.5);
                            else junior.worknums = junior.worknums + 1;
                            n--;
                            recalculateprobability();//更新权重
                        }
                    }
                }
                //这个地方就没有必要采用循环的方式去挑选需要的人了，直接从对应序列里面把所有的对象都复制到另一个队列里面
                //然后按照之前的更新权重和相关的属性的值就行了
                else {//如果说，这个空闲时间内所有人加起来，都小于我们设定的人数，那么只能是全部挑出来了。
                    freetimes.get(k).allworkers.forEach(e -> {
                        freetimes.get(k).realworkers.add(e);
                        //当天被选，而且优先级降低
                        e.todaypick = 0;
                        e.priority = e.priority - 1;
                        //更新班数,晚班算1.5
                        e.worktime.add(k);
                        //反正老同学也不按照newstudentpick进行排序，所以我们就直接把他们都变成0就好了
                        //对老同学来说，这个设置无所谓，所以在这也就不再区分了。
                        e.newstudentpick = 0;
                        if (freetimes.get(k).number % 5 == 0) e.worknums = (float) (e.worknums + 1.5);
                        else e.worknums = e.worknums + 1;
                        //更新权重
                        recalculateprobability();
                    });
//                    int o = juniorconunt + seniorconunt;
//                    while (o>0) {
//                        Student oneofall = freetimes.get(k).pickoneall(o);
//                        //如果跳出来的人，已经在这个班里面值班了，那么我们可以重新挑选
//                        if (freetimes.get(k).realworkers.contains(oneofall))
//                            continue;
//                        freetimes.get(k).realworkers.add(oneofall);
//                        //当天被选，而且优先级降低
//                        oneofall.todaypick = 0;
//                        oneofall.priority = oneofall.priority - 1;
//                        //更新班数,晚班算1.5
//                        oneofall.worktime.add(k);
//                        if (freetimes.get(k).number % 5 == 0) oneofall.worknums = (float) (oneofall.worknums + 1.5);
//                        else oneofall.worknums = oneofall.worknums + 1;
//
//                        recalculateprobability();//更新权重
//                        o--;
//                    }
                }
            }

            retodaypick();
            recalculateprobability();
        }
        //现在就是按照每一天去排班就行了，挑选人的工作已经完成了
    }

    //新成员排班，基本保证每个新同学都有一个班。，明天可以再测试一下。
    public static void selectrealworkersbynewstudents() {
        //先计算每一个同学的可能程度
        recalculateprobability();
        //然后按星期一-星期二这样的顺序去挑选值班人员
        //有个问题，就是在操作freetimes.seniorworkers里面的students实例时，会不会作用到全局里面
        //我觉得应该是可以的，因为他们都是类变量，是作用于全局的。

        for (int i = 0; i < 7; i++) {//确定七天
            for (int j = 0; j < 5; j++) {//确定一天中的五个时间段
                int m = oldnumber;//高年级的人数
                int n = dutynumber - oldnumber;//剩余的人数
                int r = n - 1;//表示挑出一个低年级的人，后剩下的人数。

                int k = i + j * 7;//保证按照每天的五个时间段去排班

                //如果高年级和低年级的人数都不够咋办，因为你现在是默认的高年级1个人，低年级2个人
                int seniorconunt = freetimes.get(k).seniorworkers.size();
                int juniorconunt = freetimes.get(k).juniorworkers.size();
                //不能按照上面的默认来，原来的意思是每个班至少一个高年级，如果卡死的话，低年级的人的班数会增多
                //如果空闲时间内的人数超过设定的值班人数时
                if (seniorconunt + juniorconunt >= dutynumber) {
                    if (seniorconunt >= m) {
                        //先挑出满足高年级的人数
                        while (m > 0) {
                            //先挑高年级，满足要求
                            Student senior = freetimes.get(k).pickonesenior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(senior))
                                continue;
                            freetimes.get(k).realworkers.add(senior);
                            //当天被选，而且优先级降低
                            senior.todaypick = 0;
                            senior.priority = senior.priority - 1;
                            //更新班数,晚班算1.5
                            senior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) senior.worknums = (float) (senior.worknums + 1.5);
                            else senior.worknums = senior.worknums + 1;
                            recalculateprobability();//更新权重
                            m--;
                        }
                        //在挑出一个低年级的人来,就挑那些没有被跳过的。
                        if (juniorconunt >= 1) {
                            //按照顺序挑一个低年级的出来
                            Student junior = freetimes.get(k).pickonejunior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            //但其实这样是不太合理的，因为你想啊，这个按道理来说，不应该挑出来一个就让他跳出去
                            //但是呢，从前面三个人里面抽到一个，说明这个几率也挺大的了，跳出去效果上，还真的可以。
                            if (freetimes.get(k).realworkers.contains(junior) || junior.newstudentpick == 0)
                                //这里的continue直接跳转到最外层的那个for循环上面了
                                continue;
                            freetimes.get(k).realworkers.add(junior);
                            //当天被选，而且优先级降低
                            junior.todaypick = 0;
                            junior.priority = junior.priority - 1;
                            junior.newstudentpick = 0;
                            //更新班数,晚班算1.5
                            junior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) junior.worknums = (float) (junior.worknums + 1.5);
                            else junior.worknums = junior.worknums + 1;
                            recalculateprobability();//更新权重

                            //在去总共的里面去挑剩下的人
                            while (r > 0) {
                                Student oneofall = freetimes.get(k).pickoneall();
                                //如果跳出来的人，已经在这个班里面值班了，那么我们可以重新挑选
                                if (freetimes.get(k).realworkers.contains(oneofall))
                                    continue;
                                freetimes.get(k).realworkers.add(oneofall);
                                //当天被选，而且优先级降低
                                oneofall.newstudentpick = 0;
                                oneofall.todaypick = 0;
                                oneofall.priority = oneofall.priority - 1;
                                //更新班数,晚班算1.5
                                oneofall.worktime.add(k);
                                if (freetimes.get(k).number % 5 == 0)
                                    oneofall.worknums = (float) (oneofall.worknums + 1.5);
                                else oneofall.worknums = oneofall.worknums + 1;

                                recalculateprobability();//更新权重
                                r--;
                            }
                        } else {
                            //在从总共的里面再跳剩下的人
                            while (n > 0) {
                                Student oneofall = freetimes.get(k).pickoneall();
                                //如果跳出来的人，已经在这个班里面值班了，那么我们可以重新挑选
                                if (freetimes.get(k).realworkers.contains(oneofall))
                                    continue;
                                freetimes.get(k).realworkers.add(oneofall);
                                //当天被选，而且优先级降低
                                oneofall.todaypick = 0;
                                oneofall.priority = oneofall.priority - 1;
                                //更新班数,晚班算1.5
                                oneofall.worktime.add(k);
                                if (freetimes.get(k).number % 5 == 0)
                                    oneofall.worknums = (float) (oneofall.worknums + 1.5);
                                else oneofall.worknums = oneofall.worknums + 1;

                                recalculateprobability();//更新权重
                                n--;
                            }
                        }
                    }
                    //高年级人数不够要求的话，只能是把高年级的全部调出来，剩下的再从全部低年级的人里面挑。
                    else {
                        m = seniorconunt;//高年级的人数
                        n = dutynumber - seniorconunt;//剩下的人数
                        while (m > 0) {
                            //先挑一个高年级
                            Student senior = freetimes.get(k).pickonesenior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(senior))
                                continue;
                            freetimes.get(k).realworkers.add(senior);
                            //当天被选，而且优先级降低
                            senior.todaypick = 0;
                            senior.priority = senior.priority - 1;
                            //更新班数,晚班算1.5
                            senior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) senior.worknums = (float) (senior.worknums + 1.5);
                            else senior.worknums = senior.worknums + 1;
                            recalculateprobability();//更新权重
                            m--;
                        }
                        while (n > 0) {
                            Student junior = freetimes.get(k).pickonejunior();
                            //如果该值班人员里面，已经包含选出来的人，那么只能进行重新选择
                            if (freetimes.get(k).realworkers.contains(junior) || junior.newstudentpick == 0)
                                continue;
                            freetimes.get(k).realworkers.add(junior);
                            //当天被选，而且优先级降低
                            junior.todaypick = 0;
                            junior.priority = junior.priority - 1;
                            junior.newstudentpick = 0;
                            //更新班数,晚班算1.5
                            junior.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) junior.worknums = (float) (junior.worknums + 1.5);
                            else junior.worknums = junior.worknums + 1;
                            n--;
                            recalculateprobability();//更新权重
                        }
                    }
                } else {//如果说，这个空闲时间内所有人加起来，都小于我们设定的人数，那么只能是全部挑出来了。
                    freetimes.get(k).allworkers.forEach(e -> {
                        freetimes.get(k).realworkers.add(e);
                        //当天被选，而且优先级降低
                        e.todaypick = 0;
                        e.priority = e.priority - 1;
                        //更新班数,晚班算1.5
                        e.worktime.add(k);
                        //反正老同学也不按照newstudentpick进行排序，所以我们就直接把他们都变成0就好了
                        //对老同学来说，这个设置无所谓，所以在这也就不再区分了。
                        e.newstudentpick = 0;
                        if (freetimes.get(k).number % 5 == 0) e.worknums = (float) (e.worknums + 1.5);
                        else e.worknums = e.worknums + 1;
                        //更新权重
                        recalculateprobability();
                    });
                }
            }

            retodaypick();
            recalculateprobability();
        }


        //由于之前的班，有的班只有一个人，那么我们现在重新去给她们补全就完事了。
        for (int i = 0; i < 7; i++) {//确定七天
            for (int j = 0; j < 5; j++) {//确定一天中的五个时间段
                int k = i + j * 7;//保证按照每天的五个时间段去排班
                int realworkers = freetimes.get(k).realworkers.size();
                int allworkers = freetimes.get(k).allworkers.size();
                int n = dutynumber - realworkers;

                if (realworkers < 3) {

                    if (allworkers > dutynumber) {
                        while (n > 0) {
                            Student oneofall = freetimes.get(k).pickoneall_newStudent();
                            //如果跳出来的人，已经在这个班里面值班了，那么我们可以重新挑选
                            if (freetimes.get(k).realworkers.contains(oneofall))
                                continue;
                            freetimes.get(k).realworkers.add(oneofall);
                            //当天被选，而且优先级降低
                            oneofall.todaypick = 0;
                            oneofall.priority = oneofall.priority - 1;
                            //更新班数,晚班算1.5
                            oneofall.worktime.add(k);
                            if (freetimes.get(k).number % 5 == 0) oneofall.worknums = (float) (oneofall.worknums + 1.5);
                            else oneofall.worknums = oneofall.worknums + 1;

                            recalculateprobability();//更新权重
                            n--;
                        }
                    } else {
                        freetimes.get(k).allworkers.forEach(e -> {
                            //如果这里是不包含的话，我们再从空闲时间里面把他们跳出来
                            if (!freetimes.get(k).allworkers.contains(e)) {
                                freetimes.get(k).realworkers.add(e);
                                //当天被选，而且优先级降低
                                e.todaypick = 0;
                                e.priority = e.priority - 1;
                                //更新班数,晚班算1.5
                                e.worktime.add(k);
                                //反正老同学也不按照newstudentpick进行排序，所以我们就直接把他们都变成0就好了
                                //对老同学来说，这个设置无所谓，所以在这也就不再区分了。
                                e.newstudentpick = 0;
                                if (freetimes.get(k).number % 5 == 0) e.worknums = (float) (e.worknums + 1.5);
                                else e.worknums = e.worknums + 1;
                                //更新权重
                                recalculateprobability();
                            }
                        });
                    }
                }

            }

            retodaypick();
            recalculateprobability();
        }
    }

    //输出文件
    public static void exportfile(String path) {
//        String filePath = "F:\\c语言实践\\排班\\src\\main\\java\\空闲时间表.xlsx";
        String filePath = path;
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = null;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls") || filePath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            /* 读EXCEL文字内容 */
            // 获取第二个sheet表，也可使用sheet表名获取
            Sheet sheet = workbook.getSheetAt(1);
            //获取总行数和总列数
            int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
            int rowNum = sheet.getLastRowNum();
            Row row;
            Cell cell, cell1;

            //获取所有同学的姓名
            for (int i = 1; i <= rowNum; i++) {
                row = sheet.getRow(i);
                cell = row.getCell(1);//获取姓名
                cell1 = row.createCell(2);//获取班数
                List cellList = convertStringToList(cell.getStringCellValue(), " ");
                /*输出样例：[杨新羽]*/
                //System.out.print(cellList + "\n");
                Cell finalCell = cell1;
                students.forEach((e) -> {
                    if (e.name.equals(cellList.get(0).toString())) {
                        //把班数写到里面
                        finalCell.setCellValue(e.worknums);
                    }
                });
            }

            //测试创建值班表
            Sheet sheetschedule1 = createSheet((XSSFWorkbook) workbook);

            Sheet sheetschedule = workbook.getSheetAt(2);
            //获取总行数和总列数
            int coloumNum1 = sheetschedule.getRow(1).getPhysicalNumberOfCells();
            int rowNum1 = sheetschedule.getPhysicalNumberOfRows();
            Row row_;
            Cell cell_;

            //创建单元格样式对象
            CellStyle style = workbook.createCellStyle();
            //创建字体对象
            Font font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 24);
            //设置样式
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);        //横向居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);//纵向居中
            style.setBorderTop(BorderStyle.THIN);                //上细线
            style.setBorderBottom(BorderStyle.THIN);            //下细线
            style.setBorderLeft(BorderStyle.THIN);                //左细线
            style.setBorderRight(BorderStyle.THIN);                //右细线
            style.setWrapText(true);                        //自动换行

            for (int i = 2; i < rowNum1; i++) {
                row_ = sheetschedule.getRow(i);
                for (int j = 2, k = 0; j < coloumNum1; j++) {
                    k = (i - 2) * 7 + j - 2;
                    cell_ = row_.createCell(j);
                    String realworkers = null;
                    //realworkers = freetimes.get(k).realworkers.toString();
                    List list = new ArrayList();
                    freetimes.get(k).realworkers.forEach((e) -> {
                        list.add(e.name);
                    });
                    realworkers = convertListToString(list, "\n");
                    cell_.setCellValue(realworkers);
                    //为单元格设置样式
                    cell_.setCellStyle(style);
                }
            }

            //写入保存文件
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    public static void main(ArrayList<String> args) {
    public static void main(String[] args) {
//        filepath = args.get(0);
//        scheduletype = args.get(1);
//        dutynumber = Integer.parseInt(args.get(2));
//        oldnumber = Integer.parseInt(args.get(3));
//        customornot = args.get(4);
//        timelist =convertStringToList(args.get(5),",");
//        title = args.get(6);
        filepath = "C://Users//Administrator//Desktop//2020江安秋季面试后值班表.xlsx";
        scheduletype = "新同学排班";
        dutynumber = 3;
        oldnumber = 1;
        customornot = "默认";
        timelist = Arrays.asList(new String[]{"8:00-10:00", "10:00-12:00", "12:00-13:00", "14:00-16:00", "16:00-18:00", "19:00-22:00"});
        title = "测试";
        String test = cutstring(timelist.get(0).toString());
        System.out.println(test);

        System.out.println(filepath + scheduletype + dutynumber + oldnumber + customornot + timelist);


        getallstudents(filepath);
        System.out.println("读取学生名单....");

        getfreetime(filepath);
        System.out.println("获取学生空闲时间....");

        if (scheduletype.equals("新同学排班"))
            selectrealworkersbynewstudents();//对新同学进行排班，这个我得找到相应的文件才可以
        else selectrealworkers();
        System.out.println("进行排班....");

        exportfile(filepath);
        System.out.println("输出保存文件....");

//        try {
//            getallstudents(filepath);
//            System.out.println("读取学生名单....");
//            MainController.settooltip("读取学生名单....");
//        }catch (Exception e){
//            MainController.tip1(e.getMessage());
//        }
//
//        try {
//            getfreetime(filepath);
//            System.out.println("获取学生空闲时间....");
//            MainController.settooltip("获取学生空闲时间....");
//        }catch (Exception e) {
//            MainController.tip2(e.getMessage());
//        }
//
//        try {
//            if(scheduletype.equals("新同学排班"))
//                selectrealworkersbynewstudents();//对新同学进行排班，这个我得找到相应的文件才可以
//            else selectrealworkers();
//            System.out.println("进行排班....");
//            MainController.settooltip("进行排班....");
//        }catch (Exception e) {
//            MainController.tip3(e.getMessage());
//        }
//
//        try {
//            exportfile(filepath);
//            System.out.println("输出保存文件....");
//            MainController.settooltip("输出保存文件....");
//        }catch (Exception e) {
//            MainController.tip4(e.getMessage());
//        }

        exit(0);
    }
}