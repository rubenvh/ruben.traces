package ruben.traces.engine;

import java.util.ArrayList;

import ruben.common.drawing.IDrawingSystem;
import ruben.common.drawing.Line;
import ruben.common.drawing.Point;
import ruben.common.processing.applet.BaseAppletDrawer;
import ruben.traces.context.IConfigProvider;
import ruben.traces.context.ITraceConfiguration;

public class MultipleTargetsDrawingAppletDrawer extends BaseAppletDrawer
{
	ITargetsRepository _targetsRepo;
	IDrawingSystem _drawingSystem;
	ITraceConfiguration _config;
	
	public MultipleTargetsDrawingAppletDrawer(IDrawingSystem drawingSystem, ITargetsRepository targetsRepo, IConfigProvider configProvider)
	{
		_targetsRepo = targetsRepo;
		_drawingSystem = drawingSystem;
		_config = configProvider.get_config();
	}

	public void draw()
	{
//		ArrayList<Point> targets = _targetsRepo.get_targets();
//		for (int i = 0; i < targets.size(); i++){
//			
//			Point p = targets.get(i);
//			p.SetColor(_config.get_foreground()).SetLineSize(_config.get_line_weight());
//			
//			_drawingSystem.AddLine(p);
//		}
		ArrayList<Line> targets = _targetsRepo.get_linetargets();
		for (int i = 0; i < targets.size(); i++){
			
			Line p = targets.get(i);
			p.SetColor(_config.get_foreground()).SetLineSize(_config.get_line_weight());
			
			_drawingSystem.AddLine(p);
		}
		
	}

	public void keyPressed()
	{
		// TODO Auto-generated method stub

	}

	public void keyReleased()
	{
		// TODO Auto-generated method stub

	}

	public void mousePressed()
	{
		// TODO Auto-generated method stub

	}

	public void mouseReleased()
	{
		// TODO Auto-generated method stub

	}

}
