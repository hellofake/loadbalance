/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.aliware.tianchi.rtt;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author yuda.gyd
 * @version RTTBucket: RTTBucket.java, v 0.1 2019年07月19日 14:50 yuda.gyd Exp $
 */
public class RTTBucket {
    private LongAdder totalRTTTime = new LongAdder();
    private LongAdder totalReq = new LongAdder();
    public void addRTT(long rtt){
        totalRTTTime.add(rtt);
        totalReq.add(1);
    }
    public double getAvgRTT(){
        return totalRTTTime.longValue()/totalReq.longValue();
    }
    public long getTotalReq(){
        return totalReq.longValue();
    }

}