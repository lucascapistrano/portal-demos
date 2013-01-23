package ch.rasc.portaldemos.smoothie;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Prepare;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;

@Bean
public class RandomDataHandler {

	@Wire("smoothie")
	Room room;

	@Prepare
	public void prepare() {
		SmoothieInitializer.threadPool.scheduleWithFixedDelay(new RandomDataGenerator(), 1, 1, TimeUnit.SECONDS);
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

	private class RandomDataGenerator implements Runnable {
		private Random random = new Random();

		@Override
		public void run() {
			if (room.size() > 0) {
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
}
