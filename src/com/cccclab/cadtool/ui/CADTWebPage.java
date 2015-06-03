package com.cccclab.cadtool.ui;
import com.cccclab.cadtool.design.CADTWebPageDesign;
import com.cccclab.cadtool.event.CADTEventHandler;
import com.vaadin.data.Item;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;

@SuppressWarnings("serial")
public class CADTWebPage extends CADTWebPageDesign {
	// create a DragAndDropWrapper object
	private final DragAndDropWrapper wrap = new DragAndDropWrapper(tb_files);
	
	public CADTWebPage() {
		tb_files.setSizeFull();
		tb_files.setImmediate(true);
		tb_files.addContainerProperty("Select", CheckBox.class, null);
		tb_files.addContainerProperty("Name",  String.class, null);
		tb_files.addContainerProperty("Size (bytes)",  String.class, null);
		tb_files.setColumnExpandRatio("Name", 1.0f);
		tb_files.setColumnExpandRatio("Select", 0.0f);
		tb_files.setColumnExpandRatio("Size (bytes)", 0.0f);
		tb_files.setColumnAlignment("Select", Align.CENTER);
		
		tb_files.addActionHandler(CADTEventHandler.buildContextMenuHandler(tb_files));
		// table style generator
		tb_files.setCellStyleGenerator(new Table.CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				// Styling for row
				Item item = tb_files.getItem(itemId);
			    CheckBox chk_select = (CheckBox) item.getItemProperty("Select").getValue();
			    if(chk_select.getValue()) {
			    	// use style named .v-table-row-selected
			    	return "selected";
			    }
			    else {
			    	return null;
			    }
			}
		});
	
		tb_files.addColumnResizeListener(CADTEventHandler.buildTableColumnResizeHandler(tb_files));
		tb_files.addItemClickListener(CADTEventHandler.buildTableClickHandler());
		
		wrap.setImmediate(true);
		wrap.setSizeFull();
		wrap.setDropHandler(CADTEventHandler.buildFileDropHandler(tb_files));
		
		btn_download.addClickListener(CADTEventHandler.buildButtonDownloadHandler());
		btn_send.addClickListener(CADTEventHandler.buildButtonDownloadHandler());
		
		// add DragAndDropWrapper into page
		addComponent(wrap);
		setExpandRatio(wrap, 1.0f);
	}
}
