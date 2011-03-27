package ruben.traces.engine;

import java.util.ArrayList;
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
	ArrayList<Point> _points;
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
			_applet.text(String.format("edge threshold = %d | dilation = %d",
					_edgeThreshold, _dilation), 10, 10);
		}

		fillTargets1();

	}

	private void fillTargets2()
	{
		_tBlobs = _flob.track(_diff_image);
		int numblobs = _tBlobs.size();

		_lines = new ArrayList<Line>(numblobs);
		for (int i = 0; i < numblobs; i++)
		{
			trackedBlob ab = (trackedBlob) _flob.getTrackedBlob(i);
			if (_flob.imageblobs.prevtrackedblobs.size() > 0)
			{
				trackedBlob pab = (trackedBlob) _flob.getPreviousTrackedBlob(i);

				if (_contextProvider.get_context().debug)
				{
					String txt = "id: " + ab.id;
					_applet.strokeWeight(1);
					_applet.stroke(0, 255, 0);
					_applet.fill(220, 220, 255, 100);
					_applet.rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
					_applet.fill(0, 255, 0, 200);
					_applet.rect(ab.cx, ab.cy, 5, 5);
					_applet.fill(0);
					_applet.text(txt, ab.cx - ab.dimx * 0.10f, ab.cy + 5f);
				}

				_lines.add(new Line(new Point(Math
						.round((ab.boxcenterx * 1.0f / _videowidth)
								* _screenwidth), Math
						.round((ab.boxcentery * 1.0f / _videoheight)
								* _screenheight)), new Point(Math
						.round((pab.boxcenterx * 1.0f / _videowidth)
								* _screenwidth), Math
						.round((pab.boxcentery * 1.0f / _videoheight)
								* _screenheight))));
			}
		}

	}

	private void fillTargets1()
	{
		_blobs = _flob.calc(_diff_image);
		int numblobs = _blobs.size();

		_points = new ArrayList<Point>(numblobs);
		for (int i = 0; i < numblobs; i++)
		{
			ABlob ab = (ABlob) _flob.getABlob(i);
			
			if (_contextProvider.get_context().debug)
			{
				String txt = "id: " + ab.id;
				_applet.strokeWeight(1);
				_applet.stroke(0, 255, 0);
				_applet.fill(220, 220, 255, 100);
				_applet.rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
				_applet.fill(0, 255, 0, 200);
				_applet.rect(ab.cx, ab.cy, 5, 5);
				_applet.fill(0);
				_applet.text(txt, ab.cx - ab.dimx * 0.10f, ab.cy + 5f);
			}

			_points.add(new Point(
					Math.round((ab.boxcenterx * 1.0f / _videowidth)
							* _screenwidth), Math
							.round((ab.boxcentery * 1.0f / _videoheight)
									* _screenheight)));
		}
		
		for (int i = 0; i < _flob.imageblobs.prevnumblobs; i++)
		{
			ABlob ab = (ABlob) _flob.getPreviousABlob(i);
		
			if (_contextProvider.get_context().debug)
			{
				String txt = "id: " + ab.id;
				_applet.strokeWeight(1);
				_applet.stroke(0, 255, 0);
				_applet.fill(10, 10, 10, 100);
				_applet.rect(ab.cx, ab.cy, ab.dimx, ab.dimy);
				_applet.fill(0, 255, 0, 200);
				_applet.rect(ab.cx, ab.cy, 5, 5);
				_applet.fill(0);
				_applet.text(txt, ab.cx - ab.dimx * 0.10f, ab.cy + 5f);
			}

			
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
