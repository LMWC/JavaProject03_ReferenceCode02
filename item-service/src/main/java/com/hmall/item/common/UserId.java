package com.hmall.item.common;

public class UserId {
    static ThreadLocal<Integer> threadLocal=new ThreadLocal<>();
    public static void se(Integer userid){
        threadLocal.set(userid);
    }
    public static Integer ge(){
        Integer userid = threadLocal.get();
        threadLocal.remove();
        return userid;
    }

}
