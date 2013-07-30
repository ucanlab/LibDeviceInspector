package au.csie.ucanlab;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;

public class DeviceInspector extends Application {
	
	/**
	String getProcessorHz(): Inspecting CPU frequency (in MHz)
	*/
	public String getProcessorMHz() {
		
		String result = "";
		ProcessBuilder cmd;
		
		try {
			String[] args = {"/system/bin/cat",
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		result = result.trim();
		int freq = Integer.valueOf(result);
		freq = freq / 1000;
		return String.valueOf(freq).trim();
	}
	
	/**
	String getExternalStorageSize(): Inspecting external storage (in GB)
	*/
	public String getExternalStorageSize() {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		long blockSize = statFs.getBlockSize();
		long totalSize = statFs.getBlockCount()*blockSize/(1024*1024*1024);
		String str=Long.toString(totalSize);     
		return str;
	}
	
	/**
	String getLocalIP(): Inspecting local IP address
	*/
	public String getLocalIP() {
		String ip="";
		try {
			// Obtain all network interfaces (NICs)
			Enumeration<NetworkInterface>en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				// Obtain IP address from this NIC
				Enumeration<InetAddress>enumIpAddr = intf.getInetAddresses();
				while(enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(!inetAddress.isLoopbackAddress()){
						ip = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch(Exception e) {
			ip = "check IP error:" + e;
		}
		return ip;
	}
	
	/**
	 * String getBatteryLevel(Context context): Inspecting battery level (in %) <p> 
	 * @param context <br>
	 *     In caller, you must do the following things in your class<br> 
	 *     1. Declare a Context object as member:<br> 
	 *        private static Context context;<p> 
	 *     2. Initialize the instance of Context object in onCreate():<br> 
	 *        context = getApplicationContext();<p> 
	 *     3. Pass context to getBatteryLevel(Context context):<br> 
	 *        DeviceInspector DI = new DeviceInspector();<br> 
	 *        String BatteryLevel = DI.getBatteryLevel(context)
	 * @return <br>
	 *     Battery level represented as percentage (0 ~ 100)  
	*/
	public String getBatteryLevel(Context context) {
		//Log.d(Battery, HelloBroadcastReceiver.Battery);		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		float level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		float scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int batteryPct = (int)(100 * level / scale);
		return String.valueOf(batteryPct);
	}	
}
