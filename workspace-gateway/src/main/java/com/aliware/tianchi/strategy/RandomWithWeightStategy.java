package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

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
        int smallActiveCount = (int) Constants.longAdderSmall.longValue();
        int mediumActiveCount = (int) Constants.longAdderMedium.longValue();
        int largeActiveCount = (int) Constants.longAdderLarge.longValue();


        double small = (smallActiveCount == 0 ? 1 : smallActiveCount)*1;
        double medium = (mediumActiveCount == 0 ? 1 : mediumActiveCount)*2;
        double large = (largeActiveCount == 0 ? 1 : largeActiveCount)*2.3;

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
