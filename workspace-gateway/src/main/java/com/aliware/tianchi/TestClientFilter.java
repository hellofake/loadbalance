package com.aliware.tianchi;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import com.aliware.tianchi.rtt.RTTWindow;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.exchange.support.DefaultFuture;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.dubbo.FutureAdapter;

import static com.aliware.tianchi.Constants.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!threadCountInit) {
            Result result = invoker.invoke(invocation);
            long startTime = System.currentTimeMillis();
            ((SimpleAsyncRpcResult)(result)).getResultFuture().thenApplyAsync(r-> this.recordRTT(r,startTime,invoker));
            return result;
        }
        try {
            URL url = invoker.getUrl();
            int port = url.getPort();
            if (port == 20880) {
                if (longAdderSmall.longValue() <= 0)
                    return new RpcResult();
                longAdderSmall.decrement();
            } else if (port == 20870) {
                if (longAdderMedium.longValue() <= 0)
                    return new RpcResult();
                longAdderMedium.decrement();
            } else {
                if (longAdderLarge.longValue() <= 0)
                    return new RpcResult();
                longAdderLarge.decrement();
            }
            Result result = invoker.invoke(invocation);
            long startTime = System.currentTimeMillis();
            ((SimpleAsyncRpcResult)(result)).getResultFuture().thenApplyAsync(r-> this.recordRTT(r,startTime,invoker));
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (!threadCountInit) {
            return result;
        }
        if (result.getResult() == null) {
            return result;
        }
        URL url = invoker.getUrl();
        int port = url.getPort();
        if (port == 20880) {
            longAdderSmall.increment();
        } else if (port == 20870) {
            longAdderMedium.increment();
        } else {
            longAdderLarge.increment();
        }
        return result;
    }
    private Result recordRTT(Result result,long stTime,Invoker<?> invoker){
        //if (!threadCountInit) {
        //    return result;
        //}
        if (result.getResult() == null) {
            return result;
        }
        long rtt = System.currentTimeMillis() - stTime;
        if(rtt>500) return result;
        URL url = invoker.getUrl();
        int port = url.getPort();
        if (port == 20880) {
            rttWindows[0].addRTT(stTime,rtt);
        } else if (port == 20870) {
            rttWindows[1].addRTT(stTime,rtt);
        } else {
            rttWindows[2].addRTT(stTime,rtt);
        }
        return result;
    }
}
