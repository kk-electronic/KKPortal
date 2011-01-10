package com.kk_electronic.kkportal.debug.modules;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.AbstractModule;
import com.kk_electronic.kkportal.debug.model.CpuUsage;

/**
 * This Module provides a smooth graph by animating the steps between the data points.
 * We use a HTML5 Canvas to display the values using a simple path with no interpolation.
 * @author Jes Andersen
 */
public class UsageGraph extends AbstractModule implements HasData<Double>{
	GWTCanvas canvas = new GWTCanvas();
	private List<Double> values;
	
	/*
	 * This class was used for testing before the back-end was implemented
	 */
	public static class RandomWalk{
		List<Double> l = new LinkedList<Double>();
		List<HasData<Double>> displays = new LinkedList<HasData<Double>>();
		public static final double MAX = 1.0;
		public static final double MIN = 0.0;
		public static final double STEP = 0.05;
		private double value = (MAX+MIN)/2;
		Timer t = new Timer() {
			@Override
			public void run() {
				step();
			}
		};
		protected void step() {
			value = value - STEP + 2 * STEP * Random.nextDouble();
			value = Math.min(MAX,Math.max(MIN, value));
			l.add(value);
			if(l.size() > 60){
				l.remove(0);
			}
			updateDisplays(l);
		}
		public RandomWalk() {
			t.scheduleRepeating(1000);
		}
		public void addDisplay(HasData<Double> display) {
			displays.add(display);
		}
		private void updateDisplays(List<Double> values){
			for(HasData<Double> display:displays){
				display.setRowData(0, values);
			}
		}
	}
	
	/*
	 * This timer calls drawPath every time it is triggered.
	 */
	Timer timer = new Timer() {
		
		@Override
		public void run() {
			drawPath(values,(Duration.currentTimeMillis()-offset)/1000.0);
		}
	};
	private double offset;

	/*
	 * The constructor has two responsibilities.
	 * Add the module as a display of the CpuUsage
	 * Start the timer that updates graph
	 */
	@Inject
	public UsageGraph(CpuUsage model) {
		model.addDisplay(this);
		canvas.setLineWidth(1);
	    canvas.setStrokeStyle(Color.BLACK);
	    canvas.getElement().getStyle().setBorderWidth(1, Unit.PX);
	    canvas.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
	    canvas.getElement().getStyle().setBorderColor("black");
	    
	    canvas.beginPath();
	      canvas.moveTo(1,1);
	      canvas.lineTo(1,50);
	      canvas.lineTo(50,50);
	      canvas.lineTo(50, 1);
	      canvas.closePath();
	    canvas.stroke();
	    timer.scheduleRepeating(100);
	}
	
	/*
	 * We use the IsWidget interface rather than inheriting from Widget since the latter
	 * would be expensive in unit tests of the class
	 * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
	 */
	@Override
	public Widget asWidget() {
		return canvas;
	}

	/*
	 * When the model updates the data it calls setRow Data
	 * @see com.google.gwt.view.client.HasData#setRowData(int, java.util.List)
	 */
	@Override
	public void setRowData(int start, List<Double> values) {
		
		offset = Duration.currentTimeMillis();
		this.values = values;
	}

	/*
	 * This functions draws the data on the canvas with a potential shift to the left. It will
	 * slowly move the data points to the left while we are waiting for new data creating a smooth
	 * update of the graph.
	 * The constructor will ensure this function gets called every 0.1 seconds
	 */
	private void drawPath(List<Double> values, double shift) {
		if(values == null || values.isEmpty()) return;
		canvas.clear();
	    canvas.beginPath();
	    double pixelpersecond = canvas.getCoordWidth()/57.0; 
	    int o = Math.max(60-values.size(),0); // How many missing values
	    canvas.moveTo((o-shift*2-1)*pixelpersecond, canvas.getCoordHeight()*values.get(0));
//	    canvas.moveTo((o)*pixelpersecond, canvas.getCoordHeight()*values.get(0));
	    for(int i=1,l=values.size();i<l;i++){
			double x = (i+o-shift*2-1)*pixelpersecond;
//	    	double x = (i+o)*pixelpersecond;
			double y = canvas.getCoordHeight()*(1-values.get(i));
			canvas.lineTo(x,y);
		}
		canvas.stroke();
	}

	/*
	 * All the functions below are null implementations needed the the GWT HasData interface
	 * We should perhaps use a more simple interface for simple data models. 
	 * @see com.google.gwt.view.client.HasData#getSelectionModel()
	 */
	@Override
	public SelectionModel<? super Double> getSelectionModel() {
		// TODO Auto-generated method stub
		return null;
	}	
	@Override
	public void setSelectionModel(SelectionModel<? super Double> selectionModel) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setVisibleRangeAndClearData(Range range,
			boolean forceRangeChangeEvent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Range getVisibleRange() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isRowCountExact() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setRowCount(int count) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setRowCount(int count, boolean isExact) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setVisibleRange(Range range) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setVisibleRange(int start, int length) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}
}
