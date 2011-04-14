package ruben.traces.engine;

import java.util.ArrayList;
import java.util.Vector;

import processing.core.PApplet;
import ruben.common.drawing.*;
import ruben.common.processing.applet.*;
import ruben.common.processing.drawing.*;
import ruben.common.processing.video.IWindowedImageSource;
import ruben.common.processing.video.OpenCVVideoSource;
import ruben.common.repository.*;
import ruben.common.state.Parameter;
import ruben.traces.context.IConfigProvider;
import ruben.traces.context.IContextProvider;
import ruben.traces.context.ITraceConfiguration;
import ruben.traces.context.TraceConfiguration;
import ruben.traces.context.TraceContext;

@SuppressWarnings("serial")
public class Traces extends BasePApplet implements ITargetRepository,
		IImageSourceRepository, IConfigProvider, IContextProvider
{
	public static void main(String args[])
	{
		PApplet.main(new String[] { "display=1", "--bgcolor=#000000",
				"--present-stop-color=#000000", "--exclusive", "--present",
				"ruben.traces.engine.Traces" });
	}

	TraceContext _context;
	IDrawingSystem _drawingSystem;
	IRenderSystem _renderSystem;
	ArrayList<IGraphicObjectVisitor> _visitors;
	IWindowedImageSource _source;
	ITraceConfiguration _config;

	public void setup()
	{
		_config = new TraceConfiguration(this, "app.config");
		_context = new TraceContext();
		_drawingSystem = new DrawingSystem(this);
		_renderSystem = new RenderSystem(_drawingSystem, this);
		_visitors = new ArrayList<IGraphicObjectVisitor>(1);
		_visitors.add(new FaderVisitor(_config.get_fade_decay1(), _config
				.get_fade_decay2(), _config.get_fade_decay3(), _config
				.get_fade_to(), _config.get_fade_to2()));
		_visitors.add(new DeletingVisitor());

		if (_config.get_use_camera())
			_source = OpenCVVideoSource.Create(this, 2, _config
					.get_video_width(), _config.get_video_height(), _config
					.get_camera_number(), new Parameter<Integer>(20));
		else
			_source = OpenCVVideoSource.Create(this, 2, _config
					.get_video_width(), _config.get_video_height(), _config
					.get_movie_file(), new Parameter<Integer>(20));

		super.setup();

		this.size(_config.get_screen_width(), _config.get_screen_height());
	}

	public void draw()
	{
		_source.step();

		background(_config.get_background());

		// border
		noFill();
		strokeWeight(1);
		stroke(255);
		rectMode(NORMAL);
		rect(0, 0, _config.get_screen_width() - 1,
				_config.get_screen_height() - 1);

		// redraw screen using renderSystem
		_renderSystem.Render();

		super.draw();

		// apply visitors
		for (int i = 0; i < _visitors.size(); i++)
		{
			_drawingSystem.GetGraphics().Accept(_visitors.get(i));
		}

		if (_context.debug)
		{
			this.fill(0, 255, 0);
			this.text(String.format("number of graphics = %d", _drawingSystem
					.GetGraphics().GetChildren().size()), 10, 40);
		}
	}

	public void mouseReleased()
	{
		super.mouseReleased();

	}

	public void keyPressed()
	{
		super.keyPressed();

		if (key == ruben.common.keyboard.KeyConvertor.get_char("="))
		{
			_context.debug = !_context.debug;
		}
	}

	public void keyReleased()
	{
		super.keyReleased();
	}

	protected void load_applet_drawers()
	{
		_drawers = new Vector<IAppletDrawer>(2);
		MotionDetection m = new MotionDetection(this, this, this, this);
		_drawers.add(m);
		_drawers.add(new MultipleTargetsDrawingAppletDrawer(_drawingSystem, m,
				this));
		_drawers.add(new MaterializingAppletDrawer(this, this, _drawingSystem));
	}

	public Point get_target()
	{
		return new Point(pmouseX, pmouseY);
	}

	public IWindowedImageSource get_source()
	{
		return _source;
	}

	public ITraceConfiguration get_config()
	{
		return _config;
	}

	public TraceContext get_context()
	{
		return _context;
	}

}
