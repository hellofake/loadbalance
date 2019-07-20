package com.aliware.tianchi.strategy;

import javax.sound.midi.Soundbank;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import static com.aliware.tianchi.Constants.*;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:46:35
 */
public class RandomWithWeightStategy extends AbstractStrategy {
    static {
        strategy = new RandomWithWeightStategy();
    }

    public static UserLoadBalanceStrategy getInstance() {
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {
        int smallActiveCount = (int) longAdderSmall.longValue();
        int mediumActiveCount = (int) longAdderMedium.longValue();
        int largeActiveCount = (int) longAdderLarge.longValue();


        double small = (smallActiveCount == 0 ? 1 : smallActiveCount)*smallCPU/rttWindows[0].getCurAvgRTT();
        double medium = (mediumActiveCount == 0 ? 1 : mediumActiveCount)*mediumCPU/rttWindows[1].getCurAvgRTT();
        double large = (largeActiveCount == 0 ? 1 : largeActiveCount)*largeCPU/rttWindows[2].getCurAvgRTT();
        //double small = (smallActiveCount == 0 ? 1 : smallActiveCount)*smallCPU;
        //double medium = (mediumActiveCount == 0 ? 1 : mediumActiveCount)*mediumCPU;
        //double large = (largeActiveCount == 0 ? 1 : largeActiveCount)*largeCPU;
        //System.out.println("small:"+small+"smallActiveCount:"+smallActiveCount+",smallCPU:"+smallCPU+"rtt:"+rttWindows[0].getCurAvgRTT());
        //System.out.println("medium"+medium+"mediumActiveCount:"+mediumActiveCount+",mediumCPU:"+mediumCPU+"rtt:"+rttWindows[1].getCurAvgRTT());
        //System.out.println("large"+large+"largeActiveCount:"+largeActiveCount+",largeCPU:"+largeCPU+"rtt:"+rttWindows[2].getCurAvgRTT());
        int randNumber = rand.nextInt((int)(small + medium + large));

        if (randNumber < small) {
            return 0;
        } else if (randNumber >= small && randNumber < small + medium) {
            return 1;
        } else {
            return 2;
        }
    }

}
