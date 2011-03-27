package ruben.traces.context;

import ruben.common.configuration.ConfigurationFile;
import ruben.common.configuration.IConfigurationFile;
import ruben.common.processing.applet.BasePApplet;

public class TraceConfiguration implements ITraceConfiguration
{

	private IConfigurationFile _config;
	
	public TraceConfiguration(BasePApplet applet, String filename)
	{
		_config = new ConfigurationFile(applet.loadStrings(filename));
	}
	
	public int get_background()
	{
		return Integer.parseInt(_config.get("BACKGROUND"));
	}

	public int get_camera_number()
	{
		return Integer.parseInt(_config.get("CAMERA"));
	}

	public int get_fade_decay1()
	{
		return Integer.parseInt(_config.get("FADE.DECAY1"));
	}

	public int get_fade_to()
	{
		return Integer.parseInt(_config.get("FADE.TO"));
	}

	public int get_fade_decay2()
	{
		return Integer.parseInt(_config.get("FADE.DECAY2"));
	}

	public int get_foreground()
	{
		return Integer.parseInt(_config.get("FOREGROUND"));
	}

	public int get_line_weight()
	{
		return Integer.parseInt(_config.get("LINE.WEIGHT"));
	}

	public int get_motionfilter_dilation()
	{
		return Integer.parseInt(_config.get("MOTIONFILTER.DILATION"));
	}

	public int get_motionfilter_edge_threshold()
	{
		return Integer.parseInt(_config.get("MOTIONFILTER.EDGETHRESHOLD"));
	}

	public String get_movie_file()
	{
		return _config.get("MOVIEFILE");
	}

	public int get_screen_height()
	{
		return Integer.parseInt(_config.get("SCREEN.HEIGHT"));
	}

	public int get_screen_width()
	{
		return Integer.parseInt(_config.get("SCREEN.WIDTH"));
	}

	public boolean get_use_camera()
	{
		return _config.get("USECAMERA").compareTo("true")==0;
	}

	public int get_video_height()
	{
		return Integer.parseInt(_config.get("VIDEO.HEIGHT"));
	}

	public int get_video_width()
	{
		return Integer.parseInt(_config.get("VIDEO.WIDTH"));
	}

	public int get_fade_decay3()
	{
		return Integer.parseInt(_config.get("FADE.DECAY3"));
	}

	public int get_fade_to2()
	{
		return Integer.parseInt(_config.get("FADE.TO2"));
	}

}
