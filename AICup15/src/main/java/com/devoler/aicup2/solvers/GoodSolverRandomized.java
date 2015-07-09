package com.devoler.aicup2.solvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.Intersections;
import com.devoler.aicup2.model.Intersections.Intersection;
import com.devoler.aicup2.model.Intersections.Segment;
import com.devoler.aicup2.model.Intersections.Vertex;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.RaceTrackParser;
import com.devoler.aicup2.model.TrackCell;

public final class GoodSolverRandomized extends CallableSolver {
	private static final Set<TrackCell> START_CELLS = EnumSet.of(
			TrackCell.START_CELL, TrackCell.START_LINE);

	private static class CellData {
		private final TrackCell type;
		private int distanceToFinish = Integer.MAX_VALUE;
		private Set<Pair<Integer, Integer>> legalMoves = new HashSet<>();
		
		public CellData(final TrackCell type) {
			this.type = type;
		}
		
		public TrackCell getType() {
			return type;
		}
		
		public boolean offerDistanceToFinish(int distanceToFinish) {
			if (distanceToFinish < this.distanceToFinish) {
				this.distanceToFinish = distanceToFinish;
				return true;
			}
			return false;
		}
		
		public Set<Pair<Integer, Integer>> getLegalMoves() {
			return legalMoves;
		}
	}

	private static boolean isNavigable(CellData[][] cells, Pair<Integer, Integer> coords) {
		return isNavigable(cells, coords.getLeft(), coords.getRight());
	}
	
	private static boolean isNavigable(CellData[][] cells, int x, int y) {
		return y >= 0 && x >= 0 && x < cells.length && y < cells[0].length
				&& cells[x][y] != null && cells[x][y].getType().isNavigable();
	}
	
	private static boolean isNavigableFrom(CellData[][] cells, int x, int y, TrackCell from) {
		if (!isNavigable(cells, x, y)) {
			return false;
		}
		if (from == TrackCell.PRE_START_LINE && START_CELLS.contains(cells[x][y].getType())) {
			return false;
		}
		return true;
	}
	
	private static void fillDistances(CellData[][] cells) {
		// fill in pre-start cells
		Set<Pair<Integer, Integer>> currentLayer = new HashSet<>();
		for(int x = 0; x < cells.length; x++) {
			for(int y = 0; y < cells[0].length; y++) {
				if (cells[x][y].getType() == TrackCell.PRE_START_LINE) {
					cells[x][y].offerDistanceToFinish(1);
					currentLayer.add(Pair.of(x, y));
				}
			}
		}
		int currentDistance = 1;
		while(true) {
			// iterate over the current layer cells and fill in the next layer
			Set<Pair<Integer, Integer>> nextLayer = new HashSet<>();
			for(Pair<Integer, Integer> curLayerCell: currentLayer) {
				int x = curLayerCell.getLeft();
				int y = curLayerCell.getRight();
				TrackCell from = cells[x][y].getType();
				if (isNavigableFrom(cells, x - 1, y, from) && cells[x - 1][y].offerDistanceToFinish(currentDistance + 1)) {
					nextLayer.add(Pair.of(x - 1, y));
				}
				if (isNavigableFrom(cells, x + 1, y, from) && cells[x + 1][y].offerDistanceToFinish(currentDistance + 1)) {
					nextLayer.add(Pair.of(x + 1, y));
				}
				if (isNavigableFrom(cells, x, y - 1, from) && cells[x][y - 1].offerDistanceToFinish(currentDistance + 1)) {
					nextLayer.add(Pair.of(x, y - 1));
				}
				if (isNavigableFrom(cells, x, y + 1, from) && cells[x][y + 1].offerDistanceToFinish(currentDistance + 1)) {
					nextLayer.add(Pair.of(x, y + 1));
				}
			}
			if (nextLayer.isEmpty()) {
				break;
			}
			currentLayer = nextLayer;
			currentDistance++;
		}
		
	}
	
	private static Pair<Boolean, Intersection> isMoveLegal(CellData[][] cells,
			Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
		List<Intersection> intersections = Intersections.getIntersections(from,
				to);
		List<TrackCell> intersectionTypes = new ArrayList<>();
		Intersection finish = null;
		for (Intersection i : intersections) {
			if (!isIntersectionLegal(cells, i)) {
				return Pair.of(Boolean.FALSE, null);
			}
			intersectionTypes.add(cellTypeOfIntersection(cells, i));
		}
		for (int i = 1; i < intersectionTypes.size(); i++) {
			TrackCell prev = intersectionTypes.get(i - 1);
			// illegal sequences are:
			if (START_CELLS.contains(intersectionTypes.get(i))) {
				// 1. anything but pre-start/start/start cell -> start/start
				// cell
				if (START_CELLS.contains(prev)) {
					continue;
				} else if (prev == TrackCell.PRE_START_LINE) {
					// this is a finish
					finish = intersections.get(i);
					break;
				} else {
					// illegal move
					return Pair.of(Boolean.FALSE, null);
				}
			} else if (intersectionTypes.get(i) == TrackCell.PRE_START_LINE) {
				// 2. anything but track/pre-start -> pre-start
				if ((prev != TrackCell.PRE_START_LINE)
						&& (prev != TrackCell.TRACK)) {
					// illegal move
					return Pair.of(Boolean.FALSE, null);
				}
			}
		}
		return Pair.of(Boolean.TRUE, finish);
	}
	
	private static TrackCell cellTypeOfIntersection(CellData[][] cells,
			Intersection i) {
		if (i instanceof Vertex) {
			Pair<Integer, Integer> coords = ((Vertex) i).getCoords();
			return safeCellAt(cells, coords).getType();
		} else if (i instanceof Segment) {
			Pair<Vertex, Vertex> vertices = ((Segment) i).getVertices();
			// at least one of the vertices should be navigable
			return segmentType(
					cellTypeOfIntersection(cells, vertices.getLeft()),
					cellTypeOfIntersection(cells, vertices.getRight()));
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
	}
	
	private static CellData safeCellAt(CellData[][] cells, Pair<Integer, Integer> coords) {
		int x = coords.getLeft();
		int y = coords.getRight();
		return safeCellAt(cells, x, y);
	}

	private static CellData safeCellAt(CellData[][] cells, int x, int y) {
		if (y >= 0 && x >= 0 && x < cells.length && y < cells[0].length) {
			return cells[x][y];
		}
		return null;
	}
	
	private static boolean isIntersectionLegal(CellData[][] cells, Intersection i) {
		if (i instanceof Vertex) {
			Pair<Integer, Integer> coords = ((Vertex) i).getCoords();
			return isNavigable(cells, coords);
		} else if (i instanceof Segment) {
			Pair<Vertex, Vertex> vertices = ((Segment) i).getVertices();
			// at least one of the vertices should be navigable
			return isNavigable(cells, vertices.getLeft().getCoords())
					|| isNavigable(cells, vertices.getRight().getCoords());
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
	}
	
	private static TrackCell segmentType(TrackCell t1, TrackCell t2) {
		if (t1 == null) {
			return t2;
		}
		if (t2 == null) {
			return t1;
		}
		return t1.getPriority() > t2.getPriority() ? t1 : t2;
	}
	
	private static Set<Pair<Integer, Integer>> cellsAtDistance(CellData[][] cells, int cellX, int cellY, int distance) {
		Set<Pair<Integer, Integer>> result = new HashSet<>();
		for(int x = cellX - distance; x <= cellX + distance; x++) {
			for(int y = cellY - distance; y <= cellY + distance; y++) {
				if (Math.abs(x - cellX) + Math.abs(y - cellY) == distance) {
					CellData cellData = safeCellAt(cells, x, y);
					if ((cellData != null) && (cellData.getType().isNavigable()) && (isMoveLegal(cells, Pair.of(cellX, cellY), Pair.of(x, y)).getLeft())) {
						result.add(Pair.of(x, y));
					}
				}
			}
		}
		return result;
	}
	
//	private static void fillLegalMoves(CellData[][] cells) {
//		int moveSpeed = 1;
//		while (true) {
//			int movesAdded = 0;
//			for (int x = 0; x < cells.length; x++) {
//				for (int y = 0; y < cells[0].length; y++) {
//					if (cells[x][y].getType().isNavigable()) {
//						cells[x][y].getLegalMoves().add(Pair.of(x, y));
//						Set<Pair<Integer, Integer>> moves = cellsAtDistance(
//								cells, x, y, moveSpeed);
//						for (Pair<Integer, Integer> move : moves) {
//							cells[x][y].getLegalMoves().add(move);
//							movesAdded++;
//						}
//					}
//				}
//			}
//			if (movesAdded == 0) {
//				break;
//			}
//			moveSpeed++;
//		}
//	}

	private static void fillLegalMoves(CellData[][] cells) {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[0].length; y++) {
				if (cells[x][y].getType().isNavigable()) {
					cells[x][y].getLegalMoves().add(Pair.of(x, y));
				}
			}
		}
		int moveSpeed = 1;
		while (true) {
			int movesAdded = 0;
			for (int x = 0; x < cells.length; x++) {
				for (int y = 0; y < cells[0].length; y++) {
					if (cells[x][y].getType().isNavigable()) {
						Set<Pair<Integer, Integer>> moves = cellsAtDistance(
								cells, x, y, moveSpeed);
						for (Pair<Integer, Integer> move : moves) {
							// check if the move has continuation
							int contX = 2 * move.getLeft() - x;
							int contY = 2 * move.getRight() - y;
							Set<Pair<Integer, Integer>> contMoves = cells[move
									.getLeft()][move.getRight()]
									.getLegalMoves();
							if (contMoves.contains(Pair.of(contX, contY))
									|| contMoves.contains(Pair.of(contX + 1,
											contY))
									|| contMoves.contains(Pair.of(contX - 1,
											contY))
									|| contMoves.contains(Pair.of(contX,
											contY - 1))
									|| contMoves.contains(Pair.of(contX,
											contY + 1))) {
								// this move has no future
								cells[x][y].getLegalMoves().add(move);
								movesAdded++;
							}
						}
					}
				}
			}
			if (movesAdded == 0) {
				break;
			}
			moveSpeed++;
		}
	}
	
	private static int getMoveValue(CellData[][] cells, int fromX, int fromY, int toX, int toY) {
		// check if this is the finishing move
		Pair<Boolean, Intersection> moveResult = isMoveLegal(cells, Pair.of(fromX, fromY), Pair.of(toX, toY));
		if (!moveResult.getLeft()) {
			throw new IllegalArgumentException("Illegal move: [" + fromX + ", " + fromY + "] -> [" + toX + ", " + toY + "]");
		}
		if (moveResult.getRight() != null) {
			// the higher speed, the better
			return 1000 * (Math.abs(toX - fromX) + Math.abs(fromY - toY));
		}
		// this is not a finishing move
		return cells[toX][toY].distanceToFinish * (-1000) + (Math.abs(toX - fromX) + Math.abs(fromY - toY));
	}
	
	@Override
	public Callable<String> solverFor(final String task) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				RaceTrack track = RaceTrackParser.parse(task);
				CellData[][] cells = new CellData[track.getWidth()][track.getHeight()];
				for(int x = 0; x < cells.length; x++) {
					for(int y = 0; y < cells[0].length; y++) {
						cells[x][y] = new CellData(track.cellAt(x, y));
					}
				}
				System.out.println("Created cells");
				fillDistances(cells);
				System.out.println("Distance to finish calculated for each cell");
				for (int y = 0; y < cells[0].length; y++) {
					for (int x = 0; x < cells.length; x++) {
						System.out.print(cells[x][y].type.isNavigable() ? String.format("% 4d", cells[x][y].distanceToFinish): "   -");
						System.out.print(" | ");
					}
					System.out.println();
				}
				// now let's fill in all legal moves
				fillLegalMoves(cells);
				System.out.println("moves calculated for each cell");
				for (int y = 0; y < cells[0].length; y++) {
					for (int x = 0; x < cells.length; x++) {
						System.out.print(cells[x][y].type.isNavigable() ? String.format("% 4d", cells[x][y].legalMoves.size()): "   -");
						System.out.print(" | ");
					}
					System.out.println();
				}
				// now let's race choosing best moves all the way
				StringBuilder solutionBuilder = new StringBuilder();
				List<Pair<Integer, Integer>> moves = new ArrayList<>();
				Pair<Integer, Integer> pos = track.getStartCell();
				Pair<Integer, Integer> speed = Pair.of(0, 0);
				moves.add(pos);
				Random r = new Random();
				while(true) {
					// choose best move from pos
					CellData cellData = cells[pos.getLeft()][pos.getRight()];
					
					Map<Pair<Integer, Integer>, Character> availableMoves = new HashMap<>();
					int contX = pos.getLeft() + speed.getLeft();
					int contY = pos.getRight() + speed.getRight();
					
					Pair<Integer, Integer> cont0 = Pair.of(contX, contY);
					if (cellData.getLegalMoves().contains(cont0)) {
						availableMoves.put(cont0, '0');
					}
					Pair<Integer, Integer> contL = Pair.of(contX - 1, contY);
					if (cellData.getLegalMoves().contains(contL)) {
						availableMoves.put(contL, 'L');
					}
					Pair<Integer, Integer> contR = Pair.of(contX + 1, contY);
					if (cellData.getLegalMoves().contains(contR)) {
						availableMoves.put(contR, 'R');
					}
					Pair<Integer, Integer> contU = Pair.of(contX, contY - 1);
					if (cellData.getLegalMoves().contains(contU)) {
						availableMoves.put(contU, 'U');
					}
					Pair<Integer, Integer> contD = Pair.of(contX, contY + 1);
					if (cellData.getLegalMoves().contains(contD)) {
						availableMoves.put(contD, 'D');
					}
					final Map<Pair<Integer, Integer>, Integer> moveMap = new HashMap<>();
					
					for(Pair<Integer, Integer> move: availableMoves.keySet()) {
						int moveValue = getMoveValue(cells, pos.getLeft(), pos.getRight(), move.getLeft(), move.getRight());
						moveMap.put(move, moveValue);
					}
					
					List<Pair<Integer, Integer>> allMoves = new ArrayList<>(moveMap.keySet());
					Collections.sort(allMoves, new Comparator<Pair<Integer, Integer>>() {
						@Override
						public int compare(Pair<Integer, Integer> o1,
								Pair<Integer, Integer> o2) {
							return Integer.compare(moveMap.get(o2), moveMap.get(o1));
						}
					});
					Pair<Integer, Integer> bestMove = allMoves.get(0);
					// randomly select second-best move
					if (r.nextBoolean() && (allMoves.size() > 1)) {
						bestMove = allMoves.get(1);
					}
					moves.add(bestMove);
					speed = Pair.of(bestMove.getLeft() - pos.getLeft(), bestMove.getRight() - pos.getRight());
					pos = bestMove;
					solutionBuilder.append(availableMoves.get(bestMove));
					if (moveMap.get(bestMove) > 0) {
						// finishing move
						break;
					}
				}
				System.out.println("Finished in " + (moves.size() - 1) + " moves");
				// convert to solution format
				return solutionBuilder.toString();
			}
		};
	}
}
