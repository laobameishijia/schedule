package Ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {

    //Fxml可以直接通过这个fx:id来更新相关的页面。这比swing方便
    @FXML
    private TextField filepath;//标识文件路径

    @FXML
    private MenuItem help;

    @FXML
    private MenuItem technology;

    @FXML
    private MenuItem sample;

    @FXML
    private MenuItem web;

    @FXML
    private ToggleGroup schedule;//标识排班的类型

    @FXML
    private ToggleGroup Custom;//标识是否选择自定义时间段
    @FXML
    private RadioButton Buttondefault;
    @FXML
    private RadioButton Buttoncustom;

    @FXML
    private ChoiceBox<?> headcount;//单个班内的总人数

    @FXML
    private ChoiceBox<?> seniorstudent;//班内高年级的学生人数

    @FXML
    private AnchorPane customplate;//自定义板块

    @FXML
    private VBox timebox;          //填写时间的板块

    @FXML
    public TextField Tooltiptext;//分步骤的文本提示

    @FXML
    private TextField Scheduletitle;//值班表的标题

    @FXML
    private HBox time;

    public String path;//用于标识文件路径

    //截取单引号之间的内容
    String cutstring(String str) {
        //截取单引号之间的内容
        Pattern p1 = Pattern.compile("\'(.*?)\'");
        Matcher m = p1.matcher(str);
        ArrayList<String> list = new ArrayList<String>();
        while (m.find()) {
            list.add(m.group().trim().replace("\"", ""));
        }
        return list.get(0).replace("\'", "");
    }

    List getcustomtime() {
        //ObservableList<Node> timeNodes
        //这种写法没有问题，但是没有办法绕过中间的那个冒号
        //for (Node node : timebox.getChildren()) {
        //time = (HBox)node;
        //for (Node node1 : time.getChildren()){
        //TextField text =(TextField) node1;

        //}
        //}
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < timebox.getChildren().size(); i++) {
            time = (HBox) timebox.getChildren().get(i);
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < time.getChildren().size(); j++) {
                if (j == 1) {
                    str.append("-");
                } else {
                    TextField text = (TextField) time.getChildren().get(j);
                    str.append(text.getText());
                }
            }
            list.add(str.toString());
        }
        return list;
    }

    public static String convertListToString(List list, String mark) {
        return StringUtils.join(list, mark).toString();
    }

    public static List convertStringToList(String str, String mark) {
        String[] strArray = str.split(mark);
        List list = Arrays.asList(strArray);
        return list;
    }

    //打开文件
    @FXML
    void openfile(MouseEvent event) {
        Stage mainStage = null;
        FileChooser fileChooser = new FileChooser();//构建一个文件选择器实例
        //在实例中选择“打开文件”模式在传入窗口中显示。可以看出他返回用户选择文件的一个实例
        //可以对实例fileChooser打开窗口的标题设置：
        fileChooser.setTitle("选择文件");
        //使用以下语句来为文件选择设置过滤器：
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xlsx Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        path = selectedFile.getPath();
        //把文件路径显示到文件路径上
        if (path != null) {
            filepath.setText(path);
        }

    }

    //展开自定义板块
    @FXML
    void showcustom(MouseEvent event) {
        customplate.setVisible(true);
    }

    //收缩自定义板块
    @FXML
    void notshowcustom(MouseEvent event) {
        customplate.setVisible(false);
    }

    //点击添加时间
    @FXML
    void addtimecustom(MouseEvent event) {
        //理论上来说，下面这种方式应该也是可以的，但是不知道为什么就是出不来效果
        //AnchorPane time1 = new AnchorPane(timecustom);
        //timebox.getChildren().add(time1);

        HBox timeall = new HBox();

        TextField time = new TextField();
        time.setPrefHeight(6.0);
        time.setPrefWidth(60.0);
        time.setLayoutY(2.0);
        timeall.getChildren().add(time);

        //这个位置看不见了，本来是-,现在成了... 不太影响其实也，懒得改了。
        Label time1 = new Label(" - ");
        time1.setLayoutX(70.0);
        time1.setLayoutY(-2.0);
        time1.prefHeight(40.0);
        time1.setPrefWidth(19.0);
        timeall.getChildren().add(time1);

        TextField time2 = new TextField();
        time2.setPrefHeight(6.0);
        time2.setPrefWidth(60.0);
        time2.setLayoutY(2.0);
        timeall.getChildren().add(time2);

        timebox.setSpacing(10.0);
        timebox.getChildren().add(timeall);
    }

    //点击减少时间段
    @FXML
    void subtimecustom(MouseEvent event) {
        timebox.getChildren().remove(0);//这个index 0 是删除视觉上面排在第一个的。
    }

    //针对不同的排班类型，进行页面初始化设置
    @FXML
    void schedule1(MouseEvent event) {
        notshowcustom(event);
        Buttoncustom.setDisable(false);
        Buttondefault.setDisable(false);
        Buttoncustom.setDisable(true);
        Buttondefault.setSelected(true);
    }

    @FXML
    void schedule2(MouseEvent event) {
        notshowcustom(event);
        Buttoncustom.setDisable(false);
        Buttondefault.setDisable(false);
        Buttoncustom.setDisable(true);
        Buttondefault.setSelected(true);
    }

    @FXML
    void schedule3(MouseEvent event) {
        Buttoncustom.setDisable(false);
        Buttondefault.setDisable(false);
        Buttoncustom.setSelected(true);
        showcustom(event);
    }

    @FXML
    void schedule4(MouseEvent event) {
        notshowcustom(event);
        Buttoncustom.setDisable(false);
        Buttondefault.setDisable(false);
        Buttoncustom.setDisable(true);
        Buttondefault.setSelected(true);
    }

    //错误提示
    @FXML
    public static void tip1(String tip) {
        //模态框提示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(App.primaryStage);
        alert.setTitle("文件中的班数表的设置错误");
        alert.setHeaderText("按照下面提示检查错误:");
        alert.setContentText("1.是否按照说明文档中的规定，对文件进行设置。" + "\n" +
                "2.是否按照年级、班级、班次对班数工作簿进行设置。" + "\n" +
                "3.班数工作簿中，是否存在不合理的初值。" + "\n" +
                "4.其他。" + tip);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML
    public static void tip2(String tip) {
        //模态框提示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(App.primaryStage);
        alert.setTitle("获取空闲时间错误");
        alert.setHeaderText("按照下面提示检查错误:");
        alert.setContentText("1.是否按照说明文档中的规定，对文件进行设置。" + "\n" +
                "2.空闲时间表中是否存在空值。" + "\n" +
                "3.空闲时间段中除人名、空格、回车外是否存在其他非法字符" + "\n" +
                "4.其他。" + tip);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML
    public static void tip3(String tip) {
        //模态框提示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(App.primaryStage);
        alert.setTitle("排班过程中出错");
        alert.setHeaderText("按照下面提示检查错误:");
        alert.setContentText("1.是否按照说明文档中的规定，对文件进行设置。" + "\n" +
                "2.如果是自定义时间段，空闲时间表是否与自定义的时间段一一对应" + "\n" +
                "3.其他。" + tip);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML
    public static void tip4(String tip) {
        //模态框提示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(App.primaryStage);
        alert.setTitle("输出文件错误");
        alert.setHeaderText("按照下面提示检查错误:");
        alert.setContentText("1.是否按照说明文档中的规定，对文件进行设置。" + "\n" +
                "2.xlsx文件中是否已经存在有值班表，如果有，请将其删除后，重新进行排班" + "\n" +
                "3.在程序运行过程中，是否已经打开xlsx。如果是，请将其关闭后，重新进行排班" + "\n" +
                "4.其他。" + tip);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML//通用的错误提示，直接把程序的报错传进来就完事了。
    public static void tip5(String tip) {
        //模态框提示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(App.primaryStage);
        alert.setTitle("错误");
        alert.setHeaderText("错误说明:");
        alert.setContentText(tip);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    //获取路径
    public String getPath()
    {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty("os.name").contains("dows"))
        {
            path = path.substring(1,path.length());
        }
        if(path.contains("jar"))
        {
            path = path.substring(0,path.lastIndexOf("."));
            return path.substring(0,path.lastIndexOf("/"));
        }
        return path.replace("target/classes/", "");
    }

    @FXML
    //最下面提醒用户，程序进行到哪一步
    //程序进程到哪一步的提醒
    public void settooltip(String tip) {
        Tooltiptext.appendText(tip);
    }

    @FXML
    //帮助文档
    void helpfile(ActionEvent actionEvent) throws IOException {
//        String path = System.getProperty("java.class.path");
//        System.out.println(getPath()+"help/help.pdf");
        Desktop.getDesktop().open(new File("help/help.pdf"));

    }

    //技术文档
    @FXML
    void technologyfile(ActionEvent event) throws IOException {
        Desktop.getDesktop().open(new File("help/technical.pdf"));
    }

    //样本程序
    @FXML
    void samplefile(ActionEvent event) throws IOException {
        Desktop.getDesktop().open(new File("help/样例程序.xlsx"));
    }

    //关于作者，直接链接到相应的码云库中去
    /**
     * @title 使用默认浏览器打开
     * @author Xingbz
     */
    private static void browse2(String url) throws Exception {
        Desktop desktop = Desktop.getDesktop();
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = new URI(url);
            desktop.browse(uri);
        }
    }
        //可以做一个弹框，介绍一下，哈哈哈
    @FXML
    void weburl(ActionEvent event) throws Exception {
        browse2("https://gitee.com/laobameishijia/schedule");
    }
    //开始排班
    @FXML
    void beginwork(MouseEvent event) {

        //先把所有的内容输出一下
        System.out.println("排班方式为：" + cutstring(schedule.getSelectedToggle().toString()));
        System.out.println("值班人数为：" + headcount.getValue());
        System.out.println("班内老成员人数为：" + seniorstudent.getValue());
        System.out.println("是否选择自定义：" + cutstring(Custom.getSelectedToggle().toString()));
        System.out.println("自定义的时间段为：" + getcustomtime());
        System.out.println("值班表的标题为：" + Scheduletitle.getText());

        //定义局部变量保存,后面有的没用
        String Scheduletype = cutstring(schedule.getSelectedToggle().toString());//排班方式
        String Dutynumber = (String) headcount.getValue();//值班人数
        String Oldnumber = (String) seniorstudent.getValue(); //老成员数量
        String Customornot = cutstring(Custom.getSelectedToggle().toString());//是否选择自定义
        List time = getcustomtime();//自定义的时间段

        //模态框提示
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(App.primaryStage);
        alert.setTitle("你的配置信息");
        alert.setHeaderText("请确认如下信息：");
        if (Customornot.equals("自定义")) {//这个获取出来的字符串多了一个空格
            alert.setContentText("排班方式为：" + cutstring(schedule.getSelectedToggle().toString()) + "\n" +
                    "值班人数为：" + headcount.getValue() + "\n" +
                    "班内老成员人数为：" + seniorstudent.getValue() + "\n" +
                    "是否选择自定义：" + cutstring(Custom.getSelectedToggle().toString()) + "\n" +
                    "自定义的时间段为：" + getcustomtime() + "\n" +
                    "值班表的标题为：" + Scheduletitle.getText());
        } else {
            alert.setContentText("排班方式为：" + cutstring(schedule.getSelectedToggle().toString()) + "\n" +
                    "值班人数为：" + headcount.getValue() + "\n" +
                    "班内老成员人数为：" + seniorstudent.getValue() + "\n" +
                    "是否选择自定义：" + cutstring(Custom.getSelectedToggle().toString()) + "\n" +
                    "值班表的标题为：" + Scheduletitle.getText());
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            //一开始是向另一个类直接传参的
            /*
            ArrayList<String> args = new ArrayList<>();//向程序传输参数。

            args.add(path);
            args.add(cutstring(schedule.getSelectedToggle().toString()));
            args.add(((String) headcount.getValue()));
            args.add((String) seniorstudent.getValue());
            args.add(cutstring(Custom.getSelectedToggle().toString()));
            args.add(convertListToString(getcustomtime(),","));
            args.add(Scheduletitle.getText());

            Schedule.Main.main(args);
            */

            Schedule.Main.filepath = path;
            Schedule.Main.scheduletype = cutstring(schedule.getSelectedToggle().toString());
            Schedule.Main.dutynumber = Integer.parseInt((String) headcount.getValue());
            Schedule.Main.oldnumber = Integer.parseInt((String) seniorstudent.getValue());
            Schedule.Main.customornot = cutstring(Custom.getSelectedToggle().toString());
            Schedule.Main.timelist = convertStringToList((convertListToString(getcustomtime(), ",")), ",");
            Schedule.Main.title = Scheduletitle.getText();

            //检验文件是否符合标准
            List list1 = Schedule.Main.Inspectiondocuments1(path);
            List list2 = Schedule.Main.Inspectiondocuments2(path);
            int day = (int) list1.get(0);
            int timecount = (int) list1.get(1);
            int peoplecount = (int) list2.get(1);
            //模态框提示,为什么这个alert1，在文字显示上面，不如之前的那个alert显示的多呢？？
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.initOwner(App.primaryStage);
            alert1.setTitle("文件信息如下：");
            alert1.setHeaderText("请确认如下信息：");
            alert1.setContentText("排班人数为：" + peoplecount + "    " +
                    "空闲时间段为：" + timecount + "    " +
                    "时间表天数为：" + day);
            Optional<ButtonType> result1 = alert1.showAndWait();

            if (result1.get() == ButtonType.OK) {
                try {
                    Schedule.Main.getallstudents(Schedule.Main.filepath);
                    System.out.println("读取学生名单....");
                    settooltip("读取学生名单....\n");
                } catch (Exception e) {
                    tip1(e.getMessage());
                }

                try {
                    Schedule.Main.getfreetime(Schedule.Main.filepath);
                    System.out.println("获取学生空闲时间....");
                    settooltip("获取学生空闲时间....\n");
                } catch (Exception e) {
                    tip2(e.getMessage());
                }

                try {
                    if (Schedule.Main.scheduletype.equals("新同学排班"))
                        Schedule.Main.selectrealworkersbynewstudents();//对新同学进行排班，这个我得找到相应的文件才可以
                    else Schedule.Main.selectrealworkers();
                    System.out.println("进行排班....");
                    settooltip("进行排班....\n");
                } catch (Exception e) {
                    tip3(e.getMessage());
                }

                try {
                    Schedule.Main.exportfile(Schedule.Main.filepath);
                    System.out.println("输出保存文件.... 成功进行排班");
                    settooltip("输出保存文件....\n 成功进行排班");
                } catch (Exception e) {
                    tip4(e.getMessage());
                }
            } else {

            }


        } else {
            // ... user chose CANCEL or closed the dialog
        }


    }

}

