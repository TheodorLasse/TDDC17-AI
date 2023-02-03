package tddc17;

import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

class MyAgentState {
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN = 0;
	final int WALL = 1;
	final int CLEAR = 2;
	final int DIRT = 3;
	final int HOME = 4;
	final int ACTION_NONE = 0;
	final int ACTION_MOVE_FORWARD = 1;
	final int ACTION_TURN_RIGHT = 2;
	final int ACTION_TURN_LEFT = 3;
	final int ACTION_SUCK = 4;

	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;

	MyAgentState() {
		for (int i = 0; i < world.length; i++)
			for (int j = 0; j < world[i].length; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}

	// Based on the last action and the received percept updates the x & y agent
	// position
	public void updatePosition(DynamicPercept p) {
		Boolean bump = (Boolean) p.getAttribute("bump");

		if (agent_last_action == ACTION_MOVE_FORWARD && !bump) {
			switch (agent_direction) {
				case MyAgentState.NORTH:
					agent_y_position--;
					break;
				case MyAgentState.EAST:
					agent_x_position++;
					break;
				case MyAgentState.SOUTH:
					agent_y_position++;
					break;
				case MyAgentState.WEST:
					agent_x_position--;
					break;
			}
		}
	}

	public void updateWorld(int x_position, int y_position, int info) {
		world[x_position][y_position] = info;
	}

	public void printWorldDebug() {
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[j][i] == UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i] == WALL)
					System.out.print(" # ");
				if (world[j][i] == CLEAR)
					System.out.print(" . ");
				if (world[j][i] == DIRT)
					System.out.print(" D ");
				if (world[j][i] == HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();

	private int[] homePos = new int[] { 0, 0 };

	// Here you can define your variables!
	public MyAgentState state = new MyAgentState();
	public int iterationCounter = state.world.length * state.world[0].length * 2;

	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other
	// percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if (action == 0) {
			state.agent_direction = ((state.agent_direction - 1) % 4);
			if (state.agent_direction < 0)
				state.agent_direction += 4;
			state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action == 1) {
			state.agent_direction = ((state.agent_direction + 1) % 4);
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		state.agent_last_action = state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}

	@Override
	public Action execute(Percept percept) {

		// DO NOT REMOVE this if condition!!!
		if (initnialRandomActions > 0) {
			return moveToRandomStartPosition((DynamicPercept) percept);
		} else if (initnialRandomActions == 0) {
			// process percept for the last step of the initial random actions
			initnialRandomActions--;
			state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action = state.ACTION_SUCK;
			return LIUVacuumEnvironment.ACTION_SUCK;
		}

		// This example agent program will update the internal agent state while only
		// moving forward.
		// START HERE - code below should be modified!

		System.out.println("x=" + state.agent_x_position);
		System.out.println("y=" + state.agent_y_position);
		System.out.println("dir=" + state.agent_direction);

		iterationCounter--;

		if (iterationCounter == 0)
			return NoOpAction.NO_OP;

		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean) p.getAttribute("bump");
		Boolean dirt = (Boolean) p.getAttribute("dirt");
		Boolean home = (Boolean) p.getAttribute("home");
		System.out.println("percept: " + p);

		if (home)
			this.homePos = new int[] { state.agent_x_position, state.agent_y_position };

		// State update based on the percept value and the last action
		state.updatePosition((DynamicPercept) percept);
		if (bump) {
			switch (state.agent_direction) {
				case MyAgentState.NORTH:
					state.updateWorld(state.agent_x_position, state.agent_y_position - 1, state.WALL);
					break;
				case MyAgentState.EAST:
					state.updateWorld(state.agent_x_position + 1, state.agent_y_position, state.WALL);
					break;
				case MyAgentState.SOUTH:
					state.updateWorld(state.agent_x_position, state.agent_y_position + 1, state.WALL);
					break;
				case MyAgentState.WEST:
					state.updateWorld(state.agent_x_position - 1, state.agent_y_position, state.WALL);
					break;
			}
		}
		if (dirt)
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.DIRT);
		else
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.CLEAR);

		state.printWorldDebug();

		// Next action selection based on the percept value
		if (dirt) {
			System.out.println("DIRT -> choosing SUCK action!");
			state.agent_last_action = state.ACTION_SUCK;
			return LIUVacuumEnvironment.ACTION_SUCK;
		}

		var targetDirection = getTargetDirection();

		if (targetDirection == -1) {
			return NoOpAction.NO_OP;
		}

		if (state.agent_direction == targetDirection) {
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		} else {
			state.agent_direction++;
			state.agent_direction %= 4;
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
	}

	private int getTargetDirection() {
		int x = state.agent_x_position;
		int y = state.agent_y_position;

		var path = getPathTo(false);

		if (path.isEmpty())
			path = getPathTo(true);

		if (path.size() < 2)
			return -1;

		var next = path.get(1);

		if (next[0] == x - 1 && next[1] == y)
			return MyAgentState.WEST;
		if (next[0] == x && next[1] == y - 1)
			return MyAgentState.NORTH;
		if (next[0] == x + 1 && next[1] == y)
			return MyAgentState.EAST;
		if (next[0] == x && next[1] == y + 1)
			return MyAgentState.SOUTH;

		return -1;
	}

	private List<int[]> getPathTo(boolean home) {
		boolean[][] visited = new boolean[state.world.length][state.world[0].length];

		Queue<List<int[]>> frontier = new LinkedList<List<int[]>>();
		var startList = new ArrayList<int[]>();
		startList.add(new int[] { state.agent_x_position, state.agent_y_position });
		frontier.add(startList);
		visited[state.agent_x_position][state.agent_y_position] = true;

		while (!frontier.isEmpty()) {
			var path = frontier.poll();
			var node = path.get(path.size() - 1);

			int x = node[0];
			int y = node[1];

			if ((state.world[x][y] == state.UNKNOWN && !home) || (x == this.homePos[0] && y == this.homePos[1] && home)) {
				System.out.println("Path found");
				return path;
			}

			var neighbors = new ArrayList<int[]>();
			neighbors.add(new int[] { x - 1, y });
			neighbors.add(new int[] { x, y - 1 });
			neighbors.add(new int[] { x + 1, y });
			neighbors.add(new int[] { x, y + 1 });

			for (var neighbor : neighbors) {
				int i = neighbor[0];
				int j = neighbor[1];

				if (visited[i][j] || state.world[i][j] == state.WALL)
					continue;
				
				visited[i][j] = true;

				var newPath = deepCopy(path);
				newPath.add(new int[] { i, j });

				frontier.add(newPath);
			}
		}

		System.out.println("No path found");

		return new ArrayList<>();
	}

	private List<int[]> deepCopy(List<int[]> list) {
		List<int[]> result = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			result.add(new int[] { list.get(i)[0], list.get(i)[1] });
		}

		return result;
	}
}

public class MyVacuumAgent extends AbstractAgent {
	public MyVacuumAgent() {
		super(new MyAgentProgram());
	}
}
