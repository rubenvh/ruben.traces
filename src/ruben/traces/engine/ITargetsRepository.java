package ruben.traces.engine;

import java.util.ArrayList;

import ruben.common.drawing.Line;
import ruben.common.drawing.Point;

public interface ITargetsRepository
{
	ArrayList<Point> get_targets();
	ArrayList<Line> get_linetargets();
}
