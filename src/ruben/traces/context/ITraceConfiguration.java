package ruben.traces.context;

public interface ITraceConfiguration
{
	int get_screen_width();
	int get_screen_height();
	int get_video_width();
	int get_video_height();
	boolean get_use_camera();
	int get_camera_number();
	String get_movie_file();
	int get_fade_decay1();
	int get_fade_decay2();
	int get_fade_decay3();
	int get_fade_to();
	int get_fade_to2();
	int get_background();
	int get_foreground();
	int get_line_weight();
	int get_motionfilter_edge_threshold();
	int get_motionfilter_dilation();

}
