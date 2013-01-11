/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.rasc.portaldemos.snake;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.ImmutableMap;

@Handler("/snake")
public class SnakeHandler {

	private static final Logger log = LoggerFactory.getLogger(SnakeHandler.class);

	public static final int PLAYFIELD_WIDTH = 640;

	public static final int PLAYFIELD_HEIGHT = 480;

	public static final int GRID_SIZE = 10;

	private static final long TICK_DELAY = 100;

	private static final Random random = new Random();

	private final Timer gameTimer = new Timer(SnakeHandler.class.getSimpleName() + " Timer");

	private final ConcurrentHashMap<String, Snake> snakes = new ConcurrentHashMap<>();

	public SnakeHandler() {
		gameTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					tick();
				} catch (RuntimeException e) {
					log.error("Caught to prevent timer from shutting down", e);
				}
			}
		}, TICK_DELAY, TICK_DELAY);
	}

	@Name("snake")
	Room room;

	@On.open
	public void open(Socket socket) {
		String id = socket.param("id");
		Snake snake = new Snake(id, socket);
		snakes.put(id, snake);

		List<Map<String, ?>> snakesObjects = new ArrayList<>();
		for (Iterator<Snake> iterator = getSnakes().iterator(); iterator.hasNext();) {
			Snake s = iterator.next();
			snakesObjects.add(ImmutableMap.of("id", s.getId(), "color", s.getHexColor()));
		}

		room.add(socket);
		room.send("join", snakesObjects);
	}

	@On("north")
	public void north(Socket socket) {
		snakes.get(socket.param("id")).setDirection(Direction.NORTH);
	}

	@On("west")
	public void west(Socket socket) {
		snakes.get(socket.param("id")).setDirection(Direction.WEST);
	}

	@On("east")
	public void east(Socket socket) {
		snakes.get(socket.param("id")).setDirection(Direction.EAST);
	}

	@On("south")
	public void south(Socket socket) {
		snakes.get(socket.param("id")).setDirection(Direction.SOUTH);
	}

	@On.close
	public void close(Socket socket) {
		Snake removedSnake = snakes.remove(socket.param("id"));
		room.remove(socket);
		room.send("leave", ImmutableMap.of("id", removedSnake.getId()));
	}

	private void tick() {
		List<Map<String, Object>> snakeLocations = new ArrayList<>();
		for (Iterator<Snake> iterator = getSnakes().iterator(); iterator.hasNext();) {
			Snake snake = iterator.next();
			snake.update(getSnakes());
			snakeLocations.add(snake.getLocationObjects());
		}
		room.send("update", snakeLocations);
	}

	private Collection<Snake> getSnakes() {
		return Collections.unmodifiableCollection(snakes.values());
	}

	public static String getRandomHexColor() {
		float hue = random.nextFloat();
		// sat between 0.1 and 0.3
		float saturation = (random.nextInt(2000) + 1000) / 10000f;
		float luminance = 0.9f;
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return '#' + Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1);
	}

	public static Location getRandomLocation() {
		int x = roundByGridSize(random.nextInt(SnakeHandler.PLAYFIELD_WIDTH));
		int y = roundByGridSize(random.nextInt(SnakeHandler.PLAYFIELD_HEIGHT));
		return new Location(x, y);
	}

	private static int roundByGridSize(int value) {
		int v = value;
		v = v + (SnakeHandler.GRID_SIZE / 2);
		v = v / SnakeHandler.GRID_SIZE;
		v = v * SnakeHandler.GRID_SIZE;
		return v;
	}
}
