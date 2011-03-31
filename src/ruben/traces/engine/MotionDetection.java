package ruben.traces.engine;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PConstants;
import processing.core.PImage;
import ruben.common.drawing.Line;
import ruben.common.drawing.Point;
import ruben.common.processing.applet.BaseAppletDrawer;
import ruben.common.processing.applet.BasePApplet;
import ruben.common.processing.video.IWindowedImageSource;
import ruben.traces.context.IConfigProvider;
import ruben.traces.context.IContextProvider;
import ruben.traces.context.ITraceConfiguration;
import s373.flob.ABlob;
import s373.flob.Flob;
import s373.flob.trackedBlob;

public class MotionDetection extends BaseAppletDrawer implements
		ITargetsRepository
{
	int _videowidth, _videoheight, _screenwidth, _screenheight;
	IImageSourceRepository _sourceRepo;
	IContextProvider _contextProvider;
	BasePApplet _applet;
	PImage _cur_image;
	PImage _diff_image;
	PImage _edge_image;
	Flob _flob;
	ArrayList<ABlob> _blobs;
	ArrayList<trackedBlob> _tBlobs;
	private int _edgeThreshold = 2;
	private int _dilation = 2;
	private int _distance = 100;
	private int _maxAge = 5;
	ArrayList<Point> _points;
	ArrayList<Point> _previousPoints;
	ArrayList<Line> _lines;

	public MotionDetection(BasePApplet applet,
			IImageSourceRepository sourceRepo, IConfigProvider configProvider,
			IContextProvider contextProvider)
	{
		ITraceConfiguration c = configProvider.get_config();
		_contextProvider = contextProvider;
		_dilation = c.get_motionfilter_dilation();
		_edgeThreshold = c.get_motionfilter_edge_threshold();
		_videowidth = c.get_video_width();
		_videoheight = c.get_video_height();
		_screenheight = c.get_screen_height();
		_screenwidth = c.get_screen_width();
		_applet = applet;
		_sourceRepo = sourceRepo;

		IWindowedImageSource s = _sourceRepo.get_source();

		_flob = new Flob(s.get_width(), s.get_height(), s.get_width(), s
				.get_height());
		_blobs = new ArrayList<ABlob>();

		_flob.setThresh(20).setSrcImage(0).setBlur(0).setOm(0).setFade(25)
				.setMirror(true, false);
		_flob.settrackedBlobLifeTime(600);

		_points = new ArrayList<Point>();
		_previousPoints = new ArrayList<Point>();
		_lines = new ArrayList<Line>();
	}

	public ArrayList<Point> get_targets()
	{
		return _points;
	}

	public void draw()
	{
		IWindowedImageSource source = _sourceRepo.get_source();
		_cur_image = source.get_current_image();

		_diff_image = _applet.createImage(source.get_width(), source
				.get_height(), PConstants.ARGB);
		_diff_image.copy(source.diff(), 0, 0, _cur_image.width,
				_cur_image.height, 0, 0, _cur_image.width, _cur_image.height);
		_diff_image.filter(PConstants.THRESHOLD, _edgeThreshold / 10f);
		_diff_image.filter(PConstants.ERODE);
		for (int i = 1; i < _dilation; i++)
		{
			_diff_image.filter(PConstants.DILATE);
		}

		if (_contextProvider.get_context().debug)
		{
			_applet.tint(255, 128);
			_applet.image(_cur_image, 1, 1);
			_applet.image(_diff_image, 1, _videoheight);
			_applet.fill(0, 0, 255);
			_applet
					.text(
							String
									.format(
											"edge threshold = %d | dilation = %d\ndistance = %d | maxage = %d",
											_edgeThreshold, _dilation,
											_distance, _maxAge), 10, 10);
		}

		extractBlobs();

		deleteOldBlobs();
		
		_lines.clear();
		Iterator<Point> i = _points.iterator();
		while (i.hasNext())
		{

			Point p = i.next();
			Iterator<Point> prevIt = _previousPoints.iterator();
			double minDistance = -1;
			Point found = new Point(0,0);
			while (prevIt.hasNext())
			{
				Point prev = prevIt.next();
				double currentDistance = prev.CalcDistance(p);
				if (currentDistance < _distance && (minDistance == -1 || currentDistance < minDistance))
				{
					minDistance = currentDistance;
					found = prev;					
				}
			}
			
			if (minDistance > 0) {
				_previousPoints.remove(found);
				_lines.add(new Line(p, found));
			}

		
		}
	}

	private void deleteOldBlobs() {

		ArrayList<Point> toDelete = new ArrayList<Point>();
		Iterator<Point> prevIt = _previousPoints.iterator();
		Point prev;
		while (prevIt.hasNext())
		{
			prev = prevIt.next();
			
			if (prev.GetAge() > _maxAge) toDelete.add(prev);
		}
		

		_previousPoints.removeAll(toDelete);

	}

	private void extractBlobs()
	{
		_blobs = _flob.calc(_diff_image);
		int numblobs = _blobs.size();
		_previousPoints.addAll(_points);
		_points = new ArrayList<Point>(numblobs);

		if (_contextProvider.get_context().debug)
			BasePApplet
					.println("*** BLOBS FOUND: ********************************");
		for (int i = 0; i < numblobs; i++)
		{
			ABlob ab = (ABlob) _flob.getABlob(i);

			if (_contextProvider.get_context().debug)
			{
				String txt = "id: " + ab.id;
				_applet.strokeWeight(1);
				_applet.stroke(0, 255, 0);
				_applet.rectMode(BasePApplet.CENTER);
				_applet.fill(220, 220, 255, 100);
				_applet.rect(ab.boxcenterx, ab.boxcentery, ab.dimx, ab.dimy);
				_applet.fill(0);
				_applet.text(txt, ab.cx - ab.dimx * 0.10f, ab.cy + 5f);

				BasePApplet.println("ID " + ab.id + " (" + ab.boxcenterx + ", "
						+ ab.boxcentery + ")");

			}

			_points.add(new Point(
					Math.round((ab.boxcenterx * 1.0f / _videowidth)
							* _screenwidth), Math
							.round((ab.boxcentery * 1.0f / _videoheight)
									* _screenheight)).set_id(ab.id));
		}

	}

	public void keyPressed()
	{
		if (_applet.key == ruben.common.keyboard.KeyConvertor.get_char("a"))
		{
			_edgeThreshold = Math.max(0, _edgeThreshold - 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("e"))
		{
			_edgeThreshold = Math.min(10, _edgeThreshold + 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("q"))
		{
			_dilation = Math.max(1, _dilation - 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("d"))
		{
			_dilation = Math.min(100, _dilation + 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("w"))
		{
			_distance = Math.max(1, _distance - 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("c"))
		{
			_distance = Math.min(1000, _distance + 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("&"))
		{
			_maxAge = Math.max(1, _maxAge - 1);
		}
		else if (_applet.key == ruben.common.keyboard.KeyConvertor
				.get_char("\""))
		{
			_maxAge = Math.min(1000, _maxAge + 1);
		}

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

	public ArrayList<Line> get_linetargets()
	{
		return _lines;
	}

}
