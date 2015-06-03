package com.cccclab.cadtool;

import javax.servlet.annotation.WebServlet;

import com.cccclab.cadtool.ui.CADTWebPage;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("cadtool")
public class CADToolUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = CADToolUI.class, widgetset = "com.cccclab.cadtool.widgetset.CadtoolWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		/*
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
		*/
		
		/*
		AceEditor ed = new AceEditor();
		ed.setValue("Hello World");
		ed.setMode(AceMode.verilog);
		ed.setTheme(AceTheme.chrome);
		setContent(ed);
		*/
		
		CADTWebPage page = new CADTWebPage();
		setContent(page);
	}

}