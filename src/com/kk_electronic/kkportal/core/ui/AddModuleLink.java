package com.kk_electronic.kkportal.core.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;
import com.kk_electronic.kkportal.core.event.TabSelectedEvent;
import com.kk_electronic.kkportal.core.tabs.TabInfo;
import com.kk_electronic.kkportal.core.tabs.TabsModel;

public class AddModuleLink extends Composite implements TabSelectedEvent.Handler {
	
	Hyperlink hyperlink = new Hyperlink();
	@Inject
	public AddModuleLink(TabsModel tabsModel) {
		initWidget(hyperlink);
		updateLink(tabsModel.getSelectedTab());
		tabsModel.addTabSelectedHandler(this);
	}

	public void updateLink(TabInfo tabInfo) {
		hyperlink.setHTML(tabInfo!=null?"Add Module":"");
		if(tabInfo != null) hyperlink.setTargetHistoryToken("AddModule$"+tabInfo.getId());
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		updateLink(event.getTabInfo());
	}
}
