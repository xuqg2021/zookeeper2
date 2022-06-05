package com.xuqg;

import org.apache.zookeeper.*;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    //    解决异步 的同步问题
    static CountDownLatch latch = new CountDownLatch(1);
    public static void main( String[] args ) throws IOException, InterruptedException, KeeperException {
        System.out.println( "Hello World!" );
//            传入的watch是session界别的，可以观察客户端的连接情况，跟path,node没有关系
//        watch的注册只发生在读类型调用，如：get,exists
            ZooKeeper zk = new ZooKeeper("192.168.47.130:281,192.168.47.131:2181," +
                    "192.168.47.132:2181,192.168.47.133:2181", 3000,
                    new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            Event.EventType type = watchedEvent.getType();
                            Event.KeeperState state = watchedEvent.getState();
                            String path = watchedEvent.getPath();
                            System.out.println("type: "+type);
                            System.out.println("state: "+state);
                            System.out.println("path: "+path);
                            switch (state) {
                                case Unknown:
                                    break;
                                case Disconnected:
                                    break;
                                case NoSyncConnected:
                                    break;
                                case SyncConnected:
                                    System.out.println("zookeeper connected");
//                    当链接成功，即任务完成,countdown -1
                                    latch.countDown();
                                    break;
                                case AuthFailed:
                                    break;
                                case ConnectedReadOnly:
                                    break;
                                case SaslAuthenticated:
                                    break;
                                case Expired:
                                    break;
                                case Closed:
                                    break;
                            }
                            switch (type) {
                                case None:
                                    break;
                                case NodeCreated:
                                    break;
                                case NodeDeleted:
                                    break;
                                case NodeDataChanged:
                                    break;
                                case NodeChildrenChanged:
                                    break;
                                case DataWatchRemoved:
                                    break;
                                case ChildWatchRemoved:
                                    break;
                            }
                        }
                    });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ZooKeeper.States state = zk.getState();
            switch (state) {
                case CONNECTING:
                    System.out.println("connecting ... ");
                    break;
                case ASSOCIATING:
                    break;
                case CONNECTED:
                    System.out.println("connected ... ");
                    break;
                case CONNECTEDREADONLY:
                    break;
                case CLOSED:
                    break;
                case AUTH_FAILED:
                    break;
                case NOT_CONNECTED:
                    break;
            }

            String pathname = zk.create("/test", "old data aaa".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("pathname: "+pathname);

            Stat stat = new Stat();

        byte[] data = zk.getData("/test", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        }, stat);
        System.out.println("olddata: "+new String(data));
        Stat stat1 = zk.setData("/test", "new data bbb".getBytes(), 0);
        System.out.println("stat1: "+stat1);
        zk.getData("/test", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("getdata: "+watchedEvent.toString());
            }
        },stat);
        System.out.println("stat:" + stat);
        Stat stat2 = zk.setData("/test", "new data ccc".getBytes(), stat1.getVersion());
        System.out.println(stat2);
//        true 表示使用 new zk 时的 watch
        zk.getData("/test", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("----------------async start--------");
                System.out.println(s);
                System.out.println(o.toString());
                System.out.println(new String(bytes));
                System.out.println(stat);
            }
        },"ctx");
//Thread.sleep(999999);
    }
}
