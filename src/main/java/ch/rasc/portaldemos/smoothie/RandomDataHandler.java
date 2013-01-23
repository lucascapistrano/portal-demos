package ch.rasc.portaldemos.smoothie;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;

@Bean
public class RandomDataHandler {

	private static ScheduledExecutorService threadPool;

	private static Random random = new Random();

	@Wire("smoothie")
	Room room;

	@On.close
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
		if (room.size() == 0) {
			threadPool.shutdownNow();
			threadPool = null;
		}
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
		if (threadPool == null) {
			threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleWithFixedDelay(new RandomDataGenerator(), 1, 1, TimeUnit.SECONDS);
		}
	}

	private class RandomDataGenerator implements Runnable {
		@Override
		public void run() {
			CpuData cpuData = new CpuData();
			cpuData.setHost1(new double[] { random.nextDouble(), random.nextDouble(), random.nextDouble(),
					random.nextDouble() });
			cpuData.setHost2(new double[] { random.nextDouble(), random.nextDouble(), random.nextDouble(),
					random.nextDouble() });
			cpuData.setHost3(new double[] { random.nextDouble(), random.nextDouble(), random.nextDouble(),
					random.nextDouble() });
			cpuData.setHost4(new double[] { random.nextDouble(), random.nextDouble(), random.nextDouble(),
					random.nextDouble() });
			room.send("cpu", cpuData);

		}
	}
}
