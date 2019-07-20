/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.aliware.tianchi.rtt;

import static com.aliware.tianchi.Constants.*;
/**
 * @author yuda.gyd
 * @version RTTWindow: RTTWindow.java, v 0.1 2019年07月19日 14:55 yuda.gyd Exp $
 */
public class RTTWindow {
    private RTTBucket[] rttBuckets  = new RTTBucket[1500];
    private int         startBucket = 0;

    private int         interval    = 100;
    private int         windowSize  = 3;
    private double      curAvgRTT   = 1;
    private String name = "";
    {
        for (int i = 0; i < 1500; i++) {
            rttBuckets[i] = new RTTBucket();
        }
    }
    public RTTWindow(int i){
        if(i==0)name = "small";
        if(i==1)name = "medium";
        if(i==2)name = "large";
    }
    public void addRTT(long reqStartTime, long rtt) {
        int index = (int) (reqStartTime - startTime) / interval;
        //System.out.println("startBucket"+startBucket+",index:"+index+",rtt:"+rtt+"startTime:"+startTime+",reqTime:"+reqStartTime+",curTime:"+System.currentTimeMillis());
        //System.out.println("startBucket"+startBucket+",index:"+index+",rtt:"+rtt);
        if (index < startBucket) { return; }
        rttBuckets[index].addRTT(rtt);
    }

    public void refresh() {
        long curTime = System.currentTimeMillis();
        startBucket = (int) (curTime - startTime) / interval - windowSize;
        //System.out.println("name:"+name+",startBucket:"+startBucket+",curTime:"+curTime);
        double sum = 0;

        for (int i = startBucket; i <= startBucket + windowSize - 1; i++) {
            sum += rttBuckets[i].getAvgRTT();
        }
        curAvgRTT = sum / windowSize;
        //for (int i = startBucket; i <= startBucket + windowSize - 1; i++) {
        //    System.out.print(rttBuckets[i].getTotalReq()+",");
        //}
        //System.out.println("avg:"+curAvgRTT);
    }

    public double getCurAvgRTT() {
        return curAvgRTT;
    }
}