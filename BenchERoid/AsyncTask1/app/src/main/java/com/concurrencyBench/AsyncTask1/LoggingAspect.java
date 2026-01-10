package com.concurrencyBench.AsyncTask1;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.SourceLocation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
public class LoggingAspect {

    private static final String TAG = "LoggingAspect";
        @Before("execution(* android.app.Activity.onCreate(..)) || " +
          "execution(* android.app.Activity.onStart(..)) || " +
          "execution(* android.app.Activity.onResume(..)) || " +
          "execution(* android.app.Activity.onPause(..)) || " +
         "execution(* android.app.Activity.onStop(..)) || " +
         "execution(* android.app.Activity.onDestroy(..))")

    public void logBeforeLifecycleMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
       // Log.i(TAG,  methodName +"() "+" Executing ");
            Object target = joinPoint.getTarget();
            String className = target.getClass().getName();
            Log.i(TAG, "LoggingAspectcallBack," + className + "," + methodName);
    }

    @AfterReturning("execution(* android.app.Activity.onCreate(..)) || " +
            "execution(* android.app.Activity.onStart(..)) || " +
            "execution(* android.app.Activity.onResume(..)) || " +
            "execution(* android.app.Activity.onPause(..)) || " +
            "execution(* android.app.Activity.onStop(..)) || " +
            "execution(* android.app.Activity.onDestroy(..))")
    public void logAfterLifecycleMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
      //  Log.i(TAG, methodName +"()" +" exit");
        Object target = joinPoint.getTarget();
        String className = target.getClass().getName();
        Log.i(TAG, "LoggingAspectleaving," + className + "," + methodName);
    }

    @Before("get(* *)")
    public void beforeFieldGet(JoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        Field field = getField(joinPoint);
        SourceLocation location = joinPoint.getSourceLocation();

        //static
        if (field == null){
            String signature = joinPoint.getSignature().toString();
            String type  = signature.split(" ")[0];
            String temp = signature.split(" ")[1];
            int lastIndexOf = temp.lastIndexOf(".");
            String className = temp.substring(0,lastIndexOf);
            String fieldName  = temp.substring(lastIndexOf+1);
            Log.i(TAG, "LoggingAspectget," + className +","+ fieldName +","+type+","+joinPoint.getSourceLocation());

        }
        if (field != null) {
            String fieldName = field.getName();
            String className = target.getClass().getName();
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Log.i(TAG, "LoggingAspectget," + className +","+ fieldName +","+ f.getType()+","+joinPoint.getSourceLocation());
            Signature signature = joinPoint.getSignature();
            int line = joinPoint.getSourceLocation().getLine();
            String sourceCodeLine = getSourceCodeLine(signature.getDeclaringTypeName(), line);
            Pattern pattern_if = Pattern.compile("\\bif\\b\\s*\\([^)]*\\)");
            Matcher matcher_if = pattern_if.matcher(sourceCodeLine);
            if (matcher_if.find()) {
                Log.i(TAG,"LoggingAspectbranch,"+ className);
            }
            Pattern pattern_switch = Pattern.compile("\\bswitch\\b\\s*\\([^)]*\\)");
            Matcher matcher_switch = pattern_switch.matcher(sourceCodeLine);
            if (matcher_switch.find()) {
                Log.i(TAG,"LoggingAspectbranch,"+ className);
            }
        }
    }

    @After("set(* *)")
    public void afterFieldSet(JoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        Field field = getField(joinPoint);
        //static
        if (field == null){
            String signature = joinPoint.getSignature().toString();
            String type  = signature.split(" ")[0];
            String temp = signature.split(" ")[1];
            int lastIndexOf = temp.lastIndexOf(".");
            String className = temp.substring(0,lastIndexOf);
            String fieldName  = temp.substring(lastIndexOf+1);
            Log.i(TAG, "LoggingAspectput," + className +","+ fieldName +","+type+","+joinPoint.getSourceLocation());
        }
        if (field != null) {
            String fieldName = field.getName();
            String className = target.getClass().getName();
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Log.i(TAG, "LoggingAspectput," + className +","+ fieldName +","+ f.getType()+","+joinPoint.getSourceLocation());
        }
    }

    @Pointcut("execution(synchronized * *.*(..))")
    public void syncMethod() {
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Around("syncMethod()")
    public void aroundSyncMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 在 synchronized 块执行前的逻辑
        Log.i(TAG, "LoggingAspectmonitor-enter," + joinPoint.getTarget()+","+ joinPoint.getTarget().getClass());
        // 执行 synchronized 块中的方法
        joinPoint.proceed();
        // 在 synchronized 块执行后的逻辑
        Log.i(TAG, "LoggingAspectmonitor-exit," + joinPoint.getTarget()+","+ joinPoint.getTarget().getClass());
    }

    @Before("call(* java.util.concurrent.locks.Lock+.lock(..))")
    public void beforeLock(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectmonitor-enter," + joinPoint.getTarget()+","+ joinPoint.getTarget().getClass());

    }

    @After("call(* java.util.concurrent.locks.Lock+.unlock(..))")
    public void afterUnLock(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectmonitor-exit," + joinPoint.getTarget() +","+ joinPoint.getTarget().getClass());

    }

    @Before("call(* android.os.Handler.post(Runnable))")
    public void onHandlerPostCalled(JoinPoint joinPoint) throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        long bootTime = SystemClock.uptimeMillis();
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Runnable) {
            Runnable runnable = (Runnable) args[0];
            Log.i(TAG, "LoggingAspectpost time: " + bootTime + "  runnable:  " + runnable );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @After("call(* android.os.Handler.post(Runnable))")
    public void Runnable_in_messageQueue(JoinPoint joinPoint) throws NoSuchMethodException, NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Runnable) {
            Runnable runnable = (Runnable) args[0];
            // 对runnable进行操作
            Handler handler = (Handler) joinPoint.getTarget();
            // 获取MessageQueue对象
            MessageQueue messageQueue = handler.getLooper().getQueue();
            // 获取MessageQueue中的mMessages成员变量
            Field mMessagesField = null;
            try {
                // mMessagesField = MessageQueue.class.getDeclaredField("mMessages");
                mMessagesField = MessageQueue.class.getDeclaredField("mMessages");
                mMessagesField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            //     Method nextMethod = MessageQueue.class.getDeclaredMethod("next");
            Field nextField = Message.class.getDeclaredField("next");
            //Method nextMethod = mMessagesField.getClass().getDeclaredMethod("next");
            nextField.setAccessible(true);
            // 获取队列中第一个Message的时间戳
            if (mMessagesField != null) {
                try {
                    Message message = (Message) mMessagesField.get(messageQueue);
                    while (message != null) {
                        if (message.getCallback() == runnable) {
                            long enqueueTime = message.getWhen();
                            Log.i(TAG, "LoggingAspectenque," + joinPoint.getThis()+"," +runnable + "," + enqueueTime);
                            break;
                        }
                        message = (Message) nextField.get(message);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Before("execution(void Runnable.run())")
    public void beforeRunExecution(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        String className = target.getClass().getName();
        Log.i(TAG, "LoggingAspectcallBack," + className + "," + "run");
    }

    @After("execution(void Runnable.run())")
    public void afterRunExecution(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        String className = target.getClass().getName();
        Log.i(TAG, "LoggingAspectleaving," + className + "," + "run");
    }

    //拦截 AsyncTask 的 doInBackground() 方法
    @Before("execution(* android.os.AsyncTask.doInBackground(..))")
    public void interceptDoInBackground(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectoInBackground()" + " " + joinPoint.getTarget());
        // 拦截逻辑
    }

    // 拦截 AsyncTask 的 onProgressUpdate() 方法
    @Before("execution(* android.os.AsyncTask.onProgressUpdate(..))")
    public void interceptOnProgressUpdate(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectonProgressUpdate()"+ " " + joinPoint.getTarget());
        // 拦截逻辑
    }


    // 拦截 AsyncTask 的 onPostExecute() 方法
    @Before("execution(* android.os.AsyncTask.onPostExecute(..))")
    public void interceptOnPostExecute(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectcallBack," + joinPoint.getTarget() + "onPostExecute");
        // 拦截逻辑
    }
    @After("execution(* android.os.AsyncTask.onPostExecute(..))")
    public void afterOnPostExecute(JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectleaving," + joinPoint.getTarget() +","+ "onPostExecute");
        // 拦截逻辑
    }

    @Before("call(* java.lang.Object.wait(..))")
    public void beforeWait(JoinPoint joinPoint) throws Throwable {
        // 在 wait 方法调用之前执行的逻辑
        Log.i(TAG, "LoggingAspectwait," + joinPoint.getTarget() +","+ joinPoint.getTarget().getClass());
    }

    @Before("call(* java.lang.Object.notify(..))")
    public void beforeNotify(JoinPoint joinPoint) throws Throwable {
        // 在 notify 方法调用之前执行的逻辑
        Log.i(TAG, "LoggingAspectnotify,"  + joinPoint.getTarget() +","+ joinPoint.getTarget().getClass());
    }
    //onClick
    @Before("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    public void beforeOnClick(View view, JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectcallBack," + joinPoint.getTarget() +","+ "onClick");
    }
    @After("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    public void afterOnClick(View view, JoinPoint joinPoint) {
        Log.i(TAG, "LoggingAspectleaving," + joinPoint.getTarget() +","+ "onClick");
    }

    private Field getField(JoinPoint joinPoint) {
        try {
            Object target = joinPoint.getTarget();
            if (target != null) {
                String fieldName = joinPoint.getSignature().getName().replace("get", "").replace("set", "");
                Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSourceCodeLine(String className, int lineNumber) {
      try {
        // 构造源代码文件路径
        String localfilePath = className.replace(".", "/") + ".java";
        String sysPath = "/data/local/tmp/NewFolder/";
        String filePath = sysPath + localfilePath;
        // 读取源代码文件
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        // 逐行读取源代码，直到找到指定行号的行
        int currentLine = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            if (currentLine == lineNumber) {
                reader.close();
                return line.trim();
            }
            currentLine++;
        }
        reader.close();
    } catch (IOException e) {
      }
    return ""; // 如果找不到指定行号的行，返回空字符串
    }
}

