package com.xm.logdemo.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.xm.logdemo.R;
import com.xm.logdemo.util.LogUtils;
import com.xm.logdemo.util.MyHttpLogIntercepter;
import com.xm.logdemo.util.ThreadUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by XMclassmate on 2018/10/31
 */
public class MainActivity extends Activity implements View.OnClickListener {

    Button btn_default, btn_stack, btn_thread, btn_json, btn_xml, btn_outputToSD, btn_http;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_default = findViewById(R.id.btn_default);
        btn_stack = findViewById(R.id.btn_stack);
        btn_thread = findViewById(R.id.btn_thread);
        btn_json = findViewById(R.id.btn_json);
        btn_xml = findViewById(R.id.btn_xml);
        btn_outputToSD = findViewById(R.id.btn_outputToSD);
        btn_http = findViewById(R.id.btn_http);

        btn_default.setOnClickListener(this);
        btn_stack.setOnClickListener(this);
        btn_thread.setOnClickListener(this);
        btn_json.setOnClickListener(this);
        btn_xml.setOnClickListener(this);
        btn_outputToSD.setOnClickListener(this);
        btn_http.setOnClickListener(this);

        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_default:
                LogUtils.e("我是谁，我在哪，我要干啥");
                //设置tag为‘xxx’
                LogUtils.tag("xxx").e("设置了tag是xxx");
                break;
            case R.id.btn_stack:
                ma();
                break;
            case R.id.btn_thread:
                printThread();
                break;
            case R.id.btn_json:
                Gson gson = new Gson();
                //打印json
                LogUtils.json(Log.ERROR, gson.toJson(studentList));
                break;
            case R.id.btn_xml:
                LogUtils.xml(Log.DEBUG, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\"\n" +
                        "    android:orientation=\"vertical\"\n" +
                        "    >\n" +
                        "    <Button\n" +
                        "        android:id=\"@+id/btn_http\"\n" +
                        "        android:layout_width=\"wrap_content\"\n" +
                        "        android:layout_height=\"wrap_content\"\n" +
                        "        android:text=\"打印http请求日志\"/>\n" +
                        "</LinearLayout>");
                break;
            case R.id.btn_outputToSD:
                saveToSd();
                break;
            case R.id.btn_http:
                logHttp();
                break;
            default:
                break;
        }
    }

    private void ma() {
        mb();
    }

    private void mb() {
        mc();
    }

    private void mc() {
        md();
    }

    private void md() {
        //不打印线程信息，打印方法数5
        LogUtils.methodCount(5).e("是谁在召唤我");
        LogUtils.printStack(false).e("不打印方法树");
    }

    private void printThread() {
        //在子线程打印
        ThreadUtil.start(new Runnable() {
            @Override
            public void run() {
                LogUtils.printThread(true).e("我在那个线程1111");
            }
        });
        LogUtils.printThread(true).e("我在那个线程333");
        ThreadUtil.start(new Runnable() {
            @Override
            public void run() {
                LogUtils.printThread(true).e("我在那个线程222");
            }
        });
    }

    private void saveToSd() {
        //保存各类型日志到sd卡
        LogUtils.saveDebugLogToSD("debug to sd");
        LogUtils.saveErrorLogToSD("error to sd");
        LogUtils.saveWarnLogToSD("warn to sd");
        LogUtils.saveThrowableToSD(new RuntimeException());
        LogUtils.saveLogToSD("test", "log to sd");
    }

    private void logHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new MyHttpLogIntercepter(false))
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .header("Interface-Name", "go baidu see see")
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


    School school;
    Student student;
    List<School> schoolList;
    List<Student> studentList;

    private void initData() {
        student = new Student("http://sdfajfdsaf.jjjjkj.dfaafds/jdafa.png", "张三", 22, "男", new Date(System.currentTimeMillis()), 60.15f);
        studentList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            studentList.add(student);
        }
        school = new School("2", "社会大学", "湖南", "长沙", studentList);
        schoolList = new ArrayList<>();
        schoolList.add(school);
        schoolList.add(school);
    }

    /**
     * Created by XMclassmate on 2018/4/11.
     */

    public static class Student {

        private String id;
        private String name;
        private int age;
        private String sex;
        private Date birthday;
        private float weight;

        public Student() {
        }

        public Student(String id, String name, int age, String sex, Date birthday, float weight) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.birthday = birthday;
            this.weight = weight;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }

    /**
     * Created by XMclassmate on 2018/4/11.
     */

    public static class School {

        private String id;
        private String name;
        private String address;
        private String city;
        private List<Student> studentList;

        public School() {
        }

        public School(String id, String name, String address, String city, List<Student> studentList) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.city = city;
            this.studentList = studentList;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public List<Student> getStudentList() {
            return studentList;
        }

        public void setStudentList(List<Student> studentList) {
            this.studentList = studentList;
        }
    }
}
