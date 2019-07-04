/*
 *  Monero Miner App (c) 2018 Uwe Post
 *  based on the XMRig Monero Miner https://github.com/xmrig/xmrig
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 * /
 */
// Copyright (c) 2019, Mine2Gether.com
//
// Please see the included LICENSE file for more information.

package work.wrkz.androidminer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.os.PowerManager.*;

public class MiningService extends Service {

    private static final String LOG_TAG = "MiningSvc";
    private Process process;
    private String configTemplate;
    private String privatePath;
    private OutputReaderThread outputHandler;
    private ProcessMonitor procMon;
    private int accepted;
    private String speed = "0";

    private String lastAssetPath;

    private String lastOutput = "";

    @Override
    public void onCreate() {
        super.onCreate();

        configTemplate = Tools.loadConfigTemplate(this);
        privatePath = getFilesDir().getAbsolutePath();
        Tools.deleteDirectoryContents(new File(privatePath));
    }

    private MiningServiceStateListener listener = null;

    public interface MiningServiceStateListener {
        public void onStateChange(Boolean state);
    }

    public void setMiningServiceStateListener(MiningServiceStateListener listener) {
        if (this.listener != null) this.listener = null;
        this.listener = listener;
    }

    Boolean mMiningServiceState = false;

    private void raiseMiningServiceStateChange(Boolean state) {
        mMiningServiceState = state;
        if (listener != null) listener.onStateChange(state);
    }

    public Boolean getMiningServiceState() {
        return mMiningServiceState;
    }

    private void copyMinerFiles() {

        String abi = Build.CPU_ABI.toLowerCase().trim();
        String assetPath = "";

        if (abi.equals("arm64-v8a")) {
            assetPath = "arm64";
        } else if (abi.equals("armeabi-v7a")) {
            assetPath = "arm";
        } else if (abi.equals("x86")) {
            assetPath = "x86";
        } else if (abi.equals("x86_64")) {
            assetPath = "x86_64";
        } else {
            Log.i(LOG_TAG, "NO ASSET PATH");
        }

        assetPath += PreferenceHelper.getName("assetExtension");

        Log.i(LOG_TAG, "ASSET PATH: " + assetPath);
        Log.i(LOG_TAG, "LAST ASSET PATH: " + lastAssetPath);
        Log.i(LOG_TAG, "ABI: " + abi);


        if (assetPath.equals(lastAssetPath) == false) {
            Tools.deleteDirectoryContents(new File(privatePath));
            Tools.copyDirectoryContents(this, assetPath, privatePath);
            Tools.logDirectoryFiles(new File(privatePath));
            lastAssetPath = assetPath;
        }
    }

    public class MiningServiceBinder extends Binder {
        public MiningService getService() {
            return MiningService.this;
        }
    }

    public static class MiningConfig {
        String username, pool, pass, algo, assetExtension;
        int threads, maxCpu, av;
    }

    public MiningConfig newConfig(String username, String pool, String pass, int threads, int maxCpu, int av, String algo, String assetExtension) {
        MiningConfig config = new MiningConfig();

        config.username = username;
        config.pool = pool;
        config.threads = threads;
        config.maxCpu = maxCpu;
        config.av = av;
        config.pass = pass;
        config.algo = algo;
        config.assetExtension = assetExtension;
        return config;
    }

    @Override
    public void onDestroy() {
        stopMining();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MiningServiceBinder();
    }

    public void stopMining() {
        if (outputHandler != null) {
            outputHandler.interrupt();
            outputHandler = null;
        }

        if (process != null) {
            process.destroy();
            process = null;
        }
    }

    public static String getIpByHost(String hostName) {
        try {
            Log.i(LOG_TAG, hostName);
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            Log.i(LOG_TAG, e.toString());
            return hostName;
        }
    }

    public void startMining(MiningConfig config) {
        stopMining();
        new startMiningAsync().execute(config);
    }

    class startMiningAsync extends AsyncTask<MiningConfig, Void, String> {

        protected String getPoolHost(String pool) {

            String[] hostParts = pool.split(":");

            if (hostParts.length == 2) {
                return getIpByHost(hostParts[0]) + ":" + hostParts[1];
            } else if (hostParts.length == 1) {
                return getIpByHost(hostParts[0]);
            } else {
                return pool;
            }
        }

        private Exception exception;
        private MiningConfig config;

        protected String doInBackground(MiningConfig... config) {

            try {
                this.config = config[0];
                this.config.pool = getPoolHost(this.config.pool);
                return "success";
            } catch (Exception e) {
                this.exception = e;
                return null;
            } finally {

            }
        }

        protected void onPostExecute(String result) {
            copyMinerFiles();
            startMiningProcess(this.config);
        }
    }

    public void startMiningProcess(MiningConfig config) {

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl;
        wl = pm.newWakeLock(PARTIAL_WAKE_LOCK, "app:sleeplock");
        wl.acquire();

        Log.i(LOG_TAG, "starting...");

        if (process != null) {
            process.destroy();
            wl.release(); //Wakelock
        }

        try {
            Tools.writeConfig(configTemplate, config.algo, config.pool, config.username, config.pass, config.threads, config.maxCpu, config.av, privatePath);

            String[] args = {"./xmrig-notls"};

            ProcessBuilder pb = new ProcessBuilder(args);

            pb.directory(getApplicationContext().getFilesDir());

            pb.environment().put("LD_LIBRARY_PATH", privatePath);

            pb.redirectErrorStream();

            accepted = 0;
            speed = "0";
            lastOutput = "";

            process = pb.start();

            outputHandler = new MiningService.OutputReaderThread(process.getInputStream());
            outputHandler.start();

            if (procMon != null) {
                procMon.interrupt();
                procMon = null;
            }
            procMon = new ProcessMonitor(process);
            procMon.start();

        } catch (Exception e) {
            Log.e(LOG_TAG, "exception:", e);
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            process = null;
        }
    }

    public String getSpeed() {
        return speed;
    }

    public int getAccepted() {
        return accepted;
    }

    public String getOutput() {
        if (outputHandler != null && outputHandler.getOutput() != null) {
            lastOutput = outputHandler.getOutput().toString();
            return lastOutput;
        } else {
            return lastOutput;
        }
    }

    public int getAvailableCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    private class ProcessMonitor extends Thread {

        Process proc;

        ProcessMonitor(Process proc) {
            this.proc = proc;
        }

        public void run() {
            try {

                raiseMiningServiceStateChange(true);
                if (proc != null) {
                    proc.waitFor();
                }
                raiseMiningServiceStateChange(false);

            } catch (Exception e) {
                // assume problem with process and not running
                raiseMiningServiceStateChange(false);
                Log.e(LOG_TAG, "exception:", e);
            }
        }
    }

    private class OutputReaderThread extends Thread {

        private InputStream inputStream;
        private StringBuilder output = new StringBuilder();
        private BufferedReader reader;

        OutputReaderThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + System.lineSeparator());
                    if (line.contains("accepted")) {
                        accepted++;
                    } else if (line.contains("speed")) {
                        String[] split = TextUtils.split(line, " ");
                        speed = split[split.length - 2];
                        if (speed.equals("n/a")) {
                            speed = split[split.length - 6];
                        }
                    }
                    if (output.length() > 50000)
                        output.delete(0, output.indexOf(System.lineSeparator(), 100));

                    if (currentThread().isInterrupted()) return;
                }

            } catch (IOException e) {
                Log.w(LOG_TAG, "exception", e);
            }
        }

        public StringBuilder getOutput() {
            return output;
        }

    }
}
