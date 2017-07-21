/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import ChartDirector.Chart;
import ChartDirector.ChartViewer;
import ChartDirector.DrawArea;
import ChartDirector.Layer;
import ChartDirector.LineLayer;
import ChartDirector.PlotArea;
import ChartDirector.TTFText;
import ChartDirector.TrackCursorAdapter;
import ChartDirector.ViewPortAdapter;
import ChartDirector.ViewPortChangedEvent;
import ChartDirector.ViewPortControl;
import ChartDirector.XYChart;
import datamodel.SingleFileCurveSets;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import tools.utilities.Logs;

/**
 *
 * @author JianGe
 */
public class CurvePloter extends JPanel {
    // Data arrays for the scrollable / zoomable chart.

    private double[] xAxis;
    private double[][] dataSeries;

    // The earliest date and the duration in seconds for horizontal scrolling
    private Date minDate;
    private double dateRange;

    // The vertical range of the chart for vertical scrolling
    private double maxValue;
    private double minValue;

    // The current visible duration of the view port in seconds
    private double currentDuration = 360 * 86400;
    // In this demo, the maximum zoom-in is set to 10 days
    private double minDuration = 10 * 86400;

    // This flag is used to suppress event handlers before complete initialization
    private boolean hasFinishedInitialization;

    private String chartTitle;

    //
    // Controls
    //
    private ChartViewer chartViewer1;
    private JFileChooser saveDialog;

    /**
     * Constructor
     *
     * @param preferSize
     * @param title
     * @param singleFileCurveSets
     */
    public CurvePloter(Dimension preferSize, String title, SingleFileCurveSets singleFileCurveSets) {
        // Use DISPOSE_ON_CLOSE to avoid mmeory leak, and set dialog to modal and non-resizable

        // init pama
        if (title == null || title.isEmpty()) {
            this.chartTitle = "   Zooming and Scrolling with Viewport Control";
        } else {
            this.chartTitle = title;
        }

        this.setPreferredSize(preferSize);

        // Chart Viewer
        chartViewer1 = new ChartViewer();
        chartViewer1.setBackground(Color.WHITE);
        chartViewer1.setOpaque(true);
        chartViewer1.setPreferredSize(this.getPreferredSize());
        chartViewer1.setHorizontalAlignment(SwingConstants.CENTER);
        chartViewer1.setHotSpotCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chartViewer1.addViewPortListener(new ViewPortAdapter() {
            @Override
            public void viewPortChanged(ViewPortChangedEvent e) {
                chartViewer1_ViewPortChanged(e);
            }
        });
        chartViewer1.addTrackCursorListener(new TrackCursorAdapter() {
            @Override
            public void mouseMovedPlotArea(MouseEvent e) {
                chartViewer1_MouseMovedPlotArea(e);
            }
        });

        // ViewPortControl
        ViewPortControl vpControl = new ViewPortControl();
        vpControl.setPreferredSize(this.getPreferredSize());
        vpControl.setHorizontalAlignment(SwingConstants.CENTER);

        // Put the ChartViewer and the scroll bars in the right panel
        JPanel plotPane = new JPanel(null);
        plotPane.setBackground(Color.WHITE);
        plotPane.add(chartViewer1).setBounds(0, 0, this.getPreferredSize().width, this.getPreferredSize().height - 100);
        plotPane.add(vpControl).setBounds(0, this.getPreferredSize().height - 95, this.getPreferredSize().width, 100);
        plotPane.setPreferredSize(this.getPreferredSize());

        this.add(plotPane);
        //
        // At this point, the user interface layout has been completed. 
        // Can load data and plot chart now.
        //
        // Load the data
        loadData(singleFileCurveSets);

        // Initialize the ChartViewer
        initChartViewer(chartViewer1);

        // It is safe to handle events now.
        hasFinishedInitialization = true;

        // Trigger a view port update to draw chart.
        chartViewer1.updateViewPort(true, true);

        // Draw the full thumbnail chart for the ViewPortControl
        drawFullChart(vpControl, chartViewer1);
        // Bind the ChartViewer to the ViewPortControl
        vpControl.setViewer(chartViewer1);
    }

    //
    // Load the data
    //
    private void loadData(SingleFileCurveSets singleFileCurveSets) {
        // In this example, we just use random numbers as data.

        int curveCnt = singleFileCurveSets.cuvreNumber();
        int sampleCnt = singleFileCurveSets.getCurveInfoList().get(0).sampleCount();
        xAxis = new double[sampleCnt];

        for (int i = 0; i < sampleCnt; i++) {
            xAxis[i] = i;
        }

        dataSeries = new double[curveCnt][sampleCnt];
        Logs.e("curveCnt: " + curveCnt + ", sampleCnt: " + sampleCnt);
        for (int idx = 1; idx < singleFileCurveSets.cuvreNumber(); idx++) {
            dataSeries[idx] = (singleFileCurveSets.getCurveInfoList().get(idx).getCurveValueList().getArrayValues());
            //            

        }
    }

    //
    // Initialize the WinChartViewer
    //
    private void initChartViewer(ChartViewer viewer) {
        // Set the full x range to be the duration of the data
//        viewer.setFullRange("x", timeStamps[0], timeStamps[timeStamps.length - 1]);
        viewer.setFullRange("x", xAxis[0], xAxis[xAxis.length - 1]);

        // Initialize the view port to show the latest 20% of the time range
        viewer.setViewPortWidth(0.2);
        viewer.setViewPortLeft(1 - viewer.getViewPortWidth());

        // Set the maximum zoom to 10 points
//        viewer.setZoomInWidthLimit(10.0 / timeStamps.length);
        viewer.setZoomInWidthLimit(10.0 / xAxis.length);

        // Enable mouse wheel zooming by setting the zoom ratio to 1.1 per wheel event
        viewer.setMouseWheelZoomRatio(1.1);

        // Initially set the mouse usage to "Pointer" mode (Drag to Scroll mode)
    }

    //
    // The ViewPortChanged event handler. This event occurs if the user scrolls or zooms in
    // or out the chart by dragging or clicking on the chart. It can also be triggered by
    // calling WinChartViewer.updateViewPort.
    //
    private void chartViewer1_ViewPortChanged(ViewPortChangedEvent e) {
        // Update the chart if necessary
        if (e.needUpdateChart()) {
            drawChart(chartViewer1);
        }
    }

    //
    // Draw the chart.
    //
    private void drawChart(ChartViewer viewer) {
        // Get the start date and end date that are visible on the chart.

        Logs.e(" viewer.getValueAtViewPort: " + viewer.getValueAtViewPort("x", viewer.getViewPortLeft()) + ", viewer.getViewPortLeft(): " + viewer.getViewPortLeft());
        Logs.e("viewer.getViewPortWidth(): " + viewer.getViewPortWidth());
        double viewPortStart = (double) viewer.getValueAtViewPort("x", viewer.getViewPortLeft());
        double viewPortEnd = (double) viewer.getValueAtViewPort("x", viewer.getViewPortLeft() + viewer.getViewPortWidth());

        Logs.e("viewPortStart: " + viewPortStart + ", viewPortEnd: " + viewPortEnd);
        // Get the array indexes that corresponds to the visible start and end dates
        int startIndex = (int) Math.floor(Chart.bSearch(xAxis, viewPortStart));
        int endIndex = (int) Math.ceil(Chart.bSearch(xAxis, viewPortEnd));
        int noOfPoints = endIndex - startIndex + 1;
        Logs.e("startIndex: " + startIndex + ", endIndex: " + endIndex + ", noOfPoints: " + noOfPoints);

        // Extract the part of the data array that are visible.
        double[] viewPortTimeStamps = (double[]) Chart.arraySlice(xAxis, startIndex, noOfPoints);

        double[][] viewPortDataSeries = new double[dataSeries.length][];
        for (int i = 1; i < dataSeries.length; i++) {
            viewPortDataSeries[i] = (double[]) Chart.arraySlice(dataSeries[i], startIndex, noOfPoints);
        }

        //
        // At this stage, we have extracted the visible data. We can use those data to plot the chart.
        //
        //================================================================================
        // Configure overall chart appearance.
        //================================================================================
        // Create an XYChart object of size 640 x 400 pixels
        XYChart c = new XYChart(this.getPreferredSize().width, this.getPreferredSize().height - 100);

        // Set the plotarea at (55, 55) with width 80 pixels less than chart width, and height 90 pixels
        // less than chart height. Use a vertical gradient from light blue (f0f6ff) to sky blue (a0c0ff)
        // as background. Set border to transparent and grid lines to white (ffffff).
        c.setPlotArea(55, 55, c.getWidth() - 80, c.getHeight() - 90, c.linearGradientColor(0, 55, 0,
                c.getHeight() - 35, 0xf0f6ff, 0xa0c0ff), -1, Chart.Transparent, 0xffffff, 0xffffff);

        // As the data can lie outside the plotarea in a zoomed chart, we need enable clipping.
        c.setClipping();

        // Add a title to the chart using 15pt Arial Bold font
        c.addTitle(chartTitle, "Arial Bold", 15);

        // Set legend icon style to use line style icon, sized for 10pt font
        c.getLegend().setLineStyleKey();
        c.getLegend().setFontSize(10);

        // Set the x and y axis stems to transparent and the label font to 10pt Arial
        c.xAxis().setColors(Chart.Transparent);
        c.yAxis().setColors(Chart.Transparent);
        c.xAxis().setLabelStyle("Arial", 10);
        c.yAxis().setLabelStyle("Arial", 10);

        // Add axis title using 10pt Arial Bold font
        c.yAxis().setTitle("Ionic Temperature (C)", "Arial Bold", 10);

        //================================================================================
        // Add data to chart
        //================================================================================
        //
        // In this example, we represent the data by lines. You may modify the code below to use other
        // representations (areas, scatter plot, etc).
        //
        // Add a line layer for the lines, using a line width of 2 pixels
        LineLayer layer = c.addLineLayer2();
        layer.setLineWidth(2);

        // In this demo, we do not have too many data points. In real code, the chart may contain a lot
        // of data points when fully zoomed out - much more than the number of horizontal pixels in this
        // plot area. So it is a good idea to use fast line mode.
        layer.setFastLineMode();

        // Now we add the 3 data series to a line layer, using the color red (ff33333), green (008800)
        // and blue (3333cc)
        layer.setXData(viewPortTimeStamps);
        for (int i = 1; i < dataSeries.length; i++) {
            layer.addDataSet(viewPortDataSeries[i], 0xff3333, "Alpha");

        }
//        layer.addDataSet(viewPortDataSeriesA, 0xff3333, "Alpha");
//        layer.addDataSet(viewPortDataSeriesB, 0x008800, "Beta");
//        layer.addDataSet(viewPortDataSeriesC, 0x3333cc, "Gamma");

        //================================================================================
        // Configure axis scale and labelling
        //================================================================================
        // Set the x-axis as a date/time axis with the scale according to the view port x range.
        viewer.syncDateAxisWithViewPort("x", c.xAxis());

        // For the automatic y-axis labels, set the minimum spacing to 30 pixels.
        c.yAxis().setTickDensity(30);

        //
        // In this demo, the time range can be from a few years to a few days. We demonstrate how to set
        // up different date/time format based on the time range.
        //
        // If all ticks are yearly aligned, then we use "yyyy" as the label format.
        c.xAxis().setFormatCondition("align", 360 * 86400);
        c.xAxis().setLabelFormat("{value|yyyy}");

        // If all ticks are monthly aligned, then we use "mmm yyyy" in bold font as the first label of a
        // year, and "mmm" for other labels.
        c.xAxis().setFormatCondition("align", 30 * 86400);
        c.xAxis().setMultiFormat(Chart.StartOfYearFilter(), "<*font=bold*>{value|mmm<*br*>yyyy}",
                Chart.AllPassFilter(), "{value|mmm}");

        // If all ticks are daily algined, then we use "mmm dd<*br*>yyyy" in bold font as the first
        // label of a year, and "mmm dd" in bold font as the first label of a month, and "dd" for other
        // labels.
        c.xAxis().setFormatCondition("align", 86400);
        c.xAxis().setMultiFormat(Chart.StartOfYearFilter(),
                "<*block,halign=left*><*font=bold*>{value|mmm dd<*br*>yyyy}", Chart.StartOfMonthFilter(),
                "<*font=bold*>{value|mmm dd}");
        c.xAxis().setMultiFormat2(Chart.AllPassFilter(), "{value|dd}");

        // For all other cases (sub-daily ticks), use "hh:nn<*br*>mmm dd" for the first label of a day,
        // and "hh:nn" for other labels.
        c.xAxis().setFormatCondition("else");
        c.xAxis().setMultiFormat(Chart.StartOfDayFilter(), "<*font=bold*>{value|hh:nn<*br*>mmm dd}",
                Chart.AllPassFilter(), "{value|hh:nn}");

        //================================================================================
        // Output the chart
        //================================================================================
        // We need to update the track line too. If the mouse is moving on the chart (eg. if 
        // the user drags the mouse on the chart to scroll it), the track line will be updated
        // in the MouseMovePlotArea event. Otherwise, we need to update the track line here.
        if (!viewer.isInMouseMoveEvent()) {
            trackLineLegend(c, (null == viewer.getChart()) ? c.getPlotArea().getRightX()
                    : viewer.getPlotAreaMouseX());
        }

        viewer.setChart(c);
    }

    /**
     *
     *
     *
     *
     */
    private void drawFullChart(ViewPortControl vpc, ChartViewer viewer) {
        // Create an XYChart object of size 640 x 60 pixels   
        XYChart c = new XYChart(this.getPreferredSize().width, 100);

        // Set the plotarea with the same horizontal position as that in the main chart for alignment.
        c.setPlotArea(55, 0, c.getWidth() - 80, c.getHeight() - 1, 0xc0d8ff, -1, 0x888888,
                Chart.Transparent, 0xffffff);

        // Set the x axis stem to transparent and the label font to 10pt Arial
        c.xAxis().setColors(Chart.Transparent);
        c.xAxis().setLabelStyle("Arial", 10);

        // Put the x-axis labels inside the plot area by setting a negative label gap. Use
        // setLabelAlignment to put the label at the right side of the tick.
        c.xAxis().setLabelGap(-1);
        c.xAxis().setLabelAlignment(1);

        // Set the y axis stem and labels to transparent (that is, hide the labels)
        c.yAxis().setColors(Chart.Transparent, Chart.Transparent);

        // Add a line layer for the lines with fast line mode enabled
        LineLayer layer = c.addLineLayer();
        layer.setFastLineMode();

        // Now we add the 3 data series to a line layer, using the color red (0xff3333), green
        // (0x008800) and blue (0x3333cc)
        layer.setXData(xAxis);
        for (double[] dataArray : dataSeries) {
            layer.addDataSet(dataArray, 0xff3333);
        }

//        layer.addDataSet(dataSeriesB, 0x008800);
//        layer.addDataSet(dataSeriesC, 0x3333cc);
        // The x axis scales should reflect the full range of the view port
        c.xAxis().setDateScale(viewer.getValueAtViewPort("x", 0), viewer.getValueAtViewPort("x", 1));

        // For the automatic x-axis labels, set the minimum spacing to 75 pixels.
        c.xAxis().setTickDensity(75);

        // For the auto-scaled y-axis, as we hide the labels, we can disable axis rounding. This can
        // make the axis scale fit the data tighter.
        c.yAxis().setRounding(false, false);

        // Output the chart
        vpc.setChart(c);
    }

    //
    // Click event for the pointer.
    //
    public void pointer() {
        chartViewer1.setMouseUsage(Chart.MouseUsageScrollOnDrag);
    }

    //
    // Click event for the zoomIn.
    //
    public void zoomIn() {
        chartViewer1.setMouseUsage(Chart.MouseUsageZoomIn);
    }

    //
    // Click event for the zoomOut.
    //
    public void zoomOut() {
        chartViewer1.setMouseUsage(Chart.MouseUsageZoomOut);
    }

    //
    // A utility class to be used with JFileChooser to filter files with certain extensions.
    // This is to maintain compatibility with older versions of Java that does not built-in
    // extension filtering class.
    //
    private static class SimpleExtensionFilter extends FileFilter {

        public String ext;

        public SimpleExtensionFilter(String extension) {
            this.ext = "." + extension;
        }

        @Override
        public String getDescription() {
            return ext.substring(1);
        }

        @Override
        public boolean accept(java.io.File file) {
            return file.isDirectory() || file.getName().endsWith(ext);
        }
    }

    //
    // Save button event handler
    //
    public void saveImage() {
        String[] extensions = {"png", "jpg", "gif", "bmp", "svg", "pdf"};

        // The File Save dialog
        if (null == saveDialog) {
            saveDialog = new JFileChooser();
            for (int i = 0; i < extensions.length; ++i) {
                saveDialog.addChoosableFileFilter(new SimpleExtensionFilter(extensions[i]));
            }
            saveDialog.setAcceptAllFileFilterUsed(false);
            saveDialog.setFileFilter(saveDialog.getChoosableFileFilters()[0]);
            saveDialog.setSelectedFile(new java.io.File("chartdirector_demo"));
        }

        int status = saveDialog.showSaveDialog(null);
        if ((status == JFileChooser.APPROVE_OPTION) && (null != chartViewer1.getChart())) {
            // Add extension if the pathName does not already have one
            String pathName = saveDialog.getSelectedFile().getAbsolutePath();
            boolean hasExtension = false;
            for (int i = 0; i < extensions.length; ++i) {
                if (hasExtension = pathName.endsWith("." + extensions[i])) {
                    break;
                }
            }
            if ((!hasExtension) && (saveDialog.getFileFilter() instanceof SimpleExtensionFilter)) {
                pathName += ((SimpleExtensionFilter) saveDialog.getFileFilter()).ext;
            }

            // Issue an overwrite confirmation dialog if the file already exists
            if (new java.io.File(pathName).exists()) {
                if (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(this,
                        "File \"" + pathName + "\" already exists, confirm overwrite?",
                        "Existing File - Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, new String[]{"Yes", "No"}, "No")) {
                    return;
                }
            }

            chartViewer1.getChart().makeChart(pathName);
        }
    }

    //
    // Draw track cursor when mouse is moving over plotarea
    //
    private void chartViewer1_MouseMovedPlotArea(MouseEvent e) {
        ChartViewer viewer = (ChartViewer) e.getSource();
        trackLineLegend((XYChart) viewer.getChart(), viewer.getPlotAreaMouseX());
        viewer.updateDisplay();
    }

    //
    // Draw the track line with legend
    //
    private void trackLineLegend(XYChart c, int mouseX) {
        // Clear the current dynamic layer and get the DrawArea object to draw on it.
        DrawArea d = c.initDynamicLayer();

        // The plot area object
        PlotArea plotArea = c.getPlotArea();

        // Get the data x-value that is nearest to the mouse, and find its pixel coordinate.
        double xValue = c.getNearestXValue(mouseX);
        int xCoor = c.getXCoor(xValue);

        // Draw a vertical track line at the x-position
        d.vline(plotArea.getTopY(), plotArea.getBottomY(), xCoor, d.dashLineColor(0x000000, 0x0101));

        // Container to hold the legend entries
        ArrayList legendEntries = new ArrayList();

        // Iterate through all layers to build the legend array
        for (int i = 0; i < c.getLayerCount(); ++i) {
            Layer layer = c.getLayerByZ(i);

            // The data array index of the x-value
            int xIndex = layer.getXIndexOf(xValue);

            // Iterate through all the data sets in the layer
            for (int j = 0; j < layer.getDataSetCount(); ++j) {
                ChartDirector.DataSet dataSet = layer.getDataSetByZ(j);

                // We are only interested in visible data sets with names
                String dataName = dataSet.getDataName();
                int color = dataSet.getDataColor();
                if ((!(dataName == null || dataName.length() == 0)) && (color != Chart.Transparent)) {
                    // Build the legend entry, consist of the legend icon, name and data value.
                    double dataValue = dataSet.getValue(xIndex);
                    legendEntries.add("<*block*>" + dataSet.getLegendIcon() + " " + dataName + ": " + ((dataValue == Chart.NoValue) ? "N/A" : c.formatValue(dataValue, "{value|P4}"))
                            + "<*/*>");

                    // Draw a track dot for data points within the plot area
                    int yCoor = c.getYCoor(dataSet.getPosition(xIndex), dataSet.getUseYAxis());
                    if ((yCoor >= plotArea.getTopY()) && (yCoor <= plotArea.getBottomY())) {
                        d.circle(xCoor, yCoor, 4, 4, color, color);
                    }
                }
            }
        }

        // Create the legend by joining the legend entries
        Collections.reverse(legendEntries);
        String legendText = "<*block,maxWidth=" + plotArea.getWidth() + "*><*block*><*font=Arial Bold*>["
                + c.xAxis().getFormattedLabel(xValue, "mmm dd, yyyy") + "]<*/*>        " + Chart.stringJoin(
                legendEntries, "        ") + "<*/*>";

        // Display the legend on the top of the plot area
        TTFText t = d.text(legendText, "Arial", 8);
        t.draw(plotArea.getLeftX() + 5, plotArea.getTopY() - 3, 0x000000, Chart.BottomLeft);
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

}
