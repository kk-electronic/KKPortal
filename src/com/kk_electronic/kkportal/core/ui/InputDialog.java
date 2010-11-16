package com.kk_electronic.kkportal.core.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class InputDialog {

	public static interface UIBinder extends UiBinder<Widget, InputDialog>{};
	public static interface Handler {
		public void onInput(String input);
	}
	
	private Widget widget;
	private Handler handler;

	@Inject
	public InputDialog(UIBinder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void show() {
		if(showing) return;
		showing = true;
		final RootLayoutPanel r = RootLayoutPanel.get();
		DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				if(r.getWidgetIndex(widget) == -1){
					r.add(widget);
					username.setFocus(true);
				}
			}
		});
	}

	@UiField TextBox username;
	
	private void addIdentity(){
		String username = this.username.getValue();
		if(!username.isEmpty()){
			hide();
			if(handler != null){
				handler.onInput(username);
			}
		} else {
			this.username.setFocus(true);
		}
	}
	
	private void hide() {
		if(!showing) return;
		RootLayoutPanel.get().remove(widget);
		showing = false;
	}

	@UiHandler("username")
	public void onKeyPress(KeyDownEvent event){
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
			addIdentity();
		}
	}
	
	@UiHandler("submit")
	public void onSubmit(ClickEvent event){
		addIdentity();
	}

	@UiField 
	Element text;
	private boolean showing;
	public void setText(String string) {
		text.setInnerText(string);
	}

	public boolean isShowing() {
		return showing;
	}

	public void setValue(String value) {
		username.setText(value);
	}
}
