package de.urkallinger.restclient.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import de.urkallinger.restclient.model.ConsoleEntry;
import javafx.application.Platform;
import javafx.scene.control.ListView;

@Plugin(name = "ListViewAppender", category = "Core", elementType = "appender", printObject = true)
public class ListViewAppender extends AbstractAppender {

    private static volatile List<ListView<ConsoleEntry>> listViews = new ArrayList<>();

    private int maxLines = 0;

    protected ListViewAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        this.maxLines = maxLines;
    }

    @PluginFactory
    public static ListViewAppender createAppender(@PluginAttribute("name") String name,
    											  @PluginAttribute("maxLines") int maxLines,
    											  @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
    											  @PluginElement("Layout") Layout<?> layout,
    											  @PluginElement("Filters") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for ListViewAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new ListViewAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    // Add the target ListView to be populated and updated by the logging information.
    public static void addListView(final ListView<ConsoleEntry> listView) {
    	ListViewAppender.listViews.add(listView);
    }

    @Override
    public void append(LogEvent event) {
        final String message = new String(this.getLayout().toByteArray(event));
        final Level level = event.getLevel();

        // Append formatted message to text area using the Thread.
        try {
        	Platform.runLater(() -> {
        		for (ListView<ConsoleEntry> lv : listViews){
        			try {
        				if (lv != null) {
        					lv.getItems().add(new ConsoleEntry(message.trim(), level));

        					if(maxLines > 0 && lv.getItems().size() > maxLines + 1) {
        						lv.getItems().remove(0);
        					}
        				}
        			} catch (final Throwable t) {
        				System.out.println("Unable to append log to listview: "
        						+ t.getMessage());
        			}
        		}
        	});
        } catch (final Exception e) {
            // ignore case when the platform hasn't yet been iniitialized
        }
    }
}