package biz.donvi.jakesRTP.gnuPlotter;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Plotter {
    private static final Pattern PLOT_LINE_REGEX = Pattern.compile("^(plot)?\\s+\".*\"(\\s+using (\\d+(:\\d+)+) \\\\?\\s*)$");

    private static final Pattern PLOT_USING_REGEX = Pattern.compile("(\\d+(:\\d+)+)");

    private static final String DEFAULT_PLOT_FILE_LOCATION = "plot/plot.sh";

    private static final String DEFAULT_DATA_FILE_LOCATION = "plot/data.txt";

    private static final String PLOT_USING_LINE = "  \"\" using 0:0 \\";

    private static final String PLOT_SEPARATOR = "  , \\";

    private static final String PLOT_NEW_LINE = " \\";

    private final String gnuPlotLocation;

    private String plotFileLocation;

    private String dataFileLocation;

    private File dataFile;

    private File plotFile;

    private boolean dataWritten = false;

    private boolean plotFileIsNew = false;

    private boolean relativeUsing = true;

    private int distinctDataSets;

    private int[] dataOrder;

    private int[] dataWidth;

    private int[] dataStart;

    private String[] usingInOrder;

    public Plotter() {
        this("gnuplot");
    }

    public Plotter(String gnuPlotLocation) {
        this.gnuPlotLocation = gnuPlotLocation;
        useDataFile("plot/data.txt", 0);
        usePlotFile("plot/plot.sh", 0);
    }

    public Plotter useDataFile(int dataFileNumber) {
        return useDataFile("plot/data.txt", dataFileNumber);
    }

    public Plotter useDataFile(String dataFileLocation) {
        return useDataFile(dataFileLocation, 0);
    }

    public Plotter useDataFile(String dataFileLocation, int dataFileNumber) {
        this.dataFileLocation = Util.insertIdNumbers(dataFileLocation, dataFileNumber);
        this.dataFile = new File(this.dataFileLocation);
        return this;
    }

    public Plotter usePlotFile(int plotFileNumber) {
        return usePlotFile("plot/plot.sh", plotFileNumber);
    }

    public Plotter usePlotFile(String plotFileLocation) {
        return usePlotFile(plotFileLocation, 0);
    }

    public Plotter usePlotFile(String plotFileLocation, int plotFileNumber) {
        this.plotFileLocation = Util.insertIdNumbers(plotFileLocation, plotFileNumber);
        this.plotFile = new File(this.plotFileLocation);
        return this;
    }

    public Plotter writeData(double[][]... dataGrid) {
        boolean success = true;
        try {
            int[] iSizes = new int[dataGrid.length];
            this.dataOrder = new int[dataGrid.length];
            int p;
            for (p = 0; p < dataGrid.length; p++)
                iSizes[p] = (dataGrid[p]).length;
            int place;
            for (p = 0, place = 0; p < dataGrid.length; p++) {
                int size = 0;
                for (int i = 0; i < dataGrid.length; i++) {
                    if (iSizes[i] > size)
                        size = iSizes[place = i];
                }
                iSizes[place] = -1;
                this.dataOrder[p] = place;
            }
            FileWriter dataFileWriter = new FileWriter(this.dataFile);
            this.dataWidth = new int[dataGrid.length];
            for (int i : this.dataOrder)
                this.dataWidth[i] = (dataGrid[i][0]).length;
            for (int j = 0; j < (dataGrid[this.dataOrder[0]]).length; j++) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i : this.dataOrder) {
                    if ((dataGrid[i]).length - 1 >= j) {
                        if ((dataGrid[i][j]).length != this.dataWidth[i])
                            throw new RuntimeException("Data point size mismatch! (i=" + i + ", j=" + j + ", size=" + (dataGrid[i][j]).length + ", size should be " + this.dataWidth[i] + ")");
                        for (int k = 0; k < this.dataWidth[i]; k++)
                            stringBuilder.append(dataGrid[i][j][k]).append(' ');
                    }
                }
                dataFileWriter.write(stringBuilder.append('\n').toString());
            }
            dataFileWriter.close();
            this.dataStart = new int[dataGrid.length];
            int start = 1;
            for (int i : this.dataOrder) {
                this.dataStart[i] = start;
                start += this.dataWidth[i];
            }
            this.distinctDataSets = dataGrid.length;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        this.dataWritten = success;
        return this;
    }

    public void plot() {
        plot(false);
    }

    public void plot(boolean updatePlotFile) {
        try {
            updatePlotFile = (updatePlotFile && updatePlotFile());
            String commandLineArgs = this.gnuPlotLocation + " --persist " + Util.withQuotes(Util.useForwardSlashes(this.plotFile.getAbsolutePath()));
            System.out.println(Util.Color.YELLOW + "[GnuPlotter] Hit plotting step! Running command the following in the commandline:\n" + Util.Color.MAGENTA + commandLineArgs + Util.Color.YELLOW + (updatePlotFile ? "\n[GnuPlotter] Plot file edited." : "") + Util.Color.RESET);
            Process gnuPlot = Runtime.getRuntime().exec(commandLineArgs);
            synchronized (gnuPlot) {
                try {
                    gnuPlot.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.printf(Util.Color.YELLOW + "[GnuPlotter] GnuPlot %s%n" + Util.Color.RESET, new Object[] { gnuPlot.isAlive() ? "successfully launched." : "failed to launch" });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean updatePlotFile() {
        setUsingInOrder();
        ArrayList<String> lines = new ArrayList<>();
        boolean changes = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.plotFile))) {
            String line;
            for (; (line = reader.readLine()) != null; lines.add(line)) {
                String newLine = editIfPlotLine(line);
                if (newLine != null) {
                    line = newLine;
                    changes = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (changes)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.plotFile))) {
                writer.write(plotUVars());
                for (String line : lines)
                    writer.write(line + '\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        return changes;
    }

    private String editIfPlotLine(String line) {
        Matcher matcher = PLOT_LINE_REGEX.matcher(line);
        if (matcher.matches())
            return this.plotFileIsNew ?
                    getDefaultPlotLayout() : (line
                    .substring(0, line.indexOf("\"")) + this.dataFileLocation + matcher.group(2));
        return null;
    }

    private String getDefaultPlotLayout() {
        StringBuilder newLineBuilder = new StringBuilder();
        for (int i = 0; i < this.distinctDataSets; i++) {
            newLineBuilder
                    .append(generatedPlotLine(i))
                    .append('\n').append("  title \"Dataset ").append(i + 1).append("\" \\");
            if (i < this.dataOrder.length - 1)
                newLineBuilder.append('\n').append("  , \\").append('\n');
        }
        this.plotFileIsNew = false;
        return newLineBuilder.toString();
    }

    private String plotUVars() {
        StringBuilder usingVars = new StringBuilder("# Using Placeholders:\n");
        for (int i = 0; i < this.distinctDataSets; i++) {
            for (int j = 0; j < this.dataWidth[i]; j++)
                usingVars
                        .append("u")
                        .append(i + 1)
                        .append((char)(j + 97))
                        .append(" = ")
                        .append(this.dataStart[i] + j)
                        .append('\n');
        }
        return usingVars.append("# End Section [Using Placeholders]\n").toString();
    }

    private void setUsingInOrder() {
        ArrayList<String> usings = new ArrayList<>();
        for (int i = 0; i < this.distinctDataSets; i++) {
            StringBuilder singleUsing = (new StringBuilder()).append("u").append(i + 1).append('a');
            for (int j = 1; j < this.dataWidth[i]; j++)
                singleUsing.append(":").append("u").append(i + 1).append((char)(j + 97));
            usings.add(singleUsing.toString());
        }
        this.usingInOrder = usings.<String>toArray(new String[0]);
    }

    private String generatedPlotLine(int i) {
        return generatedPlotLine(i, "  \"\" using 0:0 \\");
    }

    private String generatedPlotLine(int i, String startingLine) {
        StringBuilder usingColumns;
        if (this.relativeUsing) {
            usingColumns = new StringBuilder(this.usingInOrder[i]);
        } else {
            usingColumns = (new StringBuilder()).append(this.dataStart[i]);
            for (int j = 1; j < this.dataWidth[i]; j++)
                usingColumns.append(':').append(this.dataStart[i] + j);
        }
        return startingLine
                .replaceAll(PLOT_USING_REGEX
                        .pattern(), usingColumns
                        .toString())
                .replaceAll("\"\"",

                        Util.withQuotes(this.dataFileLocation));
    }
}
