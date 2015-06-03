package com.cccclab.cadtool.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.vaadin.aceeditor.AceEditor;

import com.cccclab.cadtool.ui.CADTFileEditor;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.ColumnResizeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CADTEventHandler {
	private static String REPOSITORY = " ";
	public static ArrayList<Html5File> files = new ArrayList<Html5File>();
	// keep table columns fixed size
	public static ColumnResizeListener buildTableColumnResizeHandler(Table table) {
		return new ColumnResizeListener() {
			@Override
			public void columnResize(ColumnResizeEvent event) {
				// table must set immediate true
				table.setColumnWidth("Select", -1);
				table.setColumnWidth("Name", -1);
				table.setColumnWidth("Size (bytes)", -1);
				table.setWidth("100%");
			}
		};
	}
	// click on the table
	public static ItemClickListener buildTableClickHandler() {
		return new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				Table table = (Table) event.getSource();
				if(event.isDoubleClick()) {
					Item item = table.getItem(event.getItemId());
					String fileName = (String) item.getItemProperty("Name").getValue();
					for(Html5File tmp : files) {
						if(tmp.getFileName().equals(fileName)) {
							CADTFileEditor eidtor = new CADTFileEditor(tmp);
							eidtor.show();
							break;
						}
					}
					table.refreshRowCache();
				}
				else if(event.getButton() == MouseButton.LEFT) {
					Item item = table.getItem(event.getItemId());
					CheckBox chk = (CheckBox) item.getItemProperty("Select").getValue();
					chk.setValue(!chk.getValue());
					table.refreshRowCache();
				}
			}
		};
	}
	// drag and drop to the table area
	public static DropHandler buildFileDropHandler(Table table) {
		return new DropHandler() {
			@Override
			public void drop(DragAndDropEvent event) {
				// drop event transfer to Html5File
				WrapperTransferable transferred = (WrapperTransferable) event.getTransferable();
				Html5File files[] = transferred.getFiles();
				
	            if(files != null) {
	            	for(final Html5File file : files) {
	            		// add column property name
	            		Item row = table.getItem(table.addItem());
	            		row.getItemProperty("Select").setValue(new CheckBox());
	            		row.getItemProperty("Name").setValue(file.getFileName());
	            		row.getItemProperty("Size (bytes)").setValue(String.valueOf(file.getFileSize()));
	            		
	            		StreamVariable streamVariable = createStreamVariable(file);
	            		/*
	            		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            		StreamVariable streamVariable = new StreamVariable() {

	            			@Override
	            			public OutputStream getOutputStream() {
	            				return outputStream;
	            			}

	            			@Override
	            			public boolean listenProgress() {
	            				return false;
	            			}

	            			@Override
	            			public void onProgress(StreamingProgressEvent event) {
	            			}

	            			@Override
	            			public void streamingStarted(StreamingStartEvent event) {
	            			}

	            			@Override
	            			public void streamingFinished(StreamingEndEvent event) {
	            				try {
	            					FileOutputStream fos = new FileOutputStream(REPOSITORY + file.getFileName());
	                                outputStream.writeTo(fos);
	                            } 
	            				catch (IOException e) {
	            					Notification.show("Streaming finished failed", Type.ERROR_MESSAGE);
	            				}
	            			}

	            			@Override
	            			public void streamingFailed(StreamingErrorEvent event) {
	            				Notification.show("streaming failed.", Type.ERROR_MESSAGE);
	            			}

	            			@Override
	            			public boolean isInterrupted() {
	            				return false;
	            			}
	                    };*/
	                    // streamVariable is the content of file
	            		file.setStreamVariable(streamVariable);
	            		CADTEventHandler.files.add(file);
	            	}
	            	
	            	table.setPageLength(table.size());
	            } 
	            else {
	            	Notification.show("Unsupported object", Type.ERROR_MESSAGE);
            	}
			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}
		};
	}
	
	private static StreamVariable createStreamVariable(Html5File file) {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		return new StreamVariable() {

			@Override
			public OutputStream getOutputStream() {
				return outputStream;
			}

			@Override
			public boolean listenProgress() {
				return false;
			}

			@Override
			public void onProgress(StreamingProgressEvent event) {
			}

			@Override
			public void streamingStarted(StreamingStartEvent event) {
			}

			@Override
			public void streamingFinished(StreamingEndEvent event) {
				try {
					FileOutputStream fos = new FileOutputStream(REPOSITORY + file.getFileName());
                    outputStream.writeTo(fos);
                } 
				catch (IOException e) {
					e.printStackTrace();
					Notification.show("Streaming finished failed", Type.ERROR_MESSAGE);
				}
			}

			@Override
			public void streamingFailed(StreamingErrorEvent event) {
				Notification.show("streaming failed.", Type.ERROR_MESSAGE);
			}

			@Override
			public boolean isInterrupted() {
				return false;
			}
        }; 
	}
	
	public static Action.Handler buildContextMenuHandler(Table table) {
		return new Action.Handler() {
			// context menu
			final Action actionEdit = new Action("Edit");
			final Action actionRemove = new Action("Remove");
			final Action actionSelectAll = new Action("Select ALL");
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {	
				if(action == actionEdit && target != null) {
					Item item = table.getItem(target);
					String fileName = (String) item.getItemProperty("Name").getValue();
					for(Html5File tmp : files) {
						if(tmp.getFileName().equals(fileName)) {
							CADTFileEditor eidtor = new CADTFileEditor(tmp);
							eidtor.show();
							break;
						}
					}
				}
				else if(action == actionRemove) {
					/* 
					 * remove specified item on table	
					 * 		
					Item item = table.getItem(target);
					String fileName = (String) item.getItemProperty("Name").getValue();
					table.removeItem(target);
					for(Html5File tmp : files) {
						if(tmp.getFileName().equals(fileName)) {
							files.remove(tmp);
							break;
						}
					} 
					*
					*
					*/
					ArrayList<Object> toDelete = new ArrayList<Object>();
					for(Iterator<?> i = table.getItemIds().iterator(); i.hasNext();) {
						int currentId = (Integer) i.next();
						Item item = table.getItem(currentId);
						CheckBox chk = (CheckBox) item.getItemProperty("Select").getValue();
						if(chk.getValue())
							toDelete.add(currentId);
					}
					
					for(Object id : toDelete) {
						table.removeItem(id);
					}
					
					table.refreshRowCache();
				}
				else if(action == actionSelectAll) {
					for(Iterator<?> i = table.getItemIds().iterator(); i.hasNext();) {
						int currentId = (Integer) i.next();
						Item item = table.getItem(currentId);
						CheckBox chk = (CheckBox) item.getItemProperty("Select").getValue();
						chk.setValue(!chk.getValue());
					}
					
					table.refreshRowCache();
				}
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {		
				// show the actions on the context menu when you click right-button
				if(files.isEmpty())
					return null;
				else 
					return new Action[] { actionEdit, actionRemove, actionSelectAll };
			}
		};
	}

	public static Button.ClickListener buildButtonDownloadHandler() {	
		return new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(event.getButton().getCaption() + " clicked!");
			}
		};
	}
	// transfer code area data into stream resource that can download
	private static StreamResource createResource(String value, String fileName) {
        return new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
            	return new ByteArrayInputStream(value.getBytes());
            }
        }, fileName);
    }
	
	public static Button.ClickListener buildButtonCancleHandler(Button btn_cancle, Window w) {
		return new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				w.close();
			}
		};
	}
}