// Copyright (c) 2019, Mine2Gether.com
//
// Please see the included LICENSE file for more information.

package work.wrkz.androidminer;

import java.util.ArrayList;

public class Config {

    public static Config settings = new Config();

    private ArrayList<PoolItem> mPools = new ArrayList<PoolItem>();
    private ArrayList<AlgoItem> mAlgos = new ArrayList<AlgoItem>();

    public static int defaultPoolIndex = 1;
    public static String defaultWallet = "";
    public static String defaultPassword = "";

    public Config() {

        mAlgos.add(new AlgoItem("cn", ""));
        mAlgos.add(new AlgoItem("cn/0", ""));
        mAlgos.add(new AlgoItem("cn/1", ""));
        mAlgos.add(new AlgoItem("cn/2", ""));
        mAlgos.add(new AlgoItem("cn/xtl", ""));
        mAlgos.add(new AlgoItem("cn/msr", ""));
        mAlgos.add(new AlgoItem("cn/xao", ""));
        mAlgos.add(new AlgoItem("cn/rto", ""));
        mAlgos.add(new AlgoItem("cn/half", ""));
        mAlgos.add(new AlgoItem("cn/gpu", ""));
        mAlgos.add(new AlgoItem("cn/wow", ""));
        mAlgos.add(new AlgoItem("cn/r", ""));
        mAlgos.add(new AlgoItem("cn/rwz", ""));
        mAlgos.add(new AlgoItem("cn/zls", ""));
        mAlgos.add(new AlgoItem("cn/double", ""));
        mAlgos.add(new AlgoItem("cn-lite", ""));
        mAlgos.add(new AlgoItem("cn-lite/0", ""));
        mAlgos.add(new AlgoItem("cn-lite/1", ""));
        mAlgos.add(new AlgoItem("cn-lite/ipbc", ""));
        mAlgos.add(new AlgoItem("cn-heavy", ""));
        mAlgos.add(new AlgoItem("cn-heavy/xhv", ""));
        mAlgos.add(new AlgoItem("cn-heavy/tube", ""));
        mAlgos.add(new AlgoItem("cn-pico/trtl", ""));
        mAlgos.add(new AlgoItem("chukwa", ""));
        mAlgos.add(new AlgoItem("chukwa/wrkz", ""));

        // Loki Network (LOKI) + Turtlecoin (TRTL) Merged Mining|loki.pool.wrkz.com:2221|cn-pico/trtl
        mPools.add(new PoolItem(
                        "loki",
                        "Loki Network (LOKI) + Turtlecoin (TRTL) Merged Mining",
                        "loki.pool.wrkz.com:2221",
                        "cn-pico/trtl",
                        "https://loki.wrkz.com/api",
                        "https://loki.wrkz.com",
                        "https://loki.wrkz.com/#my_stats",
                        "https://loki.wrkz.com/#getting_started",
                        "https://trtl.wrkz.com/api"
                )
        );

        // XtendCash (XTNC) + Turtlecoin (TRTL) Merged Mining|xtnc.pool.wrkz.com:2222|cn-pico/trtl
        mPools.add(new PoolItem(
                        "xtnc",
                        "XtendCash (XTNC) + Turtlecoin (TRTL) Merged Mining",
                        "xtnc.pool.wrkz.com:2222",
                        "cn-pico/trtl",
                        "https://xtnc.wrkz.com/api",
                        "https://xtnc.wrkz.com",
                        "https://xtnc.wrkz.com/#my_stats",
                        "https://xtnc.wrkz.com/#getting_started",
                        "https://trtl.wrkz.com/api"
                )
        );

        // Turtlecoin (TRTL)|trtl.pool.wrkz.com:2225|cn-pico/trtl
        mPools.add(new PoolItem(
                        "trtl",
                        "Turtlecoin (TRTL)",
                        "trtl.pool.wrkz.com:2225",
                        "cn-pico/trtl",
                        "https://trtl.wrkz.com/api",
                        "https://trtl.wrkz.com",
                        "https://trtl.wrkz.com/#my_stats",
                        "https://trtl.wrkz.com/#getting_started",
                        "https://trtl.wrkz.com/api"
                )
        );

        // Alloy (XAO)|xao.pool.wrkz.com:1117|cn/xao
        mPools.add(new PoolItem(
                        "xao",
                        "Alloy (XAO)",
                        "xao.pool.wrkz.com:1117",
                        "cn/xao",
                        "https://xao.wrkz.com/api",
                        "https://xao.wrkz.com",
                        "https://xao.wrkz.com/#my_stats",
                        "https://xao.wrkz.com/#getting_started",
                        ""
                )
        );

        // Citadel (CTL)|ctl.pool.wrkz.com:1114|cn/1
        mPools.add(new PoolItem(
                        "ctl",
                        "Citadel (CTL)",
                        "ctl.pool.wrkz.com:1114",
                        "cn/1",
                        "https://ctl.wrkz.com/api",
                        "https://ctl.wrkz.com",
                        "https://ctl.wrkz.com/#my_stats",
                        "https://ctl.wrkz.com/#getting_started",
                        ""
                )
        );

        // WrkzCoin TestNet (TRTL)|trtl.pool.wrkz.com:2225|cn-pico/trtl
        mPools.add(new PoolItem(
                        "wrkz",
                        "WrkzCoin TestNet (WRKZ)",
                        "testnet.wrkz.work:5555",
                        "chukwa/wrkz",
                        "http://testnet.wrkz.work:8117",
                        "http://139.162.29.140/testwrkz/",
                        "http://139.162.29.140/testwrkz/#my_stats",
                        "http://139.162.29.140/testwrkz//#getting_started",
                        ""
                )
        );
        /*
        mPools.add(new PoolItem(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                )
        );
        */

    }

    public PoolItem[] getPools() {
        return this.mPools.toArray(new PoolItem[mPools.size()]);
    }

    public AlgoItem[] getAlgos() {
        return this.mAlgos.toArray(new AlgoItem[mAlgos.size()]);
    }

}
