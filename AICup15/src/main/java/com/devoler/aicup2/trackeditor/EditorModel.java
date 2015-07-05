package com.devoler.aicup2.trackeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.Direction;

final class EditorModel {
	private int width;
	private List<Direction> axis = new ArrayList<>();
	
	private Set<Pair<Integer, Integer>> trackPoints = new HashSet<>();
	private List<Pair<Integer, Integer>> axisPoints = new ArrayList<>();
	
	public EditorModel(final int width) {
		this.width = width;
		recalculatePoints();
	}
	
	private void recalculatePoints() {
		// fill in the axis
		Pair<Integer, Integer> point = Pair.of(0, 0);
		axisPoints.clear();
		axisPoints.add(point);
		for(Direction direction: axis) {
			point = direction.apply(point);
			axisPoints.add(point);
		}
		
		// fill in the track
		trackPoints.clear();
		for(Pair<Integer, Integer> axisPoint: axisPoints) {
			for(int x = axisPoint.getLeft() - width; x <= axisPoint.getLeft() + width; x++) {
				for(int y = axisPoint.getRight() - width; y <= axisPoint.getRight() + width; y++) {
					int distance = Math.abs(axisPoint.getLeft() - x) + Math.abs(axisPoint.getRight() - y);
					if (distance <= width) {
						trackPoints.add(Pair.of(x, y));
					}
				}
			}
		}
	}
	
	public synchronized void addAxis(List<Direction> directions) {
		Direction currentDirection = null;
		if (!axis.isEmpty()) {
			currentDirection = axis.get(axis.size() - 1);
		}
		for(Direction direction: directions) {
			if ((direction == null) || (direction.opposite() == currentDirection)) {
				break;
			}
			axis.add(direction);
			currentDirection = direction;
		}
		recalculatePoints();
	}

	public synchronized void removeTip() {
		if (!axis.isEmpty()) {
			axis.remove(axis.size() - 1);
			recalculatePoints();
		}
	}

	public synchronized List<Pair<Integer, Integer>> getAxisPoints() {
		return new ArrayList<>(axisPoints);
	}
	
	public synchronized Set<Pair<Integer, Integer>> getTrackPoints() {
		return new HashSet<>(trackPoints);
	}
}
