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

import java.util.LinkedList;

/**
 * Represents single row of params
 */
public class DataModel {
    
    public DataModel() {
        mparams = new LinkedList<DataParam>();
    }
    
    /**
     * Parse string with values of data and associate those values
     * to names provided in the first param.
     * 
     * @param names Names of params
     * @param directions Direction of optimization of a value
     * @param strVal String which contains values of params
     */
    public void importData(LinkedList<String> names, LinkedList<DataParam.OptDirection> directions, String strVal) {
        String strNumb = "";
        int paramNumb = 0;
        for(int pos = 0; pos < strVal.length(); ++pos) {
            char c = strVal.charAt(pos);
            
            if(Character.isDigit(c) || c == '.') {
                strNumb += c;
                continue;
            }
            
            if((Character.isWhitespace(c) || c == '|') && strNumb.length() > 0) {
                add(names.get(paramNumb), strNumb, directions.get(paramNumb++));
                strNumb = "";
            }
        }
        
        if(strNumb.length() > 0) {
            add(names.get(paramNumb), strNumb, directions.get(paramNumb++));
        }
    }
    
    public void add(String label, String strNumb, DataParam.OptDirection direction) {
        double numb = Double.valueOf(strNumb);
        mparams.add(new DataParam(label, numb, direction));
    }
    
    public LinkedList<DataParam> params() {
        return mparams;
    }
    
    public int size() {
        return mparams.size();
    }
    
    @Override
    public String toString() {
        String str = "";
        for(DataParam param : mparams) {
            str += (param + ", ");
        }

        // get rid of the last ','
        return str.substring(0, str.length() - 2);
    }
    
    private LinkedList<DataParam> mparams;
}
