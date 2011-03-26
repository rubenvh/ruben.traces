package ruben.traces.engine;

import java.util.ArrayList;
import java.util.Vector;
import processing.core.PApplet;
import ruben.common.drawing.*;
import ruben.common.processing.applet.*;
import ruben.common.processing.drawing.*;
import ruben.common.repository.*;

@SuppressWarnings("serial")
public class Traces extends BasePApplet implements ITargetRepository
{
	public static void main(String args[]) {
		PApplet.main(new String[] {
				"display=1",
				"--bgcolor=#000000", "--present-stop-color=#000000",
				"--exclusive",
				"--present",
				"ruben.traces.engine.Traces" });
	}

	IDrawingSystem _drawingSystem;
	IRenderSystem _renderSystem;
	ArrayList<IGraphicObjectVisitor> _visitors;
	
	public void setup()
	{
		_drawingSystem = new DrawingSystem(this);
		_renderSystem = new RenderSystem(_drawingSystem, this);
		_visitors = new ArrayList<IGraphicObjectVisitor>(1);
		_visitors.add(new FaderVisitor(200, -1, 50));
		
		super.setup();
		
		this.size(700,700);
	}
	
	public void draw()
	{
		background(0);
		noFill();
		strokeWeight(1);
		stroke(255);
		rect(0, 0, 699, 699);

		// redraw screen using renderSystem
		_renderSystem.Render();

		super.draw();

		// apply visitors
		for (int i = 0; i < _visitors.size(); i++)
		{
			_drawingSystem.GetGraphics().Accept(_visitors.get(i));
		}
	}

	public void mouseReleased()
	{
		super.mouseReleased();

	}

	public void keyPressed()
	{
		super.keyPressed();
	}

	public void keyReleased()
	{
		super.keyReleased();
	}

	protected void load_applet_drawers()
	{
		_drawers = new Vector<IAppletDrawer>(1);
		_drawers.add(new MouseDrawingAppletDrawer(this, _drawingSystem, this));

	}

	public Point get_target()
	{
		return new Point(pmouseX, pmouseY);
	}

}
