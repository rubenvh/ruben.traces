package ruben.traces.engine;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ruben.common.drawing.IDrawingSystem;
import ruben.common.drawing.MaterializingVisitor;
import ruben.common.processing.applet.BaseAppletDrawer;
import ruben.common.processing.applet.BasePApplet;
import ruben.traces.context.IConfigProvider;

public class MaterializingAppletDrawer extends BaseAppletDrawer
{
	BasePApplet _applet;
	IDrawingSystem _drawingSystem;
	File _directoryToWriteTo;
	
	public MaterializingAppletDrawer(BasePApplet applet, IConfigProvider configProvider,IDrawingSystem drawingSystem) {
		
		_applet = applet;
		_drawingSystem = drawingSystem;
		String outputPath = configProvider.get_config().get_output_path();
		
		
	   _directoryToWriteTo = new File(outputPath, get_current_date());	
		
		if (!_directoryToWriteTo.exists()) _directoryToWriteTo.mkdirs();
		
		BasePApplet.println("Saving frames to: "+_directoryToWriteTo.getAbsolutePath());
	}
	
	public void draw()
	{
		
		if ((_applet.frameCount % 100) == 0){
			
			String file  =get_new_file(".tif").getAbsolutePath();
			BasePApplet.println("Saving frame to: " + file);
			
			_applet.saveFrame(file);
			
			// materialize to xml file
			MaterializingVisitor v = new MaterializingVisitor();
			_drawingSystem.GetGraphics().Accept(v);
			v.print(get_new_file(".xml"));
			
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
	
	private String get_current_date() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
	
	private File get_new_file(String extension) {
		
		return (new File(_directoryToWriteTo, get_current_date()+extension));
	}

	public void cleanup()
	{
		// TODO Auto-generated method stub
		
	}

}
