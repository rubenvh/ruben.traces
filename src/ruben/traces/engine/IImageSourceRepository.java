package ruben.traces.engine;

import ruben.common.processing.video.IWindowedImageSource;

public interface IImageSourceRepository
{
	IWindowedImageSource get_source();
}
