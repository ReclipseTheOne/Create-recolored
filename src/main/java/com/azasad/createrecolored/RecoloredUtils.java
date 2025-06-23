package com.azasad.createrecolored;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;

import java.util.List;

public class RecoloredUtils {
	public static final List<Axis> ALL_SIGNUM_AXIS = List.of(Axis.XP, Axis.XN, Axis.YP, Axis.YN, Axis.ZP, Axis.ZN);
	public static final List<Direction.Axis> ALL_AXIS = List.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z);
	public static final List<Direction> ALL_DIRECTIONS = List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN);
	public static final List<Direction> HORIZONTAL_DIRECTIONS = List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
	public static final List<Direction> VERTICAL_DIRECTIONS = List.of(Direction.UP, Direction.DOWN);

	public static Direction.Axis directionToDirAxis(Direction direction) {
		return switch (direction) {
			case UP, DOWN -> Direction.Axis.Y;
			case NORTH, SOUTH -> Direction.Axis.Z;
			case WEST, EAST -> Direction.Axis.X;
		};
	}

	public static List<Direction> dirAxisToDirection(Direction.Axis direction) {
		return switch(direction) {
			case X -> List.of(Direction.WEST, Direction.EAST);
			case Y -> List.of(Direction.DOWN, Direction.UP);
			case Z -> List.of(Direction.NORTH, Direction.SOUTH);
		};
	}
}
