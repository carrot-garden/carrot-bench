package bench;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainW520 {

	static final Logger log = LoggerFactory.getLogger(MainW520.class);

	static String tempFile(int index) {
		return "/sys/devices/platform/thinkpad_hwmon/temp" + index + "_input";
	}

	static float tempValue(int index) {
		try {
			File file = new File(tempFile(index));
			String temp = FileUtils.readFileToString(file);
			return Float.parseFloat(temp) / 1000;
		} catch (Exception e) {
			return -1.0F;
		}
	}

	static void logTemp() {
		log.debug("###");
		for (int index = 0; index < 20; index++) {
			float value = tempValue(index);
			if (value < 0) {
				continue;
			}
			log.debug("temp : {} : {}", index, value);
		}
	}

	static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			//
		}
	}

	static long past;

	static void startTask(Task... tasks) {
		for (Task task : tasks) {
			Thread thread = new Thread(task, "# task");
			thread.start();
			log.debug("started : {}", thread);
			sleep(100);
		}
	}

	static class Task implements Runnable {

		private long saved;
		private long count;

		@Override
		public void run() {
			while (true) {
				count++;
			}
		}

		public long get() {
			return count;
		}

		public long diff() {
			long c = count;
			long s = saved;
			long diff = c - s;
			saved = c;
			return diff;
		}

	}

	static void logTask(Task... tasks) {
		long diff = 0;
		for (Task task : tasks) {
			diff += task.diff();
		}
		log.debug("diff : {}", String.format("%,d", diff));
	}

	static Task[] startTask(int total) {
		Task[] tasks = new Task[total];
		for (int k = 0; k < total; k++) {
			Task task = new Task();
			tasks[k] = task;
			startTask(task);
		}
		return tasks;
	}

	public static void main(String[] args) throws Exception {

		log.debug("started");

		Task[] tasks = startTask(2);

		while (true) {
			logTemp();
			logTask(tasks);
			sleep(1000);
		}

	}

}
