/*
Copyright (C) 2013 mc_utastar Development Team

This file is part of mc_utastar.

mc_utastar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

mc_utastar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with RoomEdit. If not, see <http://www.gnu.org/licenses/>.
*/

package core;

import gui.DataDiagram;
import uta.dataModel.DataContainer;
import java.io.*;
import java.util.LinkedList;
import java.util.logging.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.ws.handler.MessageContext;
import uta.Utastar;
import uta.dataModel.*;

/**
 * Main class of the program.
 */
public class UtaApp {
    public static Logger mlog = Logger.getLogger("Ulog");
    
    public enum ExitStat { ALL_OK, NO_DATA, INTERNAL_ERROR };

    public static void main(String[] args) {
        try {
            // log messages to a log file
            FileHandler fh = new FileHandler("Uta.log");
            fh.setFormatter(new SimpleFormatter());
            mlog.addHandler(fh);
            mlog.setLevel(Level.ALL);

            // path to a file with the data should be given as a first parameter
            // to the program or a default value is used
            String dataPath = args.length > 0 ? args[0] : "data.txt";
            BufferedReader dataFile = null;
            try {
                dataFile = new BufferedReader(new FileReader(dataPath));
            } catch (IOException e) {
                UtaApp.mlog.log(Level.SEVERE, e.getMessage());
                System.exit(ExitStat.NO_DATA.ordinal());
            }

            // get data from a file
            UtaApp.mlog.log(Level.INFO, "Reading the data from a file: ''{0}''", dataPath);
            mdata = new DataContainer();
            mdata.importFromFile(dataFile);
            dataFile.close();
            
            // log info about imported data
            LinkedList<DataModel> dt = mdata.data();
            String strSize = Integer.toString(dt.getFirst().params().size());
            UtaApp.mlog.log(Level.INFO, "Number of parameters: {0} , Number of rows of data: {1}",
                            new Object[]{strSize, Integer.toString(dt.size())});

            // run the optimization method
            UtaApp.mlog.log(Level.INFO, "Scoring alternatives...");
            mscoring = Utastar.optimize(mdata);

            // feedback to the user
            strSize = Integer.toString(mdata.rows());
            UtaApp.mlog.log(Level.INFO, "Alternatives ( {0} in total ): \n{1}", 
                            new Object[]{strSize, mdata.toString()});
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createAndShowGUI();
                }
        });
            
            
            // clean up
            fh.close();
        } catch (Exception e) {
            e.printStackTrace();
            UtaApp.mlog.log(Level.SEVERE, e.getMessage());
            System.exit(ExitStat.INTERNAL_ERROR.ordinal());
        }
    }
    
    private static void createAndShowGUI() {
        DataDiagram dd = new DataDiagram(mscoring, mdata);
        
        JFrame f = new JFrame("UTASTAR alternatives scoring");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(dd);
        f.setSize(500,300);
        f.setVisible(true);
    }
    
    private static double[] mscoring;
    private static DataContainer mdata;
}