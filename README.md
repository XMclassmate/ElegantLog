## 功能介绍
支持输出日志到SD卡，打印方法调用堆栈，打印调用线程，json/xml格式化打印，完整的http请求过程打印。

- 使用之前可以在Application初始化：
```
LogUtils.init(BuildConfig.DEBUG, getString(R.string.app_name));
```
- 默认打印
```
LogUtils.e("我是谁，我在哪，我要干啥"); 
//设置tag为‘xxx’
LogUtils.tag("xxx").e("设置了tag是xxx");
```
来看下效果![](https://upload-images.jianshu.io/upload_images/14496175-6d180a16323357fc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 打印堆栈
```
 private void md() {
        //不打印线程信息，打印方法数5
        LogUtils.methodCount(5).e("是谁在召唤我");
        LogUtils.printStack(false).e("不打印方法树");
    }
```
![](https://upload-images.jianshu.io/upload_images/14496175-69a20b3f8dc30504.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 打印线程信息
默认不打印线程信息，需要`LogUtils.printThread(true)`来指定是否打印。
```
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
```
![](https://upload-images.jianshu.io/upload_images/14496175-efec085be742f8be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 打印json
```
//打印json
LogUtils.json(Log.ERROR, gson.toJson(studentList));
```
![](https://upload-images.jianshu.io/upload_images/14496175-2d7c6c4287bac770.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 输出到SD卡
需要申请存储权限
```
private void saveToSd() {
        //保存各类型日志到sd卡
        LogUtils.saveDebugLogToSD("debug to sd");
        LogUtils.saveErrorLogToSD("error to sd");
        LogUtils.saveWarnLogToSD("warn to sd");
        LogUtils.saveThrowableToSD(new RuntimeException());
        LogUtils.saveLogToSD("test", "log to sd");
    }
```
![](https://upload-images.jianshu.io/upload_images/14496175-ed82c988493ca1e4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![](https://upload-images.jianshu.io/upload_images/14496175-f28337783a87ab95.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 打印http请求过程
```
LogUtils.printHttpResponseData(url, params, result, json);
```
使用的okhttp3的异步请求，添加了一个header，"Interface-Name"来标识接口功能，在okhttp的拦截器可以一次打印完整的请求过程，避免了同时发起多个请求时的日志错乱。okhttp日志拦截器的代码网上很多就不贴了，来看下打印效果![](https://upload-images.jianshu.io/upload_images/14496175-5f776e2948b61c9e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
ok的header不支持中文，我目前是传入英文字符串再到拦截器通过枚举值来翻译，有遇到这类问题的朋友欢迎一起交流。
