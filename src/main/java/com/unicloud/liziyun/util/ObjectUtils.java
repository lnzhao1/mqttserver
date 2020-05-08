package com.unicloud.liziyun.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liujiawei on 2019-03-22.
 */
@Slf4j
public class ObjectUtils {


    private ObjectUtils() {
    }

    /**
     * 判断类中每个属性是否都为空
     *
     * @param o
     * @return
     */
    public static boolean allFieldIsNULL(Object o){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (!org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return false;
                    }
                } else {
                    if (null != object) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断对象属性为空异常" , e);
        }
        return true;
    }

    /**
     *      主键拼接
     * @param ids
     * @return
     */
    public static List<Long> getIds(String ids){
        List<Long> longList = null;
        if(StringUtils.isNotBlank(ids)){
            String[] idsArray = ids.split(",");
            if(idsArray!=null&&idsArray.length>0) {
                Long[] keys = new Long[ids.split(",").length];
                for (int i = 0; i < idsArray.length; i++) {
                    keys[i] = Long.valueOf(idsArray[i]);
                }
                longList = new ArrayList<>(Arrays.asList(keys));
            }
        }
        return longList;
    }

    /**
     *     0点时间
     * @return
     */
    public static Date getTodayZero(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     *     23:59:59点时间
     * @return
     */
    public static Date getTodayMidNight(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getMidNight(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getZeroDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getDate(int hour,int min,int sec){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if(hour>0){
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        }else{
            calendar.set(Calendar.HOUR_OF_DAY, 0);
        }

        if(min>0){
            calendar.set(Calendar.MINUTE, min);
        }else{
            calendar.set(Calendar.MINUTE, 0);
        }

        if(sec>0){
            calendar.set(Calendar.SECOND, sec);
        }else{
            calendar.set(Calendar.SECOND, 0);
        }

        return calendar.getTime();
    }

    /**
     *
     *       时区日期转换
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date dealDateFormat(String dateStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  //yyyy-MM-dd'T'HH:mm:ss.SSSZ
        return df.parse(dateStr);
    }

    /**
     *         判断考勤时间范围
     * @param slot
     * @param checkDate
     * @param regex
     * @return
     */
    public static  boolean isTimeRange(String slot,Date checkDate,String regex){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            if(StringUtils.isNotBlank(slot)) {
                Calendar checkTime = Calendar.getInstance();
                checkTime.setTime(checkDate);
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(new Date());
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(new Date());
                String[] times = slot.split(regex);
                if (times != null && times.length == 2) {
                    String[] time0 = times[0].split(":");
                    String[] time1 = times[1].split(":");
                    Date begin = df.parse(times[0]);
                    if (time0 != null && time0.length == 2&&time1 != null && time1.length == 2){
                        beginTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time0[0]));
                        beginTime.set(Calendar.MINUTE, 0);
                        beginTime.set(Calendar.SECOND, 0);
                        endTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time1[0]));
                        endTime.set(Calendar.MINUTE, 0);
                        endTime.set(Calendar.SECOND, 0);
                        if (checkTime.before(endTime) && checkTime.after(beginTime)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
