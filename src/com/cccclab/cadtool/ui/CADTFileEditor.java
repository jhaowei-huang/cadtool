package com.cccclab.cadtool.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.cccclab.cadtool.design.CADTFileEditorDesign;
import com.cccclab.cadtool.event.CADTEventHandler;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CADTFileEditor extends CADTFileEditorDesign {
	private Window window;
	public CADTFileEditor(Html5File file) {
		this.window = buildWindow(file.getFileName());
		
		editor_code.setImmediate(true);
		editor_code.setWidth("100.0%");
		editor_code.setHeight("100.0%");
		editor_code.setValue(file.getStreamVariable().getOutputStream().toString());
		editor_code.setMode(AceMode.java);
		editor_code.setTheme(AceTheme.merbivore_soft);

//		btn_save.addClickListener(CADTEventHandler.buildButtonSaveHandler(btn_save, editor_code, file));
		btn_cancle.addClickListener(CADTEventHandler.buildButtonCancleHandler(btn_cancle, window));
		
		StreamResource resource = createResource();
		FileDownloader fd = new FileDownloader(resource);
		fd.extend(btn_save);
	}

	private Window buildWindow(String caption) {
		Window window = new Window(caption);
		window.setImmediate(true);
		window.setWidth("100%");
		window.setHeight("100%");
		window.setResizable(false);
		window.setDraggable(false);
		window.setContent(this);
		window.center();
		return window;
	}
	
	public void show() {
		UI.getCurrent().addWindow(this.window);
	}
	
	private StreamResource createResource() {
        return new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
            	return new ByteArrayInputStream(editor_code.getValue().getBytes());
            }
        }, window.getCaption());
    }
}
