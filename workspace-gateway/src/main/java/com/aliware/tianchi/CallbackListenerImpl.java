package com.aliware.tianchi;

import java.util.Timer;
import java.util.TimerTask;

import com.aliware.tianchi.rtt.RTTWindow;
import org.apache.dubbo.rpc.listener.CallbackListener;

import static com.aliware.tianchi.Constants.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {
    private Timer timer = new Timer();
    public CallbackListenerImpl() { }


    @Override
    public void receiveServerMsg(String msg) {
        if (threadCountInit)
            return;
        synchronized (this) {
            if (threadCountInit) return;
            String[] threadSum = msg.split(":");
            String[] threadAndCpu = threadSum[1].split(",");
            switch (threadSum[0]) {
                case "small":
                    smallProducerThreadSum = Integer.parseInt(threadAndCpu[0]);
                    smallCPU = Integer.parseInt(threadAndCpu[1]);
                    break;
                case "medium":
                    mediumProducerThreadSum = Integer.parseInt(threadAndCpu[0]);
                    mediumCPU = Integer.parseInt(threadAndCpu[1]);
                    break;
                case "large":
                    largeProducerThreadSum = Integer.parseInt(threadAndCpu[0]);
                    largeCPU = Integer.parseInt(threadAndCpu[1]);
                    break;
            }
            if (smallProducerThreadSum != 0 && mediumProducerThreadSum != 0 && largeProducerThreadSum != 0) {
                //线程参数初始化(采用服务端传值)
                activeThreadCount.put("small", smallProducerThreadSum);
                activeThreadCount.put("medium", mediumProducerThreadSum);
                activeThreadCount.put("large", largeProducerThreadSum);

                longAdderLarge.add(largeProducerThreadSum);
                longAdderMedium.add(mediumProducerThreadSum);
                longAdderSmall.add(smallProducerThreadSum);

                //线程参数初始化(直接在gateway统计)
                threadCountInit = true;
                startTime = System.currentTimeMillis();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (RTTWindow rttWindow:rttWindows) {
                            rttWindow.refresh();
                        }
                    }
                }, 10000, 15);
            }
        }

    }
}