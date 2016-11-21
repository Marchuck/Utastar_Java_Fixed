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

package uta.dataModel;

import core.UtaApp;
import core.UtaException;
import java.io.*;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * Container for rows of data
 */
public class DataContainer {
    
    public DataContainer() {
        mdata = new LinkedList<DataModel>();
    }
    
    public DataContainer(LinkedList<DataModel> data) {
        mdata = data;
    }
    
    public LinkedList<DataModel> data() {
        return mdata;
    }
    
    public int rows() {
        return mdata.size();
    }
    
    public int columns() {
        if(mdata.size() == 0) {
            return 0;
        }
        return mdata.getFirst().size();
    }
    
    public void importFromFile(BufferedReader dataFile) throws IOException, UtaException {
        // Lines which start with '#' are ignored
        // Line which starts with '$' contains labels for parameters
        // Line which starts with '@' contains information, whether value should be maximized or minimized
        // Values are separated by '|', whitespaces are ignored unless they are part of a label
        
        String line = dataFile.readLine();
        LinkedList<String> labels = new LinkedList<String>();
        LinkedList<DataParam.OptDirection> directions = new LinkedList<DataParam.OptDirection>();
        while(line != null) {
            // ignore empty lines
            if(line.length() >= 1) {
                for(int pos = 0; pos < line.length(); ++pos) {
                    char c = line.charAt(pos);
                    if(Character.isWhitespace(c)) {
                        // ignore whitespace
                        continue;
                    }
                    
                    if(c == '#') {
                        // skip comment, get new line
                        break;
                    }
                    if(c == '@') {
                        String strDir = "";
                        for(++pos; pos < line.length(); ++pos) {
                            c = line.charAt(pos);
                            if(c == '|') {
                                directions.add(DataParam.OptDirection.valueOf(strDir));
                                strDir = "";
                            } else if(Character.isWhitespace(c)) {
                                // skip whitespace
                                continue;
                            } else {
                                strDir += c;
                            }
                        }
                        if(strDir.length() > 0) {
                            directions.add(DataParam.OptDirection.valueOf(strDir));
                        }
                        // get new line
                        break;
                    }
                    
                    if(c == '$') {
                        // read labels
                        String label = "";
                        for(++pos; pos < line.length(); ++pos) {
                            c = line.charAt(pos);
                            if(c == '|') {
                                labels.add(label);
                                label = "";
                            } else {
                                label += c;
                            }
                        }
                        if(label.length() > 0) {
                            labels.add(label);
                        }
                        // get new line
                        break;
                    }
                    
                    if(Character.isDigit(c)) {
                        if(labels.size() == 0 || directions.size() == 0) {
                            String strLabSize = String.valueOf(labels.size());
                            String strDirSize = String.valueOf(directions.size());
                            throw new UtaException("Labels (" + strLabSize + ") and directions (" + strDirSize + ") of parameters need to be defined before values");
                        }
                        // this is a row with the data
                        DataModel dataMod = new DataModel();
                        dataMod.importData(labels, directions, line);
                        mdata.add(dataMod);
                        // get new line
                        break;
                    }
                }
            }
            
            line = dataFile.readLine();
        }
        
        if(mdata.size() <= 0) {
            throw new UtaException("Data file is empty");
        }
        if(mdata.getFirst().params().size() <= 0) {
            throw new UtaException("No parameters?");
        }
    }
    
    @Override
    public String toString() {
        String str = "";
        int row = 1;
        for(DataModel dataMod : mdata) {
            str += ("\t" + Integer.toString(row++) + ") " + dataMod + "\n");
        }
        return str;
    }
    
    private LinkedList<DataModel> mdata;
}
